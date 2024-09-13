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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        trip.getAllBills().add(savedBill); // Add the new bill to the list
        trip.updateTotalCostAndBillCount(bill.getBillAmount()); // Update cost and bill count
        tripSer.saveTrip(trip);

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
    @Transactional
    public Bill updateBillWithExpenses(BillRequestDTO billDto) {
        System.out.println("BillDto: " + billDto);

        // Fetch the existing bill from the database
        Bill existingBill = billRepo.findById(billDto.getBillId())
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        // Update the bill details
        existingBill.setBillAmount(billDto.getBillAmount());
        existingBill.setDescription(billDto.getDescription());

        // Update the paidByMember
        Member paidByMember = memSer.getMemberById(billDto.getPaidByMemberId());
        existingBill.setPaidByMember(paidByMember);

        // Retrieve existing expenses for the bill
        List<Expense> existingExpenses = new ArrayList<>(existingBill.getBill_all_expenses());

        // Create a map of existing expenses for quick lookup
        Map<Long, Expense> expenseMap = existingExpenses.stream()
                .collect(Collectors.toMap(expense -> expense.getMember().getMemberId(), expense -> expense));

        List<ExpenseRequestDTO> newExpenseDtos = billDto.getAllExpenses();

        // Update or add expenses
        for (ExpenseRequestDTO expenseDto : newExpenseDtos) {
            Expense existingExpense = expenseMap.remove(expenseDto.getMemberId());
            if (existingExpense != null) {
                // Update existing expense
                existingExpense.setShare(expenseDto.getAmount());
                expRepo.save(existingExpense);
            } else {
                // Create and add new expense
                Expense newExpense = new Expense();
                newExpense.setBill(existingBill);
                newExpense.setShare(expenseDto.getAmount());

                Member member = memSer.getMemberById(expenseDto.getMemberId());
                newExpense.setMember(member);

                expRepo.save(newExpense);
            }
        }

        // Remove expenses that were not in the new list
        if (!expenseMap.isEmpty()) {
            expRepo.deleteAll(expenseMap.values());
        }

        // Save updated bill
        Bill updatedBill = billRepo.save(existingBill);
        updateTripDetails(existingBill.getTrip());
        return updatedBill;
    }

    private void updateTripDetails(Trip trip) {
        // Recalculate total cost and number of bills for the trip
        double totalCost = trip.getAllBills().stream()
                .mapToDouble(Bill::getBillAmount)
                .sum();
        trip.setTotal_cost(totalCost);
        trip.setNo_of_bills(trip.getAllBills().size());

        // Save the updated trip
        tripSer.saveTrip(trip);
    }
}
