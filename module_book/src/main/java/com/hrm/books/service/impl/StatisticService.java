package com.hrm.books.service.impl;

import com.hrm.books.repository.BillRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StatisticService {
    final BillRepository billRepository;

    public Object getByDay() {
        List<Map<Object, Object>> o = billRepository.getStatisticByDay();
        return o;
    }

    public Object getByMonth(int month, int year) {
        if (year == 0) year = LocalDate.now().getYear();
        List<Map<Object, Object>> o = billRepository.getStatisticByMonth(month, year);
        return o;
    }

    public Object getByYear(int year) {
        if (year == 0) year = LocalDate.now().getYear();
        List<Map<Object, Object>> o = billRepository.getStatisticByYear(year);
        return o;
    }
}
