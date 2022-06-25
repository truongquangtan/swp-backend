package com.swp.backend.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "vouchers")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VoucherEntity {
    @Id
    @Column(name = "id")
    private String id;
    @Column(name = "type")
    private String type;
    @Column(name = "title")
    private String title;
    @Column(name = "description")
    private String description;
    @Column(name = "is_deleted")
    @Builder.Default
    private boolean delete = false;
    @Column(name = "max_quantity")
    private int maxQuantity;
    @Column(name = "remainder_quantity")
    private int remainder;
    @Column(name = "percent_discount")
    private Integer percentDiscount;
    @Column(name = "percent_amount_discount_upto")
    private Integer percentDiscountUpto;
    @Column(name = "amount_least")
    private Integer amountLeast;
    @Column(name = "amount_discount")
    private Integer amountDiscount;
    @Column(name = "start_date")
    private Timestamp startDate;
    @Column(name = "end_date")
    private Timestamp endDate;
    @Column(name = "ref_yard_id")
    private String reference;
    @Column(name = "created_by_account_id")
    private String createdByAccountId;
    @Column(name = "created_at")
    private Timestamp createdAt;
}
