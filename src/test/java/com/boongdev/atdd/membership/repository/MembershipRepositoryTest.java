package com.boongdev.atdd.membership.repository;

import com.boongdev.atdd.membership.domain.membership.Membership;
import com.boongdev.atdd.membership.domain.membership.MembershipType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class MembershipRepositoryTest {

    @Autowired MembershipRepository membershipRepository;

    @Test
    public void BeanIsNotNull() {
        assertThat(membershipRepository).isNotNull();
    }

    @Test
    public void 멤버쉽_조회_사이즈_0() throws Exception {
        // given

        // when
        List<Membership> result = membershipRepository.findAll();

        // then
        assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void 멤버쉽_조회_사이즈_2() throws Exception {
        // given
        final Membership naberMembership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(1000)
                .build();

        final Membership kakaoMembership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.KAKAO)
                .point(2000)
                .build();
        membershipRepository.save(kakaoMembership);
        membershipRepository.save(naberMembership);

        // when
        List<Membership> result = membershipRepository.findAllByUserId("userId");

        // then
        assertThat(result.size()).isEqualTo(2);
    }

    @DisplayName("멤버쉽_등록")
    @Test
    public void createMembership() {
        // given
        Membership memberShip = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(1000)
                .build();

        // when
        final Membership saveMembership = membershipRepository.save(memberShip);

        // then
        assertThat(saveMembership.getId()).isNotNull();
        assertThat(saveMembership.getUserId()).isEqualTo("userId");
        assertThat(saveMembership.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(saveMembership.getPoint()).isEqualTo(1000);
    }

    @DisplayName("멤버쉽_중복등록_예외")
    @Test
    public void membershipException() {
        // given
        Membership memberShip = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(1000)
                .build();

        // when
        membershipRepository.save(memberShip);
        Membership findMembership = membershipRepository.findByUserIdAndMembershipType("userId", MembershipType.NAVER);

        // then
        assertThat(findMembership).isNotNull();
        assertThat(findMembership.getId()).isNotNull();
        assertThat(findMembership.getUserId()).isEqualTo("userId");
        assertThat(findMembership.getMembershipType()).isEqualTo(MembershipType.NAVER);
        assertThat(findMembership.getPoint()).isEqualTo(1000);
    }

    @Test
    public void 멤버쉽_추가_삭제() throws Exception {
        // given
        final Membership naverMembership = Membership.builder()
                .userId("userId")
                .membershipType(MembershipType.NAVER)
                .point(100000)
                .build();

        final Membership save = membershipRepository.save(naverMembership);

        // when
        membershipRepository.deleteById(save.getId());

        // then

    }

}
