package com.example.backend.service;

import com.example.backend.domain.Bill;
import com.example.backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepo;

    public Bill createBill(Bill bill){
        Bill b=billRepo.save(bill);
        return b;
    }

    public List<Bill> getAllBills() {
        return billRepo.findAll();
    }
}
