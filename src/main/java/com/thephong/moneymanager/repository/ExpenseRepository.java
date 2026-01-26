package com.thephong.moneymanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thephong.moneymanager.entity.ExpenseEntity;

public interface ExpenseRepository extends JpaRepository<ExpenseEntity, Long>{
    @Query(value = "select * from tbl_expenses where profile_id = ?1 order by date desc", nativeQuery = true)
    List<ExpenseEntity> findByProfileIdOrderByDateDesc(Long profileId);

    @Query(value = "select * from tbl_expenses where profile_id = ?1 order by date desc limit 5", nativeQuery = true)
    List<ExpenseEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("select sum(e.amount) from ExpenseEntity e where e.profile.id = :profileId")
    BigDecimal findTotalExpenseByProfileId(@Param("profileId") Long profileId);

    @Query(value = "select * from tbl_expenses where profile_id = ?1 and date between ?2 and ?3 and name like ?4", nativeQuery = true)
    List<ExpenseEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
        Long profileId,
        LocalDate startDate,
        LocalDate endDate,
        String keyword,
        Sort sort
    );

    @Query(value = "select * from tbl_expenses where profile_id = ?1 and date between ?2 and ?3", nativeQuery = true)
    List<ExpenseEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
