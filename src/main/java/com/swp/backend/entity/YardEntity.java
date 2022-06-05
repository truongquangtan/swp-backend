package com.swp.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.time.LocalTime;

@Entity
@Table(name = "yards")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YardEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "create_at")
    private Timestamp createAt;
    @Column(name = "address")
    private String address;
    @Column(name = "district_id")
    private int districtId;
    @Column(name = "is_active")
    private boolean active;
    @Column(name = "is_deleted")
    private boolean deleted;
    @Column(name = "is_approved")
    private boolean approved;
    @Column(name = "owner_id")
    private String ownerId;
    @Column(name = "open_at")
    private LocalTime openAt;
    @Column(name = "close_at")
    private LocalTime closeAt;
    @Column(name = "slot_duration")
    private int slotDuration;
}
