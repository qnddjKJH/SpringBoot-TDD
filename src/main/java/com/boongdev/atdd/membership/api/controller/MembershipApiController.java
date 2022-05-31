package com.boongdev.atdd.membership.api.controller;

import com.boongdev.atdd.membership.domain.membership.MembershipConstant;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipDetailResponse;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipRequest;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipAddResponse;
import com.boongdev.atdd.membership.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.util.List;

import static com.boongdev.atdd.membership.domain.membership.MembershipConstant.USER_ID_HEADER;

@RestController
@RequiredArgsConstructor
public class MembershipApiController {

    private final MembershipService membershipService;

    @GetMapping("/api/v1/memberships/{id}")
    public ResponseEntity<MembershipDetailResponse> getMembership(
            @RequestHeader(USER_ID_HEADER) final String userId,
            @PathVariable Long id) {
        return ResponseEntity.ok(membershipService.getMembership(id, userId));
    }

    @GetMapping("/api/v1/memberships")
    public ResponseEntity<List<MembershipDetailResponse>> getMemberships(
            @RequestHeader(USER_ID_HEADER) final String userId) {
        return ResponseEntity.ok(membershipService.getMembershipList(userId));
    }

    @PostMapping("/api/v1/memberships")
    public ResponseEntity<MembershipAddResponse> addMembership(
            @RequestHeader(USER_ID_HEADER) String userId,
            @RequestBody @Valid MembershipRequest membershipRequest) {

        MembershipAddResponse membershipResponse = membershipService.addMembership(userId, membershipRequest.getPoint(), membershipRequest.getMembershipType());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(membershipResponse);
    }

    @DeleteMapping("/api/v1/memberships/{id}")
    public ResponseEntity<Void> deleteMembership(
            @RequestHeader(USER_ID_HEADER) String userId,
            @PathVariable Long id) {
        membershipService.removeMembership(id, userId);
        return ResponseEntity.noContent().build();
    }


}
