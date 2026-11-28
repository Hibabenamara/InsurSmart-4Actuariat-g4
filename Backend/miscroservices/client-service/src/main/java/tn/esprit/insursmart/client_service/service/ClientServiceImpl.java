package tn.esprit.insursmart.client_service.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.insursmart.client_service.dto.ClientRequest;
import tn.esprit.insursmart.client_service.dto.ClientResponse;
import tn.esprit.insursmart.client_service.entity.*;
import tn.esprit.insursmart.client_service.exception.ConflictException;
import tn.esprit.insursmart.client_service.exception.ResourceNotFoundException;
import tn.esprit.insursmart.client_service.mapper.ClientMapper;
import tn.esprit.insursmart.client_service.repository.ClientRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    @Override
    public ClientResponse create(ClientRequest request) {
        if (clientRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Un client avec cet email existe déjà : " + request.getEmail());
        }
        if (clientRepository.existsByKeycloakUserId(request.getKeycloakUserId())) {
            throw new ConflictException("Un client avec ce keycloakUserId existe déjà : " + request.getKeycloakUserId());
        }
        Client saved = clientRepository.save(clientMapper.toEntity(request));
        return clientMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientResponse> getAll() {
        return clientRepository.findAll().stream()
                .map(clientMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClientResponse> search(String search, Pageable pageable) {
        return clientRepository.searchClients(search, pageable)
                .map(clientMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));
        return clientMapper.toResponse(client);
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponse getByKeycloakUserId(String keycloakUserId) {
        Client client = clientRepository.findByKeycloakUserId(keycloakUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable pour keycloakUserId : " + keycloakUserId));
        return clientMapper.toResponse(client);
    }

    @Override
    public ClientResponse update(Long id, ClientRequest request) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));

        if (!existing.getEmail().equalsIgnoreCase(request.getEmail())
                && clientRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email déjà utilisé : " + request.getEmail());
        }

        existing.setFirstName(request.getFirstName());
        existing.setLastName(request.getLastName());
        existing.setEmail(request.getEmail());
        existing.setPhone(request.getPhone());
        existing.setBirthDate(request.getBirthDate());

        if (request.getAddress() != null) {
            Address addr = existing.getAddress() != null ? existing.getAddress() : new Address();
            addr.setStreet(request.getAddress().getStreet());
            addr.setCity(request.getAddress().getCity());
            addr.setPostalCode(request.getAddress().getPostalCode());
            addr.setCountry(request.getAddress().getCountry());
            addr.setClient(existing);
            existing.setAddress(addr);
        }

        if (request.getPreferences() != null) {
            CustomerPreferences prefs = existing.getPreferences() != null
                    ? existing.getPreferences()
                    : new CustomerPreferences();
            if (request.getPreferences().getNotificationChannel() != null) {
                prefs.setNotificationChannel(request.getPreferences().getNotificationChannel());
            }
            if (request.getPreferences().getLanguage() != null) {
                prefs.setLanguage(request.getPreferences().getLanguage());
            }
            prefs.setClient(existing);
            existing.setPreferences(prefs);
        }

        return clientMapper.toResponse(clientRepository.save(existing));
    }

    @Override
    public ClientResponse updateStatus(Long id, ClientStatus status) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));
        existing.setStatus(status);
        return clientMapper.toResponse(clientRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        Client existing = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client introuvable avec l'id : " + id));
        clientRepository.delete(existing);
    }
}