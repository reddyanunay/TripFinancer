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

            //Updating the PersonalCosts of each member;
            double previousPersonalCosts = member.getPersonalCosts();
            member.setPersonalCosts(previousPersonalCosts + expenseDto.getAmount());

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
            Member member = memSer.getMemberById(expenseDto.getMemberId());

            if (existingExpense != null) {
                // Update existing expense
                double previousShare = existingExpense.getShare();
                existingExpense.setShare(expenseDto.getAmount());

                // Adjust the personalCosts for the member
                double previousPersonalCosts = member.getPersonalCosts();
                member.setPersonalCosts(previousPersonalCosts - previousShare + expenseDto.getAmount());

                expRepo.save(existingExpense);
            } else {
                // Create and add new expense
                Expense newExpense = new Expense();
                newExpense.setBill(existingBill);
                newExpense.setShare(expenseDto.getAmount());
                newExpense.setMember(member);

                // Update the personalCosts of the member for new expense
                double previousPersonalCosts = member.getPersonalCosts();
                member.setPersonalCosts(previousPersonalCosts + expenseDto.getAmount());

                expRepo.save(newExpense);
            }
        }

        // Remove expenses that were not in the new list
        if (!expenseMap.isEmpty()) {
            for (Expense removedExpense : expenseMap.values()) {
                Member member = removedExpense.getMember();

                // Adjust the personalCosts for the member when an expense is removed
                double previousPersonalCosts = member.getPersonalCosts();
                member.setPersonalCosts(previousPersonalCosts - removedExpense.getShare());

                expRepo.delete(removedExpense);
            }
        }

        // Save updated bill
        Bill updatedBill = billRepo.save(existingBill);
        updateTripDetails(existingBill.getTrip());
        return updatedBill;
    }

    @Transactional
    public void deleteBill(Long billId) {
        // Fetch the existing bill from the database
        Bill bill = billRepo.findById(billId)
                .orElseThrow(() -> new RuntimeException("Bill not found"));

        // Get all expenses associated with the bill
        List<Expense> expenses = bill.getBill_all_expenses();

        // Adjust the personalCosts for each member involved in the bill
        for (Expense expense : expenses) {
            Member member = expense.getMember();

            // Update the member's personalCosts by subtracting the share they had in this bill
            double previousPersonalCosts = member.getPersonalCosts();
            member.setPersonalCosts(previousPersonalCosts - expense.getShare());

            // No need to save member explicitly, assuming cascading saves from Expense or MemberRepo saves them later.
        }

        // Remove the expenses associated with the bill
        expRepo.deleteAll(expenses);

        // Update the trip's total cost and bill count
        Trip trip = bill.getTrip();
        trip.updateTotalCostAndBillCount(-bill.getBillAmount());
        trip.getAllBills().remove(bill);
        tripSer.saveTrip(trip); // Save the updated trip

        // Delete the bill
        billRepo.delete(bill);
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
