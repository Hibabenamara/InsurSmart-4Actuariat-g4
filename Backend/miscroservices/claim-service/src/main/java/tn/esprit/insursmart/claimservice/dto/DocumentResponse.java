package tn.esprit.insursmart.claimservice.dto;

import tn.esprit.insursmart.claimservice.entity.ClaimDocument;

import java.time.LocalDateTime;

public record DocumentResponse(Long id, String fileName, String fileUrl, LocalDateTime uploadedAt) {
    public static DocumentResponse from(ClaimDocument d) {
        return new DocumentResponse(d.getId(), d.getFileName(), d.getFileUrl(), d.getUploadedAt());
    }
}
