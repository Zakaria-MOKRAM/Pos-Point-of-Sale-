package ma.jway.rms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ma.jway.rms.dto.models.Item;
import ma.jway.rms.dto.models.ItemComposition;

@Repository
public interface ItemCompositionRepository extends JpaRepository<ItemComposition, Long> {
    @Query("SELECT ic FROM ItemComposition ic WHERE ic.parentItem = ?1")
    List<ItemComposition> findByParentItem(Item parentItem);
}