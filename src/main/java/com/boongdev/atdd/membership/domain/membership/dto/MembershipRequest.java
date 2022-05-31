package com.boongdev.atdd.membership.domain.membership.dto;

import com.boongdev.atdd.membership.domain.membership.MembershipType;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipRequest {

    @NotNull
    @Min(0)
    private Integer point;

    @NotNull
    private MembershipType membershipType;
}
