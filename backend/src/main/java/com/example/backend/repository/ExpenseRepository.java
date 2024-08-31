package com.example.backend.repository;

import com.example.backend.domain.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense,Long> {
    List<Expense> findByBill_BillId(Long billId);
}
