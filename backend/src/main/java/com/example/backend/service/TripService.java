package com.example.backend.service;

import com.example.backend.Exception.UserNotFoundException;
import com.example.backend.domain.*;
import com.example.backend.repository.BillRepository;
import com.example.backend.repository.TripRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TripService {
    @Autowired
    private TripRepository tripRepo;
    @Autowired
    private UserService userService;
    @Autowired
    private BillRepository billRepo;
    @Autowired
    private MemberService memberservice;

    public Trip saveTrip(Trip trip) {
        return tripRepo.save(trip);
    }
    public Trip createTrip(TripRequestDTO tripRequest) throws UserNotFoundException {
        System.out.println(tripRequest);

        Trip trip = new Trip();
        trip.setTrip_name(tripRequest.getTripName());
        trip.setNo_of_people(tripRequest.getNoOfPeople());

        User u = userService.getUser(tripRequest.getEmail());
        trip.setUser(u);

        Trip savedTrip = tripRepo.save(trip);
        List<Member> members = new ArrayList<>();
        for(String membername : tripRequest.getMembers()){
            Member member = new Member();
            member.setName(membername);
            member.setTrip(trip);
            Member savedMember = memberservice.saveMember(member);
            members.add(savedMember);
        }
        savedTrip.setAllMembers(members);
        u.getTrips().add(savedTrip);

        return tripRepo.save(savedTrip);
    }

    public Trip getTrip(Long id) {
        return tripRepo.findById(id).orElse(null);
    }
    public List<Trip> getTripsByEmail(String email) {
        return tripRepo.findByUserEmail(email);
    }
    public void deleteTrip(Long id) {
        tripRepo.deleteById(id);
    }

    //These three Functions will give analysis on who owes whom and how much
    public Map<String, Map<String, Double>> getDebtAnalysisForTrip(Long tripId) {
        List<Bill> bills = billRepo.findByTrip_TripId(tripId);
        List<DebtRecord> debts = calculateDebts(bills);
        return simplifyDebts(debts);
    }
    public List<DebtRecord> calculateDebts(List<Bill> bills) {
        List<DebtRecord> debts = new ArrayList<>();

        for (Bill bill : bills) {
            String payer = bill.getPaidByMember().getName();
            List<Expense> expenses = bill.getBill_all_expenses(); // Get the expenses for this bill

            for (Expense expense : expenses) {
                String person = expense.getMember().getName();
                double amountOwed = expense.getShare();

                if (!person.equals(payer)) {
                    // Create a record of who owes whom and how much
                    debts.add(new DebtRecord(person, payer, amountOwed));
                }
            }
        }

        return debts;
    }
    public Map<String, Map<String, Double>> simplifyDebts(List<DebtRecord> debts) {
        Map<String, Map<String, Double>> debtMap = new HashMap<>();

        // Consolidate all debts
        for (DebtRecord debt : debts) {
            String debtor = debt.getFrom();
            String creditor = debt.getTo();
            double amount = debt.getAmount();

            // Add to the debtor's record
            debtMap.putIfAbsent(debtor, new HashMap<>());
            debtMap.get(debtor).put(creditor, debtMap.get(debtor).getOrDefault(creditor, 0.0) + amount);

            // Add mutual debt reduction from the creditor's perspective
            debtMap.putIfAbsent(creditor, new HashMap<>());
            debtMap.get(creditor).put(debtor, debtMap.get(creditor).getOrDefault(debtor, 0.0) - amount);
        }

        // Simplify the debt map by netting off mutual debts
        Map<String, Map<String, Double>> simplifiedDebts = new HashMap<>();

        for (String debtor : debtMap.keySet()) {
            for (String creditor : debtMap.get(debtor).keySet()) {
                double netAmount = debtMap.get(debtor).get(creditor);

                // Only store if there's an outstanding balance
                if (netAmount > 0) {
                    simplifiedDebts.putIfAbsent(debtor, new HashMap<>());
                    simplifiedDebts.get(debtor).put(creditor, netAmount);
                }
            }
        }

        return simplifiedDebts;
    }


    public Map<String, Object> getPersonalExpenditure(Long tripId, String memberId) {
        List<Bill> bills = billRepo.findByTrip_TripId(tripId);

        double totalPaid = 0.0;
        double totalOwed = 0.0;
        double totalToReceive = 0.0;
        List<Map<String, Object>> expenseDetails = new ArrayList<>();

        for (Bill bill : bills) {
            if (bill.getPaidByMember().getMemberId().equals(memberId)) {
                totalPaid += bill.getBillAmount();
                for (Expense expense : bill.getBill_all_expenses()) {
                    if (!expense.getMember().getMemberId().equals(memberId)) {
                        totalOwed += expense.getShare();
                        Map<String, Object> expenseDetail = new HashMap<>();
                        expenseDetail.put("billId", bill.getBillId());
                        expenseDetail.put("amountPaid", expense.getShare());
                        expenseDetails.add(expenseDetail);
                    }
                }
            }
        }

        totalToReceive = calculateTotalToReceive(bills, memberId);

        Map<String, Object> response = new HashMap<>();
        response.put("memberId", memberId);
        response.put("totalPaid", totalPaid);
        response.put("totalOwed", totalOwed);
        response.put("totalToReceive", totalToReceive);
        response.put("expenses", expenseDetails);

        return response;
    }

    public double calculateTotalToReceive(List<Bill> bills, String memberId) {
        double totalToReceive = 0.0;

        for (Bill bill : bills) {
            List<Expense> expenses = bill.getBill_all_expenses();
            for (Expense expense : expenses) {
                if (expense.getMember().getMemberId().equals(memberId)) {
                    // If the expense's member is the target member, this means others owe this member
                    totalToReceive += expense.getShare();
                }
            }
        }

        return totalToReceive;
    }


}
