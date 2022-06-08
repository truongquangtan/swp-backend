package com.swp.backend.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "type_yards")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TypeYard {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "type_name")
    private String typeName;
}
