package com.pro.infomate.Board.service;


import com.pro.infomate.Board.Repository.PostRepository;
import com.pro.infomate.Board.dto.PostDTO;
import com.pro.infomate.Board.entity.Post;
import com.pro.infomate.member.dto.MemberDTO;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BoardService {

//    private final BoardRepository boardRepository;

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public BoardService(PostRepository postRepository, ModelMapper modelMapper) {
//        this.boardRepository = boardRepository;
        this.postRepository = postRepository;
        this.modelMapper = modelMapper;

    }

//    public Board getBoardById(int boardCode) {
//        return boardRepository.findById(boardCode)
//                .orElseThrow(() -> new EntityNotFoundException("Board not found with id: " + boardCode));
//    }

    public List<PostDTO> getAllBoards() {   // 게시판 목록 조회
        List<Post> postList = postRepository.findAll();

        return postList.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .map(postDTO -> {

                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setMemberId(postDTO.getMember().getMemberId());
                    memberDTO.setMemberName(postDTO.getMember().getMemberName());
                    memberDTO.setMemberNo(postDTO.getMember().getMemberNo());

                    postDTO.setMember(memberDTO);

                    return postDTO;

                })
                .collect(Collectors.toList());
    }


    @Transactional
    public String postPost(PostDTO postDTO) {   // 새 글 작성

//        String imageName = UUID.randomUUID().toString().replace("-", "");
//        String replaceFileName = null;
        int result = 0;

        try {
            Post postPost = modelMapper.map(postDTO, Post.class);

            postRepository.save(postPost);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        log.info("[PostService] postPost End ====================");
        return (result > 0) ? "게시글 등록 성공" : "게시글 입력 실패";

    }



    public List<PostDTO> boardNotice() {    // 공지사항

        List<Post> boardNotice = postRepository.findAllByBoardCode(101);
//        List<Post> boardNotice = postRepository.findByBoardCode(101);

//        for(int i = 0 ; i < boardNotice.size() ; i++) {
//            boardNotice.get(i).setPostImageUrl(IMAGE_URL + boardNotice.get(i).getPostImageUrl());

        return boardNotice.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .map(postDTO -> {

                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setMemberId(postDTO.getMember().getMemberId());
                    memberDTO.setMemberName(postDTO.getMember().getMemberName());
                    memberDTO.setMemberNo(postDTO.getMember().getMemberNo());

                    postDTO.setMember(memberDTO);

                    return postDTO;

                })
                .collect(Collectors.toList());
    }




    public List<PostDTO> boardCommon() {    // 일반게시판

//        List<Post> boardCommon = postRepository.findByBoardId(102);
        List<Post> boardCommon = postRepository.findAllByBoardCode(102);

//        for(int i = 0 ; i < boardCommon.size() ; i++) {
//            boardCommon.get(i).setPostImageUrl(IMAGE_URL + boardCommon.get(i).getPostImageUrl());

        return boardCommon.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .map(postDTO -> {

                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setMemberId(postDTO.getMember().getMemberId());
                    memberDTO.setMemberName(postDTO.getMember().getMemberName());
                    memberDTO.setMemberNo(postDTO.getMember().getMemberNo());

                    postDTO.setMember(memberDTO);

                    return postDTO;

                })
                .collect(Collectors.toList());
    }



    public List<PostDTO> boardAnony() {    // 익명게시판

//        List<Post> boardAnony = postRepository.findByBoardId(103);
        List<Post> boardAnony = postRepository.findAllByBoardCode(103);

//        for(int i = 0 ; i < boardAnony.size() ; i++) {
//            boardAnony.get(i).setPostImageUrl(IMAGE_URL + boardAnony.get(i).getPostImageUrl());

        return boardAnony.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .map(postDTO -> {

                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setMemberId(postDTO.getMember().getMemberId());
                    memberDTO.setMemberName(postDTO.getMember().getMemberName());
                    memberDTO.setMemberNo(postDTO.getMember().getMemberNo());

                    postDTO.setMember(memberDTO);

                    return postDTO;

                })
                .collect(Collectors.toList());
    }



    public List<PostDTO> boardDept() {    // 부서게시판

//        List<Post> boardDept = postRepository.findByBoardId(104);
        List<Post> boardDept = postRepository.findAllByBoardCode(104);

//        for(int i = 0 ; i < boardDept.size() ; i++) {
//            boardDept.get(i).setPostImageUrl(IMAGE_URL + boardDept.get(i).getPostImageUrl());

        return boardDept.stream()
                .map(post -> modelMapper.map(post, PostDTO.class))
                .map(postDTO -> {

                    MemberDTO memberDTO = new MemberDTO();
                    memberDTO.setMemberId(postDTO.getMember().getMemberId());
                    memberDTO.setMemberName(postDTO.getMember().getMemberName());
                    memberDTO.setMemberNo(postDTO.getMember().getMemberNo());

                    postDTO.setMember(memberDTO);

                    return postDTO;

                })
                .collect(Collectors.toList());
    }
}