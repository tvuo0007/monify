package com.thephong.moneymanager.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thephong.moneymanager.dto.IncomeDTO;
import com.thephong.moneymanager.entity.CategoryEntity;
import com.thephong.moneymanager.entity.IncomeEntity;
import com.thephong.moneymanager.entity.ProfileEntity;
import com.thephong.moneymanager.repository.CategoryRepository;
import com.thephong.moneymanager.repository.IncomeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;
    private final IncomeRepository incomeRepository;

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepository.findById(incomeDTO.getCategoryId())
                                                .orElseThrow(() -> new RuntimeException("Category not found"));
        IncomeEntity newIncome = toEntity(incomeDTO, profile, category);
        newIncome = incomeRepository.save(newIncome);
        return toDTO(newIncome);
    }

    // Retrieve all incomes for current month based on startDate and endDate
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate starDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> incomeList = incomeRepository.findByProfileIdAndDateBetween(profile.getId(), starDate, endDate);
        return incomeList.stream().map(this::toDTO).toList();
    }

    public void deleteIncome(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity income = incomeRepository.findById(incomeId)
                        .orElseThrow(() -> new RuntimeException("Income not found"));
        if (!income.getProfile().getId().equals(profile.getId())) {
            throw new RuntimeException("Unauthorized to delete this income");
        }
        incomeRepository.delete(income);
    }

    private IncomeEntity toEntity(IncomeDTO incomeDTO, ProfileEntity profileEntity, CategoryEntity categoryEntity) {
        return IncomeEntity.builder()
                            .name(incomeDTO.getName())
                            .icon(incomeDTO.getIcon())
                            .amount(incomeDTO.getAmount())
                            .date(incomeDTO.getDate())
                            .profile(profileEntity)
                            .category(categoryEntity)
                            .build();
    }

    private IncomeDTO toDTO(IncomeEntity incomeEntity) {
        return IncomeDTO.builder()
                        .id(incomeEntity.getId())
                        .name(incomeEntity.getName())
                        .icon(incomeEntity.getIcon())
                        .categoryId(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getId() : null)
                        .categoryName(incomeEntity.getCategory() != null ? incomeEntity.getCategory().getName() : "N/A")
                        .amount(incomeEntity.getAmount())
                        .date(incomeEntity.getDate())
                        .createdAt(incomeEntity.getCreatedAt())
                        .updatedAt(incomeEntity.getUpdatedAt())
                        .build();
    }
}