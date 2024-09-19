package com.example.backend.repository;

import com.example.backend.domain.Expense;
import com.example.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findById(Long id);
    List<Member> findByTrip_TripId(Long tripId);
    List<Member> findByMemberIdIn(Set<Long> memberIds);
}
