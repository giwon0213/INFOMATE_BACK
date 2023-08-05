package com.pro.infomate.calendar.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ParticipantPK implements Serializable {

    private int schedule;

    private int memberCode;
}