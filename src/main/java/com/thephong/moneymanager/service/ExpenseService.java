package com.thephong.moneymanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.thephong.moneymanager.dto.ExpenseDTO;
import com.thephong.moneymanager.entity.CategoryEntity;
import com.thephong.moneymanager.entity.ExpenseEntity;
import com.thephong.moneymanager.entity.ProfileEntity;
import com.thephong.moneymanager.repository.CategoryRepository;
import com.thephong.moneymanager.repository.ExpenseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(expenseDTO.getCategoryId())
                                                .orElseThrow(() -> new RuntimeException("Category not found"));
        ExpenseEntity newExpense = toEntity(expenseDTO, profile, category);
        newExpense = expenseRepository.save(newExpense);
        return toDTO(newExpense);
    }

    // Retrieve all expenses for current month based on startDate and endDate
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate starDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> expenseList = expenseRepository.findByProfileIdAndDateBetween(profile.getId(), starDate, endDate);
        return expenseList.stream().map(this::toDTO).toList();
    }

    public void deleteExpense(Long expenseId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity expense = expenseRepository.findById(expenseId)
                        .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this expense");
        }
        expenseRepository.delete(expense);
    }

    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenseList = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenseList.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal totalExpenses = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return totalExpenses != null ? totalExpenses : BigDecimal.ZERO;
    }

    // filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenseList = expenseRepository.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
            profile.getId(),
            startDate,
            endDate,
            keyword,
            sort
        );
        return expenseList.stream().map(this::toDTO).toList();
    }

    private ExpenseEntity toEntity(ExpenseDTO expenseDTO, ProfileEntity profileEntity, CategoryEntity categoryEntity) {
        return ExpenseEntity.builder()
                            .name(expenseDTO.getName())
                            .icon(expenseDTO.getIcon())
                            .amount(expenseDTO.getAmount())
                            .date(expenseDTO.getDate())
                            .profile(profileEntity)
                            .category(categoryEntity)
                            .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity expenseEntity) {
        return ExpenseDTO.builder()
                        .id(expenseEntity.getId())
                        .name(expenseEntity.getName())
                        .icon(expenseEntity.getIcon())
                        .categoryId(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getId() : null)
                        .categoryName(expenseEntity.getCategory() != null ? expenseEntity.getCategory().getName() : "N/A")
                        .amount(expenseEntity.getAmount())
                        .date(expenseEntity.getDate())
                        .createdAt(expenseEntity.getCreatedAt())
                        .updatedAt(expenseEntity.getUpdatedAt())
                        .build();
    }
}