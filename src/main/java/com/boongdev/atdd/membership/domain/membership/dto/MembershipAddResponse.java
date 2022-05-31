package com.boongdev.atdd.membership.domain.membership.dto;

import com.boongdev.atdd.membership.domain.membership.MembershipType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MembershipAddResponse {
    private final Long id;
    private final MembershipType membershipType;
}
