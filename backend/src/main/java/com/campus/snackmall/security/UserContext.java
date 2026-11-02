package com.campus.snackmall.security;

public class UserContext {

    private static final ThreadLocal<JwtUserInfo> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(JwtUserInfo userInfo) {
        HOLDER.set(userInfo);
    }

    public static JwtUserInfo get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        JwtUserInfo userInfo = get();
        return userInfo == null ? null : userInfo.getUserId();
    }

    public static String getRole() {
        JwtUserInfo userInfo = get();
        return userInfo == null ? null : userInfo.getRole();
    }

    public static void clear() {
        HOLDER.remove();
    }
}
