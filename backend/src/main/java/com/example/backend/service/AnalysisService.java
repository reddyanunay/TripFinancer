package com.example.backend.service;

import com.example.backend.domain.Bill;
import com.example.backend.domain.Expense;
import com.example.backend.domain.Member;
import com.example.backend.repository.BillRepository;
import com.example.backend.repository.MemberRepository;
import com.example.backend.repository.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalysisService {
    @Autowired
    private TripRepository tripRepo;
    @Autowired
    private BillRepository billRepo;
    @Autowired
    private MemberRepository memRepo;



    //Total summary for trip method
    public Map<String, Object> getTotalTripSummary(Long tripId) {
        List<Bill> bills = billRepo.findByTrip_TripId(tripId);
        double totalCost = calculateTotalCost(bills);
        List<Map<String, Object>> costBreakdownByBill = calculateCostBreakdownByBill(bills);
        Map<Long, Double> memberExpenditures = calculateEachMemberTotalExpenditure(bills);
        Map<Long, Double> memberPercentages = calculateMemberPercentages(memberExpenditures, totalCost);

        Map<Long, String> memberNames = fetchMemberNames(memberExpenditures.keySet());
        Bill largestBill = findLargestSingleBill(bills);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalCost", totalCost);
        summary.put("costBreakdownByBill", costBreakdownByBill);
        summary.put("eachMemberTotalExpenditure", memberExpenditures.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> memberData = new HashMap<>();
                    memberData.put("memberId", entry.getKey());
                    memberData.put("memberName", memberNames.get(entry.getKey()));
                    memberData.put("totalExpenditure", entry.getValue());
                    return memberData;
                }).collect(Collectors.toList()));

        summary.put("memberPercentages", memberPercentages.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> percentageData = new HashMap<>();
                    percentageData.put("memberId", entry.getKey());
                    percentageData.put("memberName", memberNames.get(entry.getKey()));
                    percentageData.put("percentageOfTotal", entry.getValue());
                    return percentageData;
                }).collect(Collectors.toList()));
        if (largestBill != null) {
            summary.put("largestSingleBill", Map.of("billName", largestBill.getDescription(), "amount", largestBill.getBillAmount()));
        }

        return summary;
    }


    //Member vise summary method
    public Map<String, Object> getMemberSummary(Long tripId, Long memberId) {
        List<Bill> bills = billRepo.findByTrip_TripId(tripId);

        // Existing calculations
        double totalCost = calculateTotalCost(bills);
        double totalSpent = calculateTotalSpent(bills, memberId);
        double totalOwed = calculateTotalOwed(bills, memberId);
        double totalToReceive = calculateTotalToReceive(bills, memberId);
        double totalExpenditureForMember = calculateTotalExpenditureForMember(bills, memberId);
        List<Map<String, Object>> billsPaid = getBillsPaid(bills, memberId);
        List<Map<String, Object>> billBreakdown = getBillBreakdown(bills, memberId);
        double percentExpenditure = calculatePercentExpenditure(totalCost, totalExpenditureForMember);
        Map<Long, Map<String, Object>> comparativeAnalysis = getComparativeAnalysis(bills, memberId);

        // Calculate debts
        List<Map<String, Object>> debts = calculateTotalDebts(bills, memberId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("memberId", memberId);
        summary.put("memberName", getMemberName(memberId));
        summary.put("totalExpenditure", totalExpenditureForMember);
        summary.put("totalSpent", totalSpent);
        summary.put("totalOwed", totalOwed);
        summary.put("totalToReceive", totalToReceive);
        summary.put("billsPaid", billsPaid);
        summary.put("percentExpenditure", percentExpenditure);
        summary.put("billBreakdown", billBreakdown);
        summary.put("comparativeAnalysis", comparativeAnalysis);
        summary.put("debts", debts);  // Add debts to the summary

        return summary;
    }


    public double calculateTotalSpent(List<Bill> bills, Long memberId) {
        return bills.stream()
                .filter(bill -> bill.getPaidByMember().getMemberId().equals(memberId))
                .mapToDouble(Bill::getBillAmount)
                .sum();
    }
    public double calculateTotalOwed(List<Bill> bills, Long memberId) {
        // Calculate how much the member owes to others
        return bills.stream()
                .flatMapToDouble(bill -> bill.getBill_all_expenses().stream()
                        .filter(expense -> expense.getMember().getMemberId().equals(memberId) && !bill.getPaidByMember().getMemberId().equals(memberId))
                        .mapToDouble(Expense::getShare))
                .sum();
    }
    public double calculateTotalToReceive(List<Bill> bills, Long memberId) {
        // Calculate how much others owe to the member
        return bills.stream()
                .flatMapToDouble(bill -> bill.getBill_all_expenses().stream()
                        .filter(expense -> !expense.getMember().getMemberId().equals(memberId) && bill.getPaidByMember().getMemberId().equals(memberId))
                        .mapToDouble(Expense::getShare))
                .sum();
    }
    public List<Map<String, Object>> getBillsPaid(List<Bill> bills, Long memberId) {
        return bills.stream()
                .filter(bill -> bill.getPaidByMember().getMemberId().equals(memberId))
                .map(bill -> {
                    Map<String, Object> billData = new HashMap<>();
                    billData.put("billName", bill.getDescription());
                    billData.put("amountPaid", bill.getBillAmount());
                    return billData;
                })
                .collect(Collectors.toList());
    }
    public List<Map<String, Object>> getBillBreakdown(List<Bill> bills, Long memberId) {
        return bills.stream()
                .flatMap(bill -> bill.getBill_all_expenses().stream())
                .filter(expense -> expense.getMember().getMemberId().equals(memberId))
                .map(expense -> {
                    Map<String, Object> breakdownData = new HashMap<>();
                    breakdownData.put("billName", expense.getBill().getDescription());
                    breakdownData.put("amount", expense.getShare());
                    return breakdownData;
                })
                .collect(Collectors.toList());
    }
    public double calculatePercentExpenditure(double totalCost, double totalexpenditure) {
        return (totalexpenditure / totalCost) * 100;
    }
    public Map<Long, Map<String, Object>> getComparativeAnalysis(List<Bill> bills, Long memberId) {
        Map<Long, Map<String, Object>> comparativeAnalysis = new HashMap<>();

        // Stream through all bills, find distinct member IDs and then map to their total expenditure and name
        bills.stream()
                .flatMap(bill -> bill.getBill_all_expenses().stream())
                .map(expense -> expense.getMember().getMemberId())
                .distinct()  // Get distinct member IDs involved in the trip
                .filter(id -> !id.equals(memberId))  // Exclude the provided memberId
                .forEach(id -> {
                    double totalExpenditureByOthers = calculateTotalExpenditureForMember(bills, id);
                    String memberName = getMemberName(id); // Fetch member name based on memberId

                    // Create a map for storing name and total expenditure
                    Map<String, Object> memberData = new HashMap<>();
                    memberData.put("memberName", memberName);
                    memberData.put("totalExpenditure", totalExpenditureByOthers);

                    // Add to the comparative analysis map
                    comparativeAnalysis.put(id, memberData);
                });

        return comparativeAnalysis;
    }

    public String getMemberName(Long memberId) {
        return memRepo.findById(memberId).map(Member::getName).orElse("Unknown");
    }
    public double calculateTotalExpenditureForMember(List<Bill> bills, Long memberId) {
        // Calculate total expenditure based on the member's share in all bills
        return bills.stream()
                .flatMap(bill -> bill.getBill_all_expenses().stream())
                .filter(expense -> expense.getMember().getMemberId().equals(memberId))
                .mapToDouble(Expense::getShare)
                .sum();
    }
    public List<Map<String, Object>> calculateTotalDebts(List<Bill> bills, Long memberId) {
        List<Map<String, Object>> debts = new ArrayList<>();
        double totalOwed = 0;
        double totalToReceive = 0;
        Map<Long, Double> netAmounts = new HashMap<>();

        for (Bill bill : bills) {
            Long creditorId = bill.getPaidByMember().getMemberId();

            for (Expense expense : bill.getBill_all_expenses()) {
                Long debtorId = expense.getMember().getMemberId();
                double amount = expense.getShare();

                if (debtorId.equals(memberId) && !creditorId.equals(memberId)) {
                    totalOwed += amount;
                    netAmounts.put(creditorId, netAmounts.getOrDefault(creditorId, 0.0) - amount);
                } else if (creditorId.equals(memberId) && !debtorId.equals(memberId)) {
                    totalToReceive += amount;
                    netAmounts.put(debtorId, netAmounts.getOrDefault(debtorId, 0.0) + amount);
                }
            }
        }

        // Add net amounts to the debts list using the createDebtEntry method
        for (Map.Entry<Long, Double> entry : netAmounts.entrySet()) {
            debts.add(createDebtEntry(entry.getKey(), entry.getValue()));
        }

        return debts;
    }

    // Helper method to create the debt entry
    private Map<String, Object> createDebtEntry(Long memberId, double amount) {
        return Map.of(
                "memberId", memberId,
                "netAmount", amount
        );
    }









    //Get total cost of trip
    public double calculateTotalCost(List<Bill> bills) {
        return bills.stream()
                .mapToDouble(Bill::getBillAmount)
                .sum();
    }
    //Get cost breakdown by bill
    public List<Map<String, Object>> calculateCostBreakdownByBill(List<Bill> bills) {
        return bills.stream()
                .map(bill -> {
                    Map<String, Object> breakdown = new HashMap<>();
                    breakdown.put("billName", bill.getDescription());
                    breakdown.put("amount", bill.getBillAmount());
                    return breakdown;
                })
                .collect(Collectors.toList());
    }
    //Get Each Member total expenditure in trip
    public Map<Long, Double> calculateEachMemberTotalExpenditure(List<Bill> bills){
        Map<Long, Double> memberExpenditures = new HashMap<>();

        for (Bill bill : bills) {
            for (Expense expense : bill.getBill_all_expenses()) {
                Long memberId = expense.getMember().getMemberId();
                double amount = expense.getShare();

                memberExpenditures.put(memberId, memberExpenditures.getOrDefault(memberId, 0.0) + amount);
            }
        }

        return memberExpenditures;
    }
    //Get each member percentage of total cost
    public Map<Long, Double> calculateMemberPercentages(Map<Long, Double> memberExpenditures, double totalCost) {
        Map<Long, Double> memberPercentages = new HashMap<>();

        for (Map.Entry<Long, Double> entry : memberExpenditures.entrySet()) {
            Long memberId = entry.getKey();
            double expenditure = entry.getValue();
            double percentage = (expenditure / totalCost) * 100;
            memberPercentages.put(memberId, percentage);
        }

        return memberPercentages;
    }
    //Get the largest single bill
    public Bill findLargestSingleBill(List<Bill> bills) {
        return bills.stream()
                .max(Comparator.comparingDouble(Bill::getBillAmount))
                .orElse(null);
    }
    //Get member names
    private Map<Long, String> fetchMemberNames(Set<Long> memberIds) {
        // Assuming you have a method to get member details by their IDs
        return memRepo.findByMemberIdIn(memberIds).stream()
                .collect(Collectors.toMap(Member::getMemberId, Member::getName));
    }



}
