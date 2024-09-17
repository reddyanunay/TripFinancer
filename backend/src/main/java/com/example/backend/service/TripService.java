package com.example.backend.service;

import com.example.backend.Exception.UserNotFoundException;
import com.example.backend.domain.Member;
import com.example.backend.domain.Trip;
import com.example.backend.domain.TripRequestDTO;
import com.example.backend.domain.User;
import com.example.backend.repository.TripRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TripService {
    @Autowired
    private TripRepository tripRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private MemberService memberservice;

    public Trip saveTrip(Trip trip) {
        return tripRepo.save(trip);
    }
    public Trip createTrip(TripRequestDTO tripRequest) throws UserNotFoundException {
        System.out.println(tripRequest);

        Trip trip = new Trip();
        trip.setTrip_name(tripRequest.getTripName());
        trip.setNo_of_people(tripRequest.getNoOfPeople());

        User u = userService.getUser(tripRequest.getEmail());
        trip.setUser(u);

        Trip savedTrip = tripRepo.save(trip);
        List<Member> members = new ArrayList<>();
        for(String membername : tripRequest.getMembers()){
            Member member = new Member();
            member.setName(membername);
            member.setTrip(trip);
            Member savedMember = memberservice.saveMember(member);
            members.add(savedMember);
        }
        savedTrip.setAllMembers(members);
        u.getTrips().add(savedTrip);

        return tripRepo.save(savedTrip);
    }

    public Trip getTrip(Long id) {
        return tripRepo.findById(id).orElse(null);
    }
    public void deleteTrip(Long id) {
        tripRepo.deleteById(id);
    }
}
