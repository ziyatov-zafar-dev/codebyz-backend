package uz.codebyz.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseDto<T> {

    private boolean success;
    private String message;
    private int code;

    private ErrorCode errorCode;

    @JsonProperty(value = "error_uz")
    private String errorDescriptionUz;
    @JsonProperty(value = "error_en")
    private String errorDescriptionEn;
    @JsonProperty(value = "error_tr")
    private String errorDescriptionTr;

    private T data;

    public ResponseDto() {
    }

    public ResponseDto(boolean success, String message, int code, ErrorCode errorCode, T data) {
        this.success = success;
        this.message = message;
        this.code = code;
        this.errorCode = errorCode;
        this.data = data;

        if (errorCode != null) {
            this.errorDescriptionUz = errorCode.getUz();
            this.errorDescriptionEn = errorCode.getEn();
            this.errorDescriptionTr = errorCode.getTr();
        }
    }

    /* ================= SUCCESS ================= */

    public static <T> ResponseDto<T> ok(String message, T data) {
        return new ResponseDto<>(true, message, 200, null, data);
    }

    public static <T> ResponseDto<T> ok(String message) {
        return new ResponseDto<>(true, message, 200, null, null);
    }

    /* ================= FAIL ================= */

    public static <T> ResponseDto<T> fail(int code, ErrorCode errorCode, String message) {
        return new ResponseDto<>(false, message, code, errorCode, null);
    }

    /* ================= GETTERS / SETTERS ================= */

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescriptionUz() {
        return errorDescriptionUz;
    }

    public String getErrorDescriptionEn() {
        return errorDescriptionEn;
    }

    public String getErrorDescriptionTr() {
        return errorDescriptionTr;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
