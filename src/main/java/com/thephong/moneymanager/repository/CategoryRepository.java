package com.thephong.moneymanager.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.thephong.moneymanager.entity.CategoryEntity;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long>{
    @Query(value = "select * from tbl_categories c where c.profile_id = ?1", nativeQuery = true)
    List<CategoryEntity> findByProfileId(Long profileId);

    @Query(value = "select * from tbl_categories c where c.id = ?1 and c.profile_id = ?2", nativeQuery = true)
    Optional<CategoryEntity> findByIdAndProfileId(Long id, Long profileId);

    @Query(value = "select * from tbl_categories c where c.type = ?1 and c.profile_id = ?2", nativeQuery = true)
    List<CategoryEntity> findByTypeAndProfileId(String type, Long profileId);

    Boolean existsByNameAndProfileId(String name, Long profileId);
}
