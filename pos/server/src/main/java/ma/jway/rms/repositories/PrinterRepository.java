package ma.jway.rms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ma.jway.rms.dto.models.Printer;
import ma.jway.rms.dto.models.Station;

@Repository
public interface PrinterRepository extends JpaRepository<Printer, Long> {
    @Query("SELECT p FROM Printer p WHERE p.station = ?1")
    Printer findByStation(Station station);
}