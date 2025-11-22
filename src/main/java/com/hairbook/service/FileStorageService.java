package com.hairbook.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Service de gestion du stockage des fichiers.
 * <p>
 * Gère l'upload et la suppression des fichiers (photos de profil, etc.).
 * </p>
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    /**
     * Stocke un fichier uploadé et retourne le chemin relatif.
     *
     * @param file fichier à stocker
     * @param subDirectory sous-répertoire (ex: "profiles")
     * @return chemin relatif du fichier stocké
     * @throws IOException si une erreur survient lors du stockage
     * @throws IllegalArgumentException si le fichier est invalide
     */
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        // Validation
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot store empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size (5MB)");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("File must have a name");
        }

        // Vérifier l'extension
        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!isAllowedExtension(extension)) {
            throw new IllegalArgumentException("File type not allowed. Allowed types: jpg, jpeg, png, gif, webp");
        }

        // Générer un nom de fichier unique
        String filename = UUID.randomUUID().toString() + extension;

        // Créer le répertoire de destination s'il n'existe pas
        Path uploadPath = Paths.get(uploadDir, subDirectory);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Copier le fichier
        Path targetLocation = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        log.info("File stored: {}", targetLocation);

        // Retourner le chemin relatif
        return subDirectory + "/" + filename;
    }

    /**
     * Supprime un fichier.
     *
     * @param relativePath chemin relatif du fichier à supprimer
     * @throws IOException si une erreur survient lors de la suppression
     */
    public void deleteFile(String relativePath) throws IOException {
        if (relativePath == null || relativePath.isEmpty()) {
            return;
        }

        Path filePath = Paths.get(uploadDir, relativePath);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            log.info("File deleted: {}", filePath);
        }
    }

    /**
     * Extrait l'extension d'un nom de fichier.
     *
     * @param filename nom du fichier
     * @return extension (avec le point)
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * Vérifie si une extension est autorisée.
     *
     * @param extension extension à vérifier
     * @return true si autorisée
     */
    private boolean isAllowedExtension(String extension) {
        for (String allowed : ALLOWED_EXTENSIONS) {
            if (allowed.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }
}
