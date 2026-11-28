package tn.esprit.insursmart.client_service.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit.insursmart.client_service.dto.ClientRequest;
import tn.esprit.insursmart.client_service.dto.ClientResponse;
import tn.esprit.insursmart.client_service.entity.ClientStatus;

import java.util.List;

public interface ClientService {
    ClientResponse create(ClientRequest request);
    List<ClientResponse> getAll();
    Page<ClientResponse> search(String search, Pageable pageable);
    ClientResponse getById(Long id);
    ClientResponse getByKeycloakUserId(String keycloakUserId);
    ClientResponse update(Long id, ClientRequest request);
    ClientResponse updateStatus(Long id, ClientStatus status);
    void delete(Long id);
}