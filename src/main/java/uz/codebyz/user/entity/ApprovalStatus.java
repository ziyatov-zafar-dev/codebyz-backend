package uz.codebyz.user.entity;

/**
 * Account approval status (admin tomonidan tasdiqlash):
 * - CHECKING: admin tekshiryapti (hali tasdiqlanmagan)
 * - CONFIRMED: admin tasdiqlagan, login mumkin
 * - CANCEL: admin rad etgan, sababi emailga yuboriladi
 */
public enum ApprovalStatus {
    CONFIRMED,
    CHECKING,
    CANCEL
}
