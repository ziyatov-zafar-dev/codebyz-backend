package uz.codebyz.storage;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.nio.file.Path;
@RestController
@RequestMapping("/files")
public class FilesController {

    private final StorageProperties props;

    public FilesController(StorageProperties props) {
        this.props = props;
    }

    @GetMapping("/{subdir}/{filename:.+}")
    public ResponseEntity<Resource> serve(
            @PathVariable String subdir,
            @PathVariable String filename
    ) throws MalformedURLException {
        Path file = Path.of(props.getUploadsDir())
                .toAbsolutePath()
                .normalize()
                .resolve(subdir)
                .resolve(filename)
                .normalize();

        Resource resource = new UrlResource(file.toUri());

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (filename.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (filename.endsWith(".webp")) {
            mediaType = MediaType.valueOf("image/webp");
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline")
                .body(resource);
    }
}
