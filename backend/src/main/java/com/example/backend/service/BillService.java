package com.example.backend.service;

import com.example.backend.domain.Bill;
import com.example.backend.domain.BillRequestDTO;
import com.example.backend.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepo;

    public Bill createBill(Bill bill){
        Bill b=billRepo.save(bill);
        return b;
    }

    public List<BillRequestDTO> getAllBills() {
        List<BillRequestDTO> bDTO = new ArrayList<>();
        for(Bill bill:billRepo.findAll()){
            BillRequestDTO b1 = new BillRequestDTO();
            b1.setBillAmount(bill.getBillAmount());
            b1.setBillId(bill.getBillId());
            b1.setPaidByMember(bill.getPaidByMember());
            b1.setBill_all_expenses(bill.getBill_all_expenses());
            b1.setDescription(bill.getDescription());
            bDTO.add(b1);
        }
        return bDTO;
    }
}
