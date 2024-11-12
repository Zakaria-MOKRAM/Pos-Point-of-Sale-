package ma.jway.rms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.jway.rms.dto.models.ItemDetails;

@Repository
public interface ItemIngredientsRepository extends JpaRepository<ItemDetails, Long> {

}