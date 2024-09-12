package com.example.backend.controller;

import com.example.backend.domain.Bill;
import com.example.backend.domain.BillRequestDTO;
import com.example.backend.service.BillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bills")
public class BillController {
    @Autowired
    private BillService billService;

    @PostMapping("/create")
    public ResponseEntity<?> createBill(@RequestBody Bill bill) {
        Bill b=billService.createBill(bill);
        return new ResponseEntity<Bill>(b, HttpStatus.CREATED);
    }
    @PostMapping("/createWithExpenses")
    public ResponseEntity<?> createBillWithExpenses(@RequestBody BillRequestDTO billDto) {
        System.out.println("BillDto: "+billDto);
        Bill b=billService.createBillWithExpenses(billDto);
        return new ResponseEntity<Bill>(b, HttpStatus.CREATED);
    }

    @GetMapping("/all/{tripId}")
    public ResponseEntity<?> getAllBills(@PathVariable Long tripId) {
        List<BillRequestDTO> bills = billService.getAllBillsByTripId(tripId);
        return new ResponseEntity<List<BillRequestDTO>>(bills, HttpStatus.OK);
    }
}
