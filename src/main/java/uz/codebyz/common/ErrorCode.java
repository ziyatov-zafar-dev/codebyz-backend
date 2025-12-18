package uz.codebyz.common;

/**
 * API Error Codes
 */
public enum ErrorCode {

    /**
     * Request validatsiyadan o'tmadi
     */
    VALIDATION_ERROR,

    /**
     * User topilmadi
     */
    USER_NOT_FOUND,

    /**
     * Login yoki parol xato
     */
    BAD_CREDENTIALS,

    /**
     * Username band
     */
    USERNAME_TAKEN,

    /**
     * Email band
     */
    EMAIL_TAKEN,

    /**
     * Email verifikatsiya qilinmagan
     */
    EMAIL_NOT_VERIFIED,

    /**
     * Email verifikatsiya kodi eskirib ketgan
     */
    CODE_EXPIRED,

    /**
     * Verifikatsiya kodi noto'g'ri
     */
    CODE_INVALID,

    /**
     * Verifikatsiya kodi oldin ishlatilgan
     */
    CODE_ALREADY_USED,

    /**
     * Login urinishlari sabab bloklangan
     */
    USER_LOCKED,

    /**
     * Admin hali tasdiqlamagan (CHECKING)
     */
    ACCOUNT_CHECKING,

    /**
     * Admin rad etgan (CANCEL)
     */
    ACCOUNT_CANCELLED,

    /**
     * Sign-up orqali ruxsat etilmagan role
     */
    ROLE_NOT_ALLOWED,

    /**
     * OAuth2 jarayonida xatolik
     */
    OAUTH2_FAILED,

    /**
     * Google email bazada yo'q (sign-up yo'q, faqat sign-in)
     */
    GOOGLE_ACCOUNT_NOT_REGISTERED,

    /**
     * Ruxsat yo'q
     */
    NOT_AUTHORIZED,

    /**
     * Server ichki xato
     */
    INTERNAL_ERROR,
    INVALID_TOKEN

}
