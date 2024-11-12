package ma.jway.rms.repositories;

import ma.jway.rms.dto.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT i FROM Item i WHERE i.saleStatus = true")
    List<Item> findSalableItems();

    @Query("SELECT i FROM Item i WHERE i.purchaseStatus = true")
    List<Item> findPurchasableItems();
}