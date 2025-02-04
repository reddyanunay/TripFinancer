package com.example.backend.controller;

import com.example.backend.domain.Member;
import com.example.backend.domain.MemberRequestDTO;
import com.example.backend.domain.Trip;
import com.example.backend.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;


@CrossOrigin
@RestController
@RequestMapping(value = "/api/members", method = RequestMethod.OPTIONS)
public class MemberController {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberService memberService;

    @PostMapping("/saveMember")
    public ResponseEntity<?> saveMember(@RequestBody Member member){
        Member m=memberService.saveMember(member);
        return new ResponseEntity<Member>(m, HttpStatus.CREATED);
    }

    @PostMapping("/saveMembers")
    public ResponseEntity<?> saveAll(@RequestBody Map<String,Object> requestBody){
        List<String> memberNames = (List<String>) requestBody.get("memberNames");
        Trip trip = objectMapper.convertValue(requestBody.get("trip"), Trip.class);
        List<Member> members = memberService.saveMembers(memberNames, trip);
        return new ResponseEntity<List<Member>>(members,HttpStatus.CREATED);
    }

    @GetMapping("/getAllMembers/{tripId}")
    public ResponseEntity<?> getAll(@PathVariable Long tripId){
        List<MemberRequestDTO> ml=memberService.findAllMembersByTripId(tripId);
        return new ResponseEntity<List<MemberRequestDTO>>(ml,HttpStatus.OK);
    }

}
