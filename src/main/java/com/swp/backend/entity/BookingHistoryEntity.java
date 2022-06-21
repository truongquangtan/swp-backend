package com.swp.backend.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "booking_history")
@Getter
@Setter
@Builder
public class BookingHistoryEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "booking_id")
    private String bookingId;
    @Column(name = "reference", insertable = false)
    private int reference;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "note")
    private String note;
    @Column(name = "booking_status")
    private String bookingStatus;
}
