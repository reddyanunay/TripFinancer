package com.example.backend.controller;

import com.example.backend.domain.Member;
import com.example.backend.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/members")
public class MemberController {
    @Autowired
    private MemberService memberService;

    @PostMapping("/member")
    public ResponseEntity<?> saveMember(@RequestBody Member member){
        Member m=memberService.saveMember(member);
        return new ResponseEntity<Member>(m, HttpStatus.CREATED);
    }

    @GetMapping("/getAllMembers")
    public ResponseEntity<?> getAll(){
        List<Member> ml=memberService.findAllMembers();
        return new ResponseEntity<List<Member>>(ml,HttpStatus.OK);
    }

}
