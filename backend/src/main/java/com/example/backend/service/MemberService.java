package com.example.backend.service;

import com.example.backend.domain.Member;
import com.example.backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepo;

    public Member saveMember(Member member){
      Member m=memberRepo.save(member);
      return m;
    }

    public List<Member> findAllMembers(){
        List<Member> ml=memberRepo.findAll();
        return ml;
    }
}
