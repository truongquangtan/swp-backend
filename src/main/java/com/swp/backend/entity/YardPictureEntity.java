package com.swp.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "yard_picture")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class YardPictureEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "ref_id")
    private String refId;
    @Column(name = "image")
    private String image;
}
