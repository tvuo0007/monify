package com.thephong.moneymanager.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class FilterDTO {
    private String type;
    private LocalDate startDate;
    private LocalDate endDate;
    private String keyword;
    private String sortField; // e.g., "date", "amount"
    private String sortOrder; // "asc" or "desc"
}
