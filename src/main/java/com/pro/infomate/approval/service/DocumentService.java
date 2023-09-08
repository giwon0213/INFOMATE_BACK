package com.pro.infomate.approval.service;

import com.pro.infomate.approval.dto.DocumentDTO;
import com.pro.infomate.approval.dto.request.*;
import com.pro.infomate.approval.dto.response.*;
import com.pro.infomate.approval.entity.*;
import com.pro.infomate.approval.repository.*;
import com.pro.infomate.approval.service.visitor.DocumentToDTOVisitor;
import com.pro.infomate.approval.dto.response.VacationResponse;
import com.pro.infomate.exception.NotEnoughDateException;
import com.pro.infomate.exception.NotFindDataException;
import com.pro.infomate.member.entity.Member;
import com.pro.infomate.member.repository.MemberRepository;
import com.pro.infomate.util.FileUploadUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {

  private final DocumentRepository<Document> documentRepository;
  private final DocumentFileRepository documentFileRepository;
  private final MemberRepository memberRepository;

  private final ApprovalRepository approvalRepository;
  private final DocRefRepository docRefRepository;


  private final DocumentToDTOVisitor visitor;

  private final ModelMapper modelMapper;

  @Value("${files.files-dir}")
  private String FILES_DIR;
  @Value("${files.files-url}")
  private String FILES_URL;

  //1. 문서 저장
  @Transactional
  public <T extends DocumentRequest, R extends DocumentDetailResponse> R saveDocument(
          int memberCode, T documentRequest, List<MultipartFile> multipartFiles, Class<? extends Document> documentClass, Class<R> responseClass) {

    Member member = memberRepository.findById(memberCode).orElseThrow(() -> new NotFindDataException("회원정보가 없습니다"));

    Document document = createDocument(member, documentRequest, documentClass);
    Document save = documentRepository.save(document);

    createRefer(documentRequest, save);
    createApprovals(documentRequest, member, save,memberCode);
    processFiles(multipartFiles, save);

    return modelMapper.map(save, responseClass);
  }

  //2. 문서 세부
  @Transactional
  public DocumentDetailResponse findById(long documentId, int memberCode) {

    Member nowMember = memberRepository.findByMemberCode(memberCode);

    Document document = documentRepository.findById(documentId)
            .orElseThrow(() -> new NotFindDataException("해당문서가 없습니다."));

    // 결재여부
    List<Approval> approvalList = document.getApprovalList();
    approvalList.sort(Comparator.comparingInt(Approval::getOrder));

    Approval approval = approvalList.stream().filter(app -> app.getApprovalDate() == null)
            .findFirst().orElse(null);

    boolean allApprovalDatesNull = approvalList.stream()
            .allMatch(app -> app.getApprovalDate() == null);

    DocumentCondition condition = DocumentCondition.builder()
            .isDept(document.getMember().getDepartment().equals(nowMember.getDepartment()))
            .isCredit(approval != null && approval.getMember().getMemberCode() == memberCode)
            .isRemove(document.getMember().equals(nowMember) && allApprovalDatesNull)
            .build();

  // 취소할수있을때는 apporval이 전부가 Wating일때
    //삭제는 전부 Wating이거나 REJECT당했을때


    DocumentDetailResponse result = document.accept(visitor);
    result.setCondition(condition);

    return result;

  }

  //3. 문서 리스트 top 5개 main
  public ApprovalHomeResponse top5List(int memberCode){
    Member member = memberRepository.findById(memberCode).orElseThrow(() -> new NotFindDataException("회원정보가 없습니다"));
    //일단...
    PageRequest pageRequest = PageRequest.of(0, 5);

    //내 기안문서
    List<Document> approvalList = documentRepository.findTop5ByMemberOrderByCreatedDateDesc(member);

    //내 참조문서
    Page<DocumentListResponse> refList = docRefRepository.refPagingList(null, memberCode, pageRequest);

    //내 결재 대기문서
    List<Document> documents = documentRepository.findApprovalsDocument(memberCode);

    List<Document> approvedDocuments = documents.stream()
            .map(approvalRepository::findTopByDocumentAndApprovalDateIsNullOrderByOrderAsc)
            .filter(approval -> approval != null && approval.getMember().getMemberCode() == memberCode)
            .map(Approval::getDocument)
            .filter(document -> document.getDocumentStatus() == DocumentStatus.WAITING )
            .limit(5)
            .collect(Collectors.toList());

    List<DocumentListResponse> creditList = approvedDocuments.stream().map(DocumentListResponse::new).collect(Collectors.toList());

    return ApprovalHomeResponse
            .builder()
            .approvalList(approvalList.stream().map(DocumentListResponse::new).collect(Collectors.toList()))
            .refList(refList.getContent())
            .creditList(creditList)
            .build();
  }

  //4. 기안문서 페이징
  public Page<DocumentListResponse> approvalList(String status, int memberCode, Pageable pageable){

    Member member = memberRepository.findById(memberCode).orElseThrow(() -> new NotFindDataException("회원정보가 없습니다"));

    return documentRepository.findAllApproval(status, memberCode, pageable);

  }

  //5. 결재 대기 페이징
  public Page<DocumentListResponse> creditList(int memberCode, Pageable pageable) {
    List<Document> documents = documentRepository.findApprovalsDocument(memberCode);

    List<Document> approvedDocuments = documents.stream()
            .map(approvalRepository::findTopByDocumentAndApprovalDateIsNullOrderByOrderAsc)
            .filter(approval -> approval != null && approval.getMember().getMemberCode() == memberCode)
            .map(Approval::getDocument)
            .filter(document -> document.getDocumentStatus() == DocumentStatus.WAITING )
            .collect(Collectors.toList());

    Page<Document> creditPaging = new PageImpl<>(approvedDocuments, pageable, approvedDocuments.size());

    return creditPaging.map(DocumentListResponse::new);
  }

  // 메인대시보드용
  public MainCreditResponse mainCredit(int memberCode){
    List<Document> documents = documentRepository.findApprovalsDocument(memberCode);

    int approvalCount = documentRepository.findByMemberDocuments(memberCode);


    int countBeforeLimit = (int)documents.stream()
            .map(approvalRepository::findTopByDocumentAndApprovalDateIsNullOrderByOrderAsc)
            .filter(approval -> approval != null && approval.getMember().getMemberCode() == memberCode)
            .map(Approval::getDocument)
            .filter(document -> document.getDocumentStatus() == DocumentStatus.WAITING)
            .count();

    List<Document> approvedDocuments = documents.stream()
            .map(approvalRepository::findTopByDocumentAndApprovalDateIsNullOrderByOrderAsc)
            .filter(approval -> approval != null && approval.getMember().getMemberCode() == memberCode)
            .map(Approval::getDocument)
            .filter(document -> document.getDocumentStatus() == DocumentStatus.WAITING )
            .limit(2)
            .collect(Collectors.toList());

    List<DocumentListResponse> creditList = approvedDocuments.stream().map(DocumentListResponse::new).collect(Collectors.toList());

    MainCreditResponse mainResp = MainCreditResponse.builder()
            .approvalCount(approvalCount)
            .doneList(0)
            .creditCount(countBeforeLimit)
            .creditList(creditList)
            .build();

    return mainResp;
  }


  //6. 부서문서
  public Page<DocumentListResponse> deptList(int memberCode, Pageable pageable) {

    Page<DocumentListResponse> deptResult = documentRepository.findByDeptDoc(memberCode, pageable);

    return new PageImpl<>(deptResult.getContent(), pageable, deptResult.getTotalElements());
  }

  //7. 문서삭제
  @Transactional
  public void deleteDocument(long documentId){

    documentRepository.findById(documentId).orElseThrow(() -> new NotFindDataException("해당문서가 없습니다"));
    documentRepository.deleteById(documentId);
  }

  //8. 결재 취소
//  @Transactional
//  public void cancelApproval(Long documentId){
//    Document document = documentRepository.findById(documentId).orElseThrow(() -> new NotFindDataException("해당문서가 없습니다"));
//
//    document.getApprovalList().;
//
//  }


  //문서 임시저장
  public <T extends DocumentRequest> void tempSave(Long documentCode, T documentRequest ){


    // 1. 임시저장 시 documentCode가 없으면 저장되고
    // 값들을 받아서 저장하는데 문서상태는 temporary, 결재리스트는 waiting인 상태로

    //


    //2. 임시저장시 documentCode가 있으면 해당내용을 업데이트 시킨다.
    // 결재 리스트와 참조문서가 수정되었을 수 있기 때문에 approval, ref를 지우고 새롭게 들어온 값을 insert 하는 로직

  }

 //문서 만들기
  private <T extends DocumentRequest, E extends Document> E createDocument(Member member, T documentRequest, Class<E> entityClass) {

    E document = modelMapper.map(documentRequest, entityClass);

    document.addMember(member);
    document.setCreatedDate(LocalDateTime.now());
    return document;
  }

  //참조 저장
  private void createRefer(DocumentRequest documentRequest, Document save) {
    if(documentRequest.getRefList() == null) return;

    List<Integer> result = documentRequest.getRefList().stream().map(RefRequest::getId).collect(Collectors.toList());
    List<Member> memberList = memberRepository.findByMemberCodeIn(result);

    memberList.forEach(m -> {
      DocRef ref = DocRef.builder().document(save).member(m).build();
      docRefRepository.save(ref);
    });
  }

  // 결재 리스트 저장
  private void createApprovals(DocumentRequest documentRequest, Member member, Document save, int memberCode) {
    if (save.getApprovalList() == null) {
      Approval approval = Approval.builder().order(1).member(member).document(save).build();
      approval.setApprovalStatus(ApprovalStatus.APPROVAL);
      Approval savedApproval = approvalRepository.save(approval);
      savedApproval.setApprovalDate(save.getCreatedDate());
      save.setDocumentStatus(DocumentStatus.APPROVAL);
    } else {
      List<Approval> approvals = documentRequest.getApprovalList()
              .stream()
              .map(list -> {
                Member byMemberId = memberRepository.findByMemberCode(list.getId());
                Approval approval = Approval.builder()
                        .order(list.getOrder())
                        .member(byMemberId)
                        .document(save)
                        .build();
                if (byMemberId.getMemberCode() == memberCode) {
                  approval.setApprovalStatus(ApprovalStatus.APPROVAL);
                  approval.setApprovalDate(LocalDateTime.now());
                }
                return approval;
              })
              .collect(Collectors.toList());
      approvalRepository.saveAll(approvals);

      if (approvals.size() == 1 && approvals.get(0).getMember().getMemberCode() == memberCode) {
        save.setDocumentStatus(DocumentStatus.APPROVAL);
      }
    }
  }

  // 파일 저장
  private void processFiles(List<MultipartFile> multipartFiles, Document save) {

    if(multipartFiles == null) return;

    List<DocFileResponse> files = null;
    try {
      files = FileUploadUtils.saveMultiFiles(FILES_DIR, multipartFiles);
      List<DocumentFile> fileList = files.stream().map(file -> new DocumentFile(file, save)).collect(Collectors.toList());
      documentFileRepository.saveAll(fileList);

    } catch (Exception e) {
      FileUploadUtils.deleteMultiFiles(FILES_DIR, files);
      throw new RuntimeException("파일업로드 실패");
    }

  }


  private DocumentDetailResponse mapDocumentToDTO(Document document) {
    if (document instanceof Vacation) {
      return modelMapper.map((Vacation) document, VacationResponse.class);
    } else if (document instanceof Payment) {
      return modelMapper.map((Payment) document, PaymentResponse.class);
    } else if (document instanceof Draft) {
      return modelMapper.map((Draft) document, DraftResponse.class);
      // 다른 문서 유형에 대한 처리 추가 가능
    } else {
      throw new IllegalArgumentException("지원하지 않는 문서 유형입니다.");
    }
  }



}