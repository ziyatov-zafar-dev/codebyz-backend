package uz.codebyz.message.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class MessageStorageService {

    private static final String BASE_DIR = "files/messages";

    public StoredFile store(MultipartFile file) {
        try {
            LocalDate today = LocalDate.now();
            String timeFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss"));

            File dir = new File(BASE_DIR + "/" +
                    today.getYear() + "/" +
                    today.getMonthValue() + "/" +
                    today.getDayOfMonth() + "/" +
                    timeFolder
            );
            if (!dir.exists()) dir.mkdirs();

            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.contains(".")) {
                ext = original.substring(original.lastIndexOf("."));
            }

            String filename = UUID.randomUUID() + ext;
            File target = new File(dir, filename);
            Files.copy(file.getInputStream(), target.toPath());

            StoredFile sf = new StoredFile();
            sf.fileUrl = "/files/messages/" +
                    today.getYear() + "/" +
                    today.getMonthValue() + "/" +
                    today.getDayOfMonth() + "/" +
                    timeFolder + "/" +
                    filename;

            sf.originalName = original != null ? original : filename;
            sf.size = file.getSize();
            return sf;

        } catch (Exception e) {
            throw new RuntimeException("Dosya yüklenemedi: " + e.getMessage());
        }
    }

    public static class StoredFile {
        public String fileUrl;
        public String originalName;
        public long size;
    }
}
