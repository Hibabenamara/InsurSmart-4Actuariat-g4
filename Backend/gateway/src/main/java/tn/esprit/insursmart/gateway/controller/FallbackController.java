package tn.esprit.insursmart.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping("/fallback/contract")
    public ResponseEntity<Map<String, Object>> contractFallback() {
        return ResponseEntity.ok(Map.of(
                "message", "Service Contrat temporairement indisponible",
                "degraded", true));
    }
}