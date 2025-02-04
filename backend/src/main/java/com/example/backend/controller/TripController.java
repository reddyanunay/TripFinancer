package com.example.backend.controller;

import com.example.backend.Exception.UserNotFoundException;
import com.example.backend.domain.Member;
import com.example.backend.domain.Trip;
import com.example.backend.domain.TripRequestDTO;
import com.example.backend.service.AnalysisService;
import com.example.backend.service.MemberService;
import com.example.backend.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/trips", method = RequestMethod.OPTIONS)
public class TripController {
    @Autowired
    private TripService tripService;

    @Autowired
    private MemberService memberservice;
    @Autowired
    private AnalysisService analysisServ;

    @PostMapping("/trip")
    public ResponseEntity<?> saveTrip(@RequestBody TripRequestDTO tripRequest) throws UserNotFoundException {
        Trip trip = tripService.createTrip(tripRequest);
        return new ResponseEntity<Trip>(trip, HttpStatus.CREATED);
    }
    @GetMapping("/getTripsWithEmail/{email}")
    public ResponseEntity<?> getTripsByEmail(@PathVariable String email) {
        return new ResponseEntity<>(tripService.getTripsByEmail(email),HttpStatus.OK);
    }

    @GetMapping("/trip/{id}")
    public ResponseEntity<?> getTripById(@PathVariable Long id) {
        Trip t= tripService.getTrip(id);
        return new ResponseEntity<Trip>(t,HttpStatus.OK);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteTrip(@PathVariable Long id) {
        tripService.deleteTrip(id);
        return new ResponseEntity<String>("Trip deleted successfully",HttpStatus.OK);
    }

    @GetMapping("/{tripId}/summary")
    public ResponseEntity<?> analyseTrip(@PathVariable Long tripId) {
        return new ResponseEntity<>(analysisServ.getTotalTripSummary(tripId),HttpStatus.OK);
    }

    @GetMapping("/{tripId}/personal-summary/{memberId}")
    public ResponseEntity<Map<String, Object>> getPersonalExpenditure(@PathVariable Long tripId, @PathVariable Long memberId) {
        Map<String, Object> personalExpenditure = analysisServ.getMemberSummary(tripId, memberId);
        return new ResponseEntity<>(personalExpenditure,HttpStatus.OK);
    }

}
