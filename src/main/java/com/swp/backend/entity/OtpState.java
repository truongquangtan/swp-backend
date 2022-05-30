package com.swp.backend.entity;

import com.swp.backend.utils.DateHelper;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "otp_state")
public class OtpState {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "otp_code")
    private String otpCode;
    @Column(name = "expire_at")
    private Timestamp expireAt;
    @Column(name = "create_at")
    @Builder.Default
    private Timestamp createAt = DateHelper.getTimestampAtZone(DateHelper.VIETNAM_ZONE);
}
