package ma.jway.rms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ma.jway.rms.dto.models.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

}