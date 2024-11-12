package ma.jway.rms.repositories;

import ma.jway.rms.dto.models.AddsOn;
import ma.jway.rms.dto.models.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddsOnRepository extends JpaRepository<AddsOn, Long> {
    @Query("SELECT ao FROM AddsOn ao WHERE ao.parentItem = ?1")
    List<AddsOn> findByParentItem(Item parentItem);
}