package ua.edu.ukma.cleaning.storage;

import lombok.extern.slf4j.Slf4j;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

@Slf4j
@Service
public class StorageService {
    private final MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
    @Value("${storage.root-dir:C:/resources/storage}")
    private String rootDir;
    public void storeFile(Storageable storageable, MultipartFile file) {
        validateFile(storageable, file);
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File not found");
        }
        try {
            Path destDir = createDestinationDirectory(storageable);
            String extension = getFileExtension(file.getOriginalFilename());
            Path destinationFile = destDir.resolve(storageable.getId() + extension).normalize();
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to store file: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to store file", e);
        }
    }
    public ResourceWithType loadAsResource(Storageable storageable) {
        for (String mimeType : storageable.getAllowedFileTypes()) {
            Optional<Resource> resource = findResource(storageable, mimeType);
            if (resource.isPresent()) {
                return new ResourceWithType(resource.get(), mimeType);
            }
        }
        throw new RuntimeException("File not found for dir: " + storageable.getDir() + " and id: " + storageable.getId());
    }
    private Optional<Resource> findResource(Storageable storageable, String mimeType) {
        Path filePath = Paths.get(rootDir, storageable.getDir())
                .resolve(storageable.getId() + getExtensionOfMime(mimeType)).toAbsolutePath();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return Optional.of(resource);
            }
        } catch (MalformedURLException e) {
            log.error("Error loading file as resource: {}", e.getMessage(), e);
        }
        return Optional.empty();
    }
    private Path createDestinationDirectory(Storageable storageable) throws IOException {
        Path destinationDir = Paths.get(rootDir).resolve(storageable.getDir()).normalize().toAbsolutePath();
        Files.createDirectories(destinationDir);
        return destinationDir;
    }
    private void validateFile(Storageable storageable, MultipartFile file) {
        String contentType = Optional.ofNullable(file.getContentType())
                .orElseThrow(() -> new IllegalArgumentException("File type cannot be null"));
        if (!storageable.getAllowedFileTypes().contains(contentType)) {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        throw new IllegalArgumentException("Unsupported file type: " + filename);
    }
    private String getExtensionOfMime(String mimeType) {
        try {
            MimeType mimeTypeObj = allTypes.forName(mimeType);
            return mimeTypeObj.getExtension();
        } catch (MimeTypeException e) {
            throw new RuntimeException("Failed to get extension for mime type: " + mimeType, e);
        }
    }
}
