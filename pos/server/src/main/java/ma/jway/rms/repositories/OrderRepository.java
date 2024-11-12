package ma.jway.rms.repositories;

import ma.jway.rms.dto.models.DiningTable;
import ma.jway.rms.dto.models.Order;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query("SELECT o FROM Order o WHERE o.diningTable = ?1 ORDER BY o.createdAt DESC")
    List<Order> findByDiningTable(DiningTable diningTable);
}