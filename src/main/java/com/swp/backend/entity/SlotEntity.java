package com.swp.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalTime;

@Entity
@Table(name = "slots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotEntity {
    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "is_active")
    private boolean isActive;
    @Column(name = "ref_yard")
    private String refYard;
    @Column(name = "price")
    private int price;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
}
