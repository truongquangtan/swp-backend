package com.swp.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VoteModel {
    private String bookingId;
    private String voteId;
    private String yardName;
    private String typeName;
    private String address;
    private String startTime;
    private String endTime;
    private String date;
    private Integer score;
    private String comment;
}
