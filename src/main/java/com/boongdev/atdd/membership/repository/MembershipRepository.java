package com.boongdev.atdd.membership.repository;

import com.boongdev.atdd.membership.domain.membership.Membership;
import com.boongdev.atdd.membership.domain.membership.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {
    Membership findByUserIdAndMembershipType(String userId, MembershipType membershipType);

    List<Membership> findAllByUserId(String userId);
}
