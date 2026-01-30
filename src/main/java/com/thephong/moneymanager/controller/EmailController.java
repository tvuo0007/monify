package com.thephong.moneymanager.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thephong.moneymanager.entity.ProfileEntity;
import com.thephong.moneymanager.service.EmailService;
import com.thephong.moneymanager.service.ExcelService;
import com.thephong.moneymanager.service.ExpenseService;
import com.thephong.moneymanager.service.IncomeService;
import com.thephong.moneymanager.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {
    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final EmailService emailService;
    private final ProfileService profileService;
    private final ExcelService excelService;

    @GetMapping("/income-excel")
    public ResponseEntity<Void> emailIncomeExcel() throws IOException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.writeIncomesToExcel(outputStream, incomeService.getCurrentMonthIncomesForCurrentUser());
        emailService.sendEmailWithAttachment(
            profile.getEmail(),
            "Your Income Excel Report",
            "Please find attached your income report for the current month.",
            outputStream.toByteArray(),
            "incomes.xlsx"
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/expense-excel")
    public ResponseEntity<Void> emailExpenseExcel() throws IOException {
        ProfileEntity profile = profileService.getCurrentProfile();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        excelService.writeExpensesToExcel(outputStream, expenseService.getCurrentMonthExpensesForCurrentUser());
        emailService.sendEmailWithAttachment(
            profile.getEmail(),
            "Your Expense Excel Report",
            "Please find attached your expense report for the current month.",
            outputStream.toByteArray(),
            "expenses.xlsx"
        );
        return ResponseEntity.ok().build();
    }
}
