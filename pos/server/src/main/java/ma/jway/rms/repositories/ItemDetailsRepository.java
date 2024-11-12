package ma.jway.rms.repositories;

import ma.jway.rms.dto.models.Item;
import ma.jway.rms.dto.models.ItemDetails;
import ma.jway.rms.dto.models.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemDetailsRepository extends JpaRepository<ItemDetails, Long> {
    @Query("SELECT ide FROM ItemDetails ide WHERE ide.item = ?1")
    List<ItemDetails> findByItem(Item item);

    @Query("SELECT ide FROM ItemDetails ide WHERE ide.item = ?1 AND ide.resource = ?2")
    ItemDetails findByItemAndResource(Item item, Resource resource);
}