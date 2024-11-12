package ma.jway.rms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.jway.rms.dto.models.Resource;

@Repository
public interface IngredientRepository extends JpaRepository<Resource, Long> {

}