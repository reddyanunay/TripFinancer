package com.example.backend.service;

import com.example.backend.domain.Trip;
import com.example.backend.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TripService {
    @Autowired
    private TripRepository tripRepo;

    public Trip createTrip(Trip trip) {
        return tripRepo.save(trip);
    }

    public Trip getTrip(Long id) {
        return tripRepo.findById(id).orElse(null);
    }
}
