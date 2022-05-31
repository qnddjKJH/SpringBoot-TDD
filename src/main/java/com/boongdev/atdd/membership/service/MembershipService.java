package com.boongdev.atdd.membership.service;

import com.boongdev.atdd.membership.domain.membership.Membership;
import com.boongdev.atdd.membership.domain.membership.MembershipType;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipAddResponse;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipDetailResponse;
import com.boongdev.atdd.membership.exception.MembershipErrorResult;
import com.boongdev.atdd.membership.exception.MembershipException;
import com.boongdev.atdd.membership.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MembershipService {

    private final MembershipRepository membershipRepository;

    // 하도 안써서 까먹고 있었는데 변수명으로 자동으로 의존성 주입해준다.
    // interface 는 PointService 지만 구현체인 RatePointService 로 초기화 된다.
    private final PointService ratePointPointService;


    public MembershipAddResponse addMembership(String userId, Integer point, MembershipType membershipType) {
        Membership findMembership = membershipRepository.findByUserIdAndMembershipType(userId, membershipType);
        if (findMembership != null) {
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }

        Membership membership = Membership.builder()
                .userId(userId)
                .point(point)
                .membershipType(membershipType)
                .build();
        Membership savedMembership = membershipRepository.save(membership);

        if (savedMembership.getId() == null) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return MembershipAddResponse.builder()
                .id(savedMembership.getId())
                .membershipType(savedMembership.getMembershipType())
                .build();
    }

    public MembershipDetailResponse getMembership(Long membershipId, String userId) {
        Optional<Membership> findMembership = membershipRepository.findById(membershipId);

        Membership membership = findMembership
                .orElseThrow(() -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));
        if (!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
        }

        return MembershipDetailResponse.builder()
                .id(membership.getId())
                .membershipType(membership.getMembershipType())
                .point(membership.getPoint())
                .createAt(membership.getCreatedAt())
                .build();
    }

    public List<MembershipDetailResponse> getMembershipList(String userId) {

        List<Membership> membershipList = membershipRepository.findAllByUserId(userId);

        return membershipList.stream()
                .map(v -> MembershipDetailResponse.builder()
                        .id(v.getId())
                        .membershipType(v.getMembershipType())
                        .point(v.getPoint())
                        .createAt(v.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    public void removeMembership(Long membershipId, String userId) {
        Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);

        Membership membership = optionalMembership.orElseThrow(
                () -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        if (!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        membershipRepository.deleteById(membershipId);
    }

    @Transactional
    public void accumulateMembershipPoint(Long membershipId, String userId, int point) {
        Optional<Membership> optionalMembership = membershipRepository.findById(membershipId);
        Membership membership = optionalMembership.orElseThrow(
                () -> new MembershipException(MembershipErrorResult.MEMBERSHIP_NOT_FOUND));

        if (!membership.getUserId().equals(userId)) {
            throw new MembershipException(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
        }

        final int additionalPoint = ratePointPointService.calculateAmount(point);
        membership.accumulatePoint(additionalPoint);
    }
}
