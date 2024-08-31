package com.example.backend.controller;

import com.example.backend.domain.Expense;
import com.example.backend.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseService expServ;

    @PostMapping("/create")
    public ResponseEntity<?> createExpense(@RequestBody Expense exp){
        Expense e = expServ.createExp(exp);
        return new ResponseEntity<Expense>(e, HttpStatus.CREATED);
    }

    @GetMapping("/getallbybillid/{billId}")
    public ResponseEntity<?> getallExpensesByBillId(@PathVariable Long billId){
        List<Expense> l= expServ.getAllExpensesByBillID(billId);
        return new ResponseEntity<List<Expense>>(l,HttpStatus.OK);
    }

}
