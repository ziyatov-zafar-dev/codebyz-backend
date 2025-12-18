package uz.codebyz.helper;

public class UploadFileResponseDto {
    private String fileName;
    private String fileUrl;
    private Long fileSize;

    public UploadFileResponseDto() {
    }

    public UploadFileResponseDto(String fileName, String fileUrl, Long fileSize) {
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
