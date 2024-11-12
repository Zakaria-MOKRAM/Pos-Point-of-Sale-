package ma.jway.rms.repositories;

import ma.jway.rms.dto.models.User;
import ma.jway.rms.dto.models.WaiterAreas;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WaiterAreasRepository extends JpaRepository<WaiterAreas, Long> {
    List<WaiterAreas> findByUser(User user);

}