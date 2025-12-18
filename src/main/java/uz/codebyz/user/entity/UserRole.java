package uz.codebyz.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Platform roles:
 * - STUDENT: talaba
 * - TEACHER: o'qituvchi
 * - ADMIN: admin panel / tasdiqlovchi
 */
public enum UserRole {
    STUDENT,
    TEACHER,
    @JsonIgnore
    ADMIN
}
