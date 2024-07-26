package com.hrm.books.repository;

import com.hrm.books.models.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public interface BillRepository extends JpaRepository<Bill, UUID> {
//    @Query(nativeQuery = true, value = "WITH hours AS (SELECT generate_series(0, 23) AS hour) " +
//            "SELECT h.hour AS hour," +
//            "    COALESCE(COUNT(b.id), 0) AS total_bills," +
//            "    COALESCE(SUM(b.total_price), 0) AS total_price " +
//            "FROM hours h " +
//            "LEFT JOIN bill b " +
//            "        ON EXTRACT(HOUR FROM b.create_at) = h.hour" +
//            "        AND DATE(b.create_at) = CURRENT_DATE " +
//            "GROUP BY" +
//            "    h.hour, b.create_at " +
//            "ORDER BY" +
//            "    h.hour;")
    @Query(nativeQuery = true, value = "SELECT hour, total_bill, total_price, total_amount FROM vw_statistic24H;")
    List<Map<Object, Object>> getStatisticByDay();

//    @Query(nativeQuery = true, value = "WITH days AS (SELECT generate_series(1, 31) AS day) " +
//            "SELECT d.day," +
//            "    COALESCE(COUNT(b.id), 0) AS total_bills," +
//            "    COALESCE(SUM(b.total_price), 0) AS total_price " +
//            "FROM days d " +
//            "LEFT JOIN bill b " +
//            "       ON EXTRACT(DAY FROM b.create_at) = d.day" +
//            "       AND EXTRACT(MONTH FROM b.create_at) = ?1 " +
//            "       AND EXTRACT(YEAR FROM b.create_at) = ?2 " +
//            "GROUP BY" +
//            "    d.day " +
//            "ORDER BY" +
//            "    d.day;")
    @Query(nativeQuery = true, value = "SELECT day, total_bill, total_price, total_amount from fc_statistic_by_month(?1,?2);")
    List<Map<Object, Object>> getStatisticByMonth(int month, int year);

//    @Query(nativeQuery = true, value = "WITH months AS (SELECT generate_series(1, 12) AS month) " +
//            "SELECT m.month," +
//            "    COALESCE(COUNT(b.id), 0) AS total_bills," +
//            "    COALESCE(SUM(b.total_price), 0) AS total_price " +
//            "FROM months m " +
//            "LEFT JOIN bill b" +
//            "       ON EXTRACT(MONTH FROM b.create_at) = m.month " +
//            "       AND EXTRACT(YEAR FROM b.create_at) = ?1 " +
//            "GROUP BY m.month " +
//            "ORDER BY m.month;")
    @Query(nativeQuery = true, value = "SELECT month, total_bill, total_price, total_amount FROM fn_statistic_by_year(?1);")
    List<Map<Object, Object>> getStatisticByYear(int year);
}
