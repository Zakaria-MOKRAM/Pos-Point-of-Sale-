package ma.jway.rms.repositories;


import ma.jway.rms.dto.models.Area;
import ma.jway.rms.dto.models.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    @Query("SELECT dt FROM DiningTable dt WHERE dt.area = ?1")
    List<DiningTable> findByArea(Area area);
}
