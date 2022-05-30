package com.swp.backend.entity;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name="branches")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BranchEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "address")
    private String address;
    @Column(name = "province")
    private String province;
    @Column(name = "district")
    private String district;
    @Column(name = "branch_name")
    private String branchName;
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}
