package uz.codebyz.helper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uz.codebyz.common.ErrorCode;
import uz.codebyz.common.Helper;
import uz.codebyz.common.ResponseDto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class FileHelper {

    @Value("${storage.base-url}")
    private String publicBaseUrl;

    public ResponseDto<UploadFileResponseDto> uploadFile(MultipartFile file, String folder) {

        if (file == null || file.isEmpty()) {
            return ResponseDto.fail(
                    400,
                    ErrorCode.VALIDATION_ERROR,
                    "Fayl topilmadi yoki bo‘sh"
            );
        }

        try {
            Path folderPath = Paths.get(folder);
            if (!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }

            String originalName = file.getOriginalFilename();
            String ext = getFileExtension(originalName);

            assert originalName != null;
            String safeName = originalName
                    .replaceAll("\\s+", "_")
                    .replaceAll("[^a-zA-Z0-9._-]", "");

            String newFileName =
                    Helper.currentTimeInstant() + "_" + safeName;

            Path filePath = folderPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath);

            UploadFileResponseDto dto = new UploadFileResponseDto(
                    originalName,
                    filePath.toString().charAt(0) == '/' ? (publicBaseUrl + filePath) : (publicBaseUrl + "/" + filePath),
                    file.getSize()
            );

            return ResponseDto.ok("Fayl muvaffaqiyatli yuklandi", dto);

        } catch (IOException e) {
            return ResponseDto.fail(
                    500,
                    ErrorCode.INTERNAL_ERROR,
                    "Faylni saqlashda xatolik: " + e.getMessage()
            );
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
