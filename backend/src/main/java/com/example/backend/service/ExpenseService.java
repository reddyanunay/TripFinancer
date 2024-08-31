package com.example.backend.service;
import com.example.backend.domain.Bill;
import com.example.backend.domain.Expense;
import com.example.backend.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository expRepo;

    public Expense createExp(Expense exp){
        Expense e=expRepo.save(exp);
        return e;
    }
    public List<Expense> getAllExpensesByBillID(Long billId){
        return expRepo.findByBill_BillId(billId);
    }
}
