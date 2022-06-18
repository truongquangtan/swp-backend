package com.swp.backend.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking")
public class BookingEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "account_id")
    private String accountId;
    @Column(name = "slot_id")
    private int slotId;
    @Column(name = "status")
    private String status;
    @Column(name = "date")
    private Timestamp date;
    @Column(name = "note")
    private String note;
    @Column(name = "price")
    private int price;
    @Column(name = "book_at")
    private Timestamp bookAt;
    @Column(name = "reference", insertable = false)
    private long reference;
}