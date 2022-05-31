package com.boongdev.atdd.membership.domain.membership.dto;

import com.boongdev.atdd.membership.domain.membership.MembershipType;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor
public class MembershipDetailResponse {
    private final Long id;
    private final MembershipType membershipType;
    private final Integer point;
    private final LocalDateTime createAt;
}
