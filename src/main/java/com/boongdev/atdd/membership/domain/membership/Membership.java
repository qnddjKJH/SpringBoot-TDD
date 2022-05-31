package com.boongdev.atdd.membership.domain.membership;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Membership {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private MembershipType membershipType;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer point;

    @Column(nullable = false)
    private String userId;

    @CreationTimestamp
    @Column(nullable = false, updatable = false, length = 20)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, length = 20)
    private LocalDateTime updatedAt;

    public void accumulatePoint(int point) {
        this.point += point;
    }
}
