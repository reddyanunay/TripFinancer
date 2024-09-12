package com.example.backend.service;

import com.example.backend.domain.*;
import com.example.backend.repository.BillRepository;
import com.example.backend.repository.ExpenseRepository;
import com.example.backend.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepo;
    @Autowired
    private ExpenseRepository expRepo;
    @Autowired
    private MemberService memSer;
    @Autowired
    private TripService tripSer;


    public Bill createBill(Bill bill){
        Bill b=billRepo.save(bill);
        return b;
    }
    @Transactional
    public Bill createBillWithExpenses(BillRequestDTO billDto){
        Bill bill = new Bill();
        bill.setBillAmount(billDto.getBillAmount());
        bill.setDescription(billDto.getDescription());

        Member paidByMember = memSer.getMemberById(billDto.getPaidByMemberId()); // Assuming Member is fetched from the DB
        bill.setPaidByMember(paidByMember);

        Trip trip = tripSer.getTrip(billDto.getTrip()); // Assuming Trip is fetched from the DB
        bill.setTrip(trip);

        Bill savedBill = billRepo.save(bill);

        List<Expense> expenses = new ArrayList<>();

        for (ExpenseRequestDTO expenseDto : billDto.getAllExpenses()) {
            Expense expense = new Expense();
            expense.setBill(savedBill); // Set the bill to associate with
            expense.setShare(expenseDto.getAmount());

            // Set the member for the expense
            Member member = memSer.getMemberById(expenseDto.getMemberId());  // Assuming Member is fetched from the DB
            expense.setMember(member);

            expenses.add(expense);
            // Save each expense
        }
        expRepo.saveAll(expenses);
        savedBill.setBill_all_expenses(expenses);

        return billRepo.save(savedBill);
    }

    public List<BillRequestDTO> getAllBillsByTripId(Long tripId) {
        List<BillRequestDTO> billDTOList = new ArrayList<>();

        // Retrieve all bills related to the given tripId
        List<Bill> bills = billRepo.findByTrip_TripId(tripId);

        for (Bill bill : bills) {
            BillRequestDTO billDTO = new BillRequestDTO();
            billDTO.setBillId(bill.getBillId());
            billDTO.setTrip(bill.getTrip().getTripId());
            billDTO.setBillAmount(bill.getBillAmount());
            billDTO.setPaidByMemberId(bill.getPaidByMember().getMemberId());
            billDTO.setDescription(bill.getDescription());

            // For each bill, retrieve the associated expenses and map them to ExpenseRequestDTO
            List<ExpenseRequestDTO> expenseDTOList = new ArrayList<>();
            for (Expense expense : bill.getBill_all_expenses()) {
                ExpenseRequestDTO expenseDTO = new ExpenseRequestDTO();
                expenseDTO.setMemberId(expense.getMember().getMemberId());
                expenseDTO.setName(expense.getMember().getName());
                expenseDTO.setAmount(expense.getShare());
                expenseDTOList.add(expenseDTO);
            }

            // Set the list of expense DTOs in the bill DTO
            billDTO.setAllExpenses(expenseDTOList);

            // Add the BillRequestDTO to the final list
            billDTOList.add(billDTO);
        }

        return billDTOList;
    }
}
