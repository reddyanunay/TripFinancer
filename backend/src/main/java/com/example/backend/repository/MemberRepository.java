package com.example.backend.repository;

import com.example.backend.domain.Expense;
import com.example.backend.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long> {
    List<Member> findByTrip_TripId(Long tripId);
}
