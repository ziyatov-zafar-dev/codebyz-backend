package uz.codebyz.message.entity.enums;

/**
 * Chat holatini bildiruvchi enum.
 *
 * <p>Ushbu enum private (1-to-1) chatlar uchun ishlatiladi va
 * chatning hozirgi faol yoki o‘chirilgan holatini aniqlaydi.</p>
 */
public enum ChatStatus {

    /**
     * Chat faol holatda.
     * Foydalanuvchilar xabar yuborishi va qabul qilishi mumkin.
     */
    ACTIVE("Chat faol holatda, xabar almashish mumkin"),

    /**
     * Chat o‘chirilgan holatda (soft delete).
     * Chat foydalanuvchi uchun ko‘rinmaydi,
     * lekin ma’lumotlar bazada saqlanishi mumkin.
     */
    DELETE("Chat o‘chirilgan, foydalanuvchi uchun mavjud emas");

    private final String description;

    ChatStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
