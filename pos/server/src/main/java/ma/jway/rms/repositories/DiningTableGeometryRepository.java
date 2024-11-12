package ma.jway.rms.repositories;


import ma.jway.rms.dto.models.DiningTable;
import ma.jway.rms.dto.models.DiningTableGeometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiningTableGeometryRepository extends JpaRepository<DiningTableGeometry, Long> {
    @Query("SELECT dtg FROM DiningTableGeometry dtg WHERE dtg.diningTable = ?1")
    DiningTableGeometry findByDiningTable(DiningTable diningTable);
}
