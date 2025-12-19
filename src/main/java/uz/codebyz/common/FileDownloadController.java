package uz.codebyz.common;
//package uz.codebyz.storage;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.net.URL;

@RestController
@RequestMapping("/api/files")
public class FileDownloadController {

    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadByUrl(
            @RequestParam("url") String fileUrl
    ) {
        try {
            URL url = new URL(fileUrl);
            InputStream inputStream = url.openStream();

            String fileName = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + fileName + "\""
                    )
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(inputStream));

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
