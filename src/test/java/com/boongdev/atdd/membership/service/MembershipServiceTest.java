package com.boongdev.atdd.membership.service;

import com.boongdev.atdd.membership.domain.membership.Membership;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipAddResponse;
import com.boongdev.atdd.membership.domain.membership.MembershipType;
import com.boongdev.atdd.membership.domain.membership.dto.MembershipDetailResponse;
import com.boongdev.atdd.membership.exception.MembershipErrorResult;
import com.boongdev.atdd.membership.exception.MembershipException;
import com.boongdev.atdd.membership.repository.MembershipRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembershipServiceTest {

    @Mock
    MembershipRepository membershipRepository;

    @InjectMocks
    MembershipService membershipService;

    private final String userId = "userId";
    private final MembershipType membershipType = MembershipType.KAKAO;
    private final int point = 1000;
    private final Long membershipId = -1L;

    private Membership membership() {
        return Membership.builder()
                .id(-1L)
                .userId(userId)
                .point(point)
                .membershipType(membershipType)
                .build();
    }

    @Test
    public void 멤버쉽_상세조회_실패_존재안함() throws Exception {
        // given
        doReturn(Optional.empty())
                .when(membershipRepository).findById(membershipId);

        // when
        MembershipException result = assertThrows(MembershipException.class,
                () -> membershipService.getMembership(membershipId, userId));

        // then
        assertThat(result.getMembershipErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버쉽_상세조회_실패_본인아님() throws Exception {
        // given
        doReturn(Optional.empty())
                .when(membershipRepository).findById(membershipId);

        // when
        MembershipException result = assertThrows(MembershipException.class,
                () -> membershipService.getMembership(membershipId, "not owner"));

        // then
        assertThat(result.getMembershipErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버쉽_상세조회_성공() throws Exception {
        // given
        doReturn(Optional.of(membership()))
                .when(membershipRepository)
                .findById(membershipId);

        // when
        MembershipDetailResponse result = membershipService.getMembership(membershipId, userId);

        // then
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.KAKAO);
        assertThat(result.getPoint()).isEqualTo(point);
    }

    @Test
    public void 멤버쉽_조회_목록() throws Exception {
        // given
        doReturn(
                Arrays.asList(
                        Membership.builder().build(),
                        Membership.builder().build(),
                        Membership.builder().build()
                )
        ).when(membershipRepository).findAllByUserId(userId);

        // when
        List<MembershipDetailResponse> membershipList = membershipService.getMembershipList(userId);

        // then
        assertThat(membershipList.size()).isEqualTo(3);
    }

    @Test
    public void 멤버쉽_등록실패_이미존재하는() {
        // given
        doReturn(Membership.builder().build()).when(membershipRepository).findByUserIdAndMembershipType(userId, membershipType);

        // when
        MembershipException membershipException = assertThrows(MembershipException.class,
                () -> membershipService.addMembership(userId, point, membershipType));

        // then
        assertThat(membershipException.getMembershipErrorResult()).isEqualTo(MembershipErrorResult.DUPLICATED_MEMBERSHIP_REGISTER);
    }

    @Test
    public void 멤버쉽_등록성공() {
        // given
        doReturn(null).when(membershipRepository)
                .findByUserIdAndMembershipType(userId, membershipType);
        doReturn(membership()).when(membershipRepository)
                .save(any(Membership.class));

        // when
        MembershipAddResponse membership = membershipService.addMembership(userId, point, membershipType);

        // then
        assertThat(membership.getId()).isNotNull();
        assertThat(membership.getMembershipType()).isEqualTo(MembershipType.KAKAO);

        // verify
        verify(membershipRepository, times(1)).findByUserIdAndMembershipType(userId, membershipType);
        verify(membershipRepository, times(1)).save(any(Membership.class));
    }

    @Test
    public void 멤버쉽_삭제실패_존재안함() throws Exception {
        // given
        final Membership membership = membership();
        doReturn(Optional.empty()).when(membershipRepository).findById(membershipId);

        // when
        final MembershipException result = assertThrows(MembershipException.class,
                () -> membershipService.removeMembership(membershipId, userId));

        // then
        assertThat(result.getMembershipErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버쉽_삭제실패_소유자아님() throws Exception {
        // given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        // when
        final MembershipException result = assertThrows(MembershipException.class,
                () -> membershipService.removeMembership(membershipId, "not owner"));

        // then
        assertThat(result.getMembershipErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버쉽_삭제성공() throws Exception {
        // given
        final Membership membership = membership();
        doReturn(Optional.of(membership)).when(membershipRepository).findById(membershipId);

        // when
        membershipService.removeMembership(membershipId, userId);

        // then
    }

    @Test
    public void 멤버쉽_적립실패_존재하지_않음() throws Exception {
        // given
        doReturn(Optional.empty())
                .when(membershipRepository).findById(membershipId);

        // when
        MembershipException result = assertThrows(MembershipException.class,
                () -> membershipService.accumulateMembershipPoint(membershipId, userId, point));

        // then
        assertThat(result.getMembershipErrorResult()).isEqualTo(MembershipErrorResult.MEMBERSHIP_NOT_FOUND);
    }

    @Test
    public void 멤버쉽_적립실패_본인아님() throws Exception {
        Membership membership = membership();
        // given
        doReturn(Optional.of(membership))
                .when(membershipRepository).findById(membershipId);

        // when
        MembershipException result = assertThrows(MembershipException.class,
                () -> membershipService.accumulateMembershipPoint(membershipId, "not owner", point));

        // then
        assertThat(result.getMembershipErrorResult()).isEqualTo(MembershipErrorResult.NOT_MEMBERSHIP_OWNER);
    }

    @Test
    public void 멤버쉽_적립성공() throws Exception {
        // given
        Membership membership = membership();
        doReturn(Optional.of(membership))
                .when(membershipRepository).findById(membershipId);

        // when
        membershipService.accumulateMembershipPoint(membershipId, userId, point);

        // then

    }

}
