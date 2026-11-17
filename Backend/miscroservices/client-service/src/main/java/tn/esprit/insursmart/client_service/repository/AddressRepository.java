package tn.esprit.insursmart.client_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.insursmart.client_service.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
}