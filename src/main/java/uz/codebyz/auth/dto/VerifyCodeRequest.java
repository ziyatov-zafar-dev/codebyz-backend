package uz.codebyz.auth.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Sign-in verification request
 * identifier = username OR email
 */
public class VerifyCodeRequest {

    @NotBlank
    private String identifier;

    @NotBlank
    private String code;

    public VerifyCodeRequest() {}

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
