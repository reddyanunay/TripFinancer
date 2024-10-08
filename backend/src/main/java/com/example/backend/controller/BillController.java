package com.example.backend.controller;

import com.example.backend.domain.Bill;
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

    @GetMapping("/all")
    public ResponseEntity<?> getAllBills() {
        List<Bill> bills = billService.getAllBills();
        return new ResponseEntity<List<Bill>>(bills, HttpStatus.OK);
    }
}
