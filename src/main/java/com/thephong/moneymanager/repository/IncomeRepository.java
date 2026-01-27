package com.thephong.moneymanager.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thephong.moneymanager.entity.IncomeEntity;

public interface IncomeRepository extends JpaRepository<IncomeEntity, Long>{
    @Query(value = "select * from tbl_incomes where profile_id = ?1 order by date desc", nativeQuery = true)
    List<IncomeEntity> findByProfileIdOrderByDateDesc(Long profileId);

    @Query(value = "select * from tbl_incomes where profile_id = ?1 order by date desc limit 5", nativeQuery = true)
    List<IncomeEntity> findTop5ByProfileIdOrderByDateDesc(Long profileId);

    @Query("select sum(i.amount) from IncomeEntity i where i.profile.id = :profileId")
    BigDecimal findTotalIncomeByProfileId(@Param("profileId") Long profileId);

    @Query(value = "select * from tbl_incomes where profile_id = ?1 and date between ?2 and ?3 and name like ?4", nativeQuery = true)
    List<IncomeEntity> findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
        Long profileId,
        LocalDate startDate,
        LocalDate endDate,
        String keyword,
        Sort sort
    );

    @Query(value = "select * from tbl_incomes where profile_id = ?1 and date between ?2 and ?3", nativeQuery = true)
    List<IncomeEntity> findByProfileIdAndDateBetween(Long profileId, LocalDate startDate, LocalDate endDate);
}
