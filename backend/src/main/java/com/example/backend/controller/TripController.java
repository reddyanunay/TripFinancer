package com.example.backend.controller;

import com.example.backend.domain.Trip;
import com.example.backend.service.TripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    @Autowired
    private TripService tripService;

    @PostMapping("/trip")
    public ResponseEntity<?> saveTrip(@RequestBody Trip trip) {
        Trip t= tripService.createTrip(trip);
        return new ResponseEntity<Trip>(t, HttpStatus.CREATED);
    }

    @GetMapping("/trip")
    public ResponseEntity<?> getTripById(@PathVariable Long id) {
        Trip t= tripService.getTrip(id);
        return new ResponseEntity<Trip>(t,HttpStatus.OK);
    }
}
