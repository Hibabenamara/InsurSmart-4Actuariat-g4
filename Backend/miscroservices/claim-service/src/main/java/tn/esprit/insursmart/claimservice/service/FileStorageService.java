package tn.esprit.insursmart.claimservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.insursmart.claimservice.exception.BadRequestException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${claim.upload-dir:uploads}") String uploadDir) {
        this.root = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /** Enregistre le fichier sur disque et retourne son chemin (fileUrl). */
    public String store(Long claimId, MultipartFile file) {
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "piece" : file.getOriginalFilename());
        if (original.contains("..")) {
            throw new BadRequestException("Nom de fichier invalide");
        }
        try {
            Path dir = root.resolve("claims").resolve(String.valueOf(claimId));
            Files.createDirectories(dir);
            Path target = dir.resolve(UUID.randomUUID() + "_" + original);
            try (InputStream in = file.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            return target.toString();
        } catch (IOException e) {
            throw new IllegalStateException("Impossible d'enregistrer le fichier", e);
        }
    }
}
