package com.example.backend.service;

import com.example.backend.domain.*;
import com.example.backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepo;

    public Member saveMember(Member member){
      Member m=memberRepo.save(member);
      return m;
    }

    public List<Member> saveMembers(List<String> s, Trip trip){
        List<Member> m=new ArrayList<>();
        for(String name:s){
            Member m1=new Member();
            m1.setName(name);
            m1.setTrip(trip);
            m.add(memberRepo.save(m1));
        }
        return m;
    }
    public Member getMemberById(Long id){
        return memberRepo.findById(id).orElse(null);
    }

    public List<MemberRequestDTO> findAllMembersByTripId(Long tripId){
        List<MemberRequestDTO> mDTO = new ArrayList<>();
        for(Member mem:memberRepo.findByTrip_TripId(tripId)){
            MemberRequestDTO m = new MemberRequestDTO();
            m.setMemberId(mem.getMemberId());
            m.setName(mem.getName());
            mDTO.add(m);
        }
        return mDTO;
    }
}
