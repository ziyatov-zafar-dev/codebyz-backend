package uz.codebyz.common;

public enum ErrorCode {

    VALIDATION_ERROR(
            "So‘rov ma’lumotlari noto‘g‘ri",
            "Request validation failed",
            "İstek doğrulaması başarısız"
    ),

    USER_NOT_FOUND(
            "Foydalanuvchi topilmadi",
            "User not found",
            "Kullanıcı bulunamadı"
    ),

    BAD_CREDENTIALS(
            "Login yoki parol noto‘g‘ri",
            "Invalid username or password",
            "Kullanıcı adı veya şifre hatalı"
    ),

    USERNAME_TAKEN(
            "Username band",
            "Username already taken",
            "Kullanıcı adı zaten alınmış"
    ),

    EMAIL_TAKEN(
            "Email band",
            "Email already taken",
            "Email zaten kullanımda"
    ),

    EMAIL_NOT_VERIFIED(
            "Email tasdiqlanmagan",
            "Email not verified",
            "Email doğrulanmamış"
    ),

    CODE_EXPIRED(
            "Tasdiqlash kodi eskirgan",
            "Verification code expired",
            "Doğrulama kodu süresi dolmuş"
    ),

    CODE_INVALID(
            "Tasdiqlash kodi noto‘g‘ri",
            "Invalid verification code",
            "Doğrulama kodu geçersiz"
    ),

    CODE_ALREADY_USED(
            "Tasdiqlash kodi allaqachon ishlatilgan",
            "Verification code already used",
            "Doğrulama kodu daha önce kullanılmış"
    ),

    USER_LOCKED(
            "Foydalanuvchi vaqtincha bloklangan",
            "User is temporarily locked",
            "Kullanıcı geçici olarak kilitlendi"
    ),

    ACCOUNT_CHECKING(
            "Hisob admin tomonidan tekshirilmoqda",
            "Account is under admin review",
            "Hesap yönetici tarafından inceleniyor"
    ),

    ACCOUNT_CANCELLED(
            "Hisob admin tomonidan bekor qilingan",
            "Account has been rejected",
            "Hesap yönetici tarafından reddedildi"
    ),

    ROLE_NOT_ALLOWED(
            "Ushbu rol bilan ro‘yxatdan o‘tish mumkin emas",
            "Role is not allowed",
            "Bu rol ile kayıt olunamaz"
    ),

    OAUTH2_FAILED(
            "OAuth2 jarayonida xatolik",
            "OAuth2 authentication failed",
            "OAuth2 kimlik doğrulaması başarısız"
    ),

    GOOGLE_ACCOUNT_NOT_REGISTERED(
            "Google akkaunt tizimda mavjud emas",
            "Google account is not registered",
            "Google hesabı sistemde kayıtlı değil"
    ),

    NOT_AUTHORIZED(
            "Ruxsat yo‘q",
            "Not authorized",
            "Yetkisiz erişim"
    ),

    INVALID_TOKEN(
            "Token yaroqsiz yoki eskirgan",
            "Invalid or expired token",
            "Geçersiz veya süresi dolmuş token"
    ),

    INTERNAL_ERROR(
            "Server ichki xatosi",
            "Internal server error",
            "Sunucu iç hatası"
    ),
    USER_ALREADY_BLOCKED(
            "Foydalanuvchi allaqachon bloklangan",
            "User is already blocked",
            "Kullanıcı zaten engellenmiş"
    ),
    USER_NOT_BLOCKED(
            "Foydalanuvchi bloklanmagan",
            "User is not blocked",
            "Kullanıcı engellenmemiş"
    ), CHAT_NOT_FOUND("Chat topilmadi",
            "Chat not found",
            "Sohbet bulunamadı"),
    CHAT_ALREADY_EXISTS(
            "Chat allaqachon mavjud",
            "Chat already exists",
            "Sohbet zaten mevcut"
    ),

    INVALID_REQUEST(
            "Noto‘g‘ri so‘rov",
            "Invalid request",
            "Geçersiz istek"
    );
    private final String uz;
    private final String en;
    private final String tr;

    ErrorCode(String uz, String en, String tr) {
        this.uz = uz;
        this.en = en;
        this.tr = tr;
    }

    public String getUz() {
        return uz;
    }

    public String getEn() {
        return en;
    }

    public String getTr() {
        return tr;
    }
}
