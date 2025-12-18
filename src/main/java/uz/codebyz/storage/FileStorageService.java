package uz.codebyz.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final StorageProperties props;

    public FileStorageService(StorageProperties props) {
        this.props = props;
    }

    public StoredFile store(MultipartFile file, String subdir) throws IOException {
        Path root = Path.of(props.getUploadsDir()).toAbsolutePath().normalize();
        Path dir = root.resolve(subdir).normalize();
        Files.createDirectories(dir);

        String original = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
        String safeOriginal = original.replaceAll("[^a-zA-Z0-9._-]", "_");
        String name = UUID.randomUUID() + "_" + safeOriginal;

        Path target = dir.resolve(name).normalize();
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String url = props.getPublicBaseUrl() + "/" + subdir + "/" + name;
        return new StoredFile(url, target.toString(), safeOriginal, file.getSize());
    }

    public static class StoredFile {
        private final String publicUrl;
        private final String absolutePath;
        private final String originalFileName;
        private final long size;

        public StoredFile(String publicUrl, String absolutePath, String originalFileName, long size) {
            this.publicUrl = publicUrl;
            this.absolutePath = absolutePath;
            this.originalFileName = originalFileName;
            this.size = size;
        }

        public String getPublicUrl() { return publicUrl; }
        public String getAbsolutePath() { return absolutePath; }
        public String getOriginalFileName() { return originalFileName; }
        public long getSize() { return size; }
    }
}
