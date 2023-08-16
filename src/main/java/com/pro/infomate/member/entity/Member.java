package com.pro.infomate.member.entity;

import com.pro.infomate.calendar.entity.Calendar;
import com.pro.infomate.calendar.entity.FavoriteCalendar;
import com.pro.infomate.calendar.entity.Participant;
import io.github.classgraph.PackageInfo;
import lombok.*;
import com.pro.infomate.approval.entity.Approval;
import com.pro.infomate.approval.entity.DocMemberRef;
import com.pro.infomate.approval.entity.Document;
import javax.persistence.*;
import java.util.List;

import java.sql.Timestamp;

@Entity
@Table(name = "TBL_MEMBER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@SequenceGenerator(
        name = "MEMBER_CODE_GENERATOR",
        sequenceName = "SEQ_TBL_MEMBER_MEMBER_CODE",
        initialValue = 1,
        allocationSize = 1
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MEMBER_CODE_GENERATOR")
    @Column(name = "MEMBER_CODE")
    private int memberCode;

    @Column(name = "MEMBER_PWD")
    private String memberPassword;

    @Column(name = "MEMBER_NAME")
    private String memberName;

    @Column(name = "MEMBER_ID")
    private String memberId;

    @Column(name = "MEMBER_EMAIL")
    private String memberEmail;

    @Column(name = "MEMBER_PHONE")
    private String memberPhone;

    @Column(name = "MEMBER_NO")
    private String memberNo;

    @Column(name = "MEMBER_STATUS")
    private String memberStatus;

    @Column(name = "EXTENSION_NUMBER")
    private String extensionNumber;

    @Column(name = "MEMBER_ADDRESS")
    private String memberAddress;

    @Column(name = "HIRE_DATE")
    private Timestamp hireDate;

    @Column(name = "DEPT_CODE")
    private int deptCode;

    @Column(name = "MEMBER_PIC")
    private String memberPic;

    @Column(name = "RANK_CODE")
    private int rankCode;

    @Column(name = "MEMBER_OFF")
    private int memberOff;

    @OneToMany(mappedBy = "member")
    private List<DocMemberRef> memberRefList;

    @OneToMany(mappedBy = "member")
    private List<Approval> approvalList;

    @OneToMany(mappedBy = "member")
    private List<Document> documentList;

    @OneToMany(mappedBy = "member")
    private List<FavoriteCalendar> favoriteCalendarList;

    @OneToMany(mappedBy = "member")
    private List<Calendar> calendarList;

    @OneToMany(mappedBy = "member")
    private List<Participant> participantList;

}
