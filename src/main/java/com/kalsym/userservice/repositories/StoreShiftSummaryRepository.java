package com.kalsym.userservice.repositories;

import com.kalsym.userservice.models.daos.StoreShiftSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 *
 * @author Sarosh
 */
public interface StoreShiftSummaryRepository extends JpaRepository<StoreShiftSummary, String> {
    
    @Query(value = "SELECT id, created FROM `order` "
            + "WHERE staffId=:staffId "
            + "AND isClosed=0 "
            + "AND created > DATE_SUB(NOW(),INTERVAL 24 HOUR)"
            + "ORDER BY created LIMIT 1"
            , nativeQuery = true)
    List<Object[]> getFirstOrder(@Param("staffId") String staffId);
    
    @Query(value = "SELECT id, created FROM `order` "
            + "WHERE staffId=:staffId "
            + "AND isClosed=0 "
            + "AND created > DATE_SUB(NOW(),INTERVAL 24 HOUR)"
            + "ORDER BY created DESC LIMIT 1"
            , nativeQuery = true)
    List<Object[]> getLastOrder(@Param("staffId") String staffId);
    
    @Query(value = "SELECT SUM(total), paymentChannel FROM `order` "
            + "WHERE staffId=:staffId "
            + "AND isClosed=0 "
            + "AND created > DATE_SUB(NOW(),INTERVAL 24 HOUR)"
            + "GROUP BY paymentChannel"
            , nativeQuery = true)
    List<Object[]> getOrderSummary(@Param("staffId") String staffId);
    
    @Transactional 
    @Modifying
    @Query(value = "UPDATE `order` SET isClosed=1 WHERE id = :orderId", nativeQuery = true) 
    void UpdateOrderClose(
            @Param("orderId") String orderId
            );
      
}
