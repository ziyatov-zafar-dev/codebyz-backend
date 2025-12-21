package uz.codebyz.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Platform roles:
 * - STUDENT: talaba
 * - TEACHER: o'qituvchi
 * - ADMIN: admin panel / tasdiqlovchi
 */
public enum UserRole {
    STUDENT("STUDENT"),
    TEACHER("TEACHER"),
    ADMIN("ADMIN");
    private String name;
    UserRole(String name) { this.name = name; }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
