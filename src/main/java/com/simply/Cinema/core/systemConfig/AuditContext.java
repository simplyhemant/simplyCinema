//package com.simply.Cinema.core.systemConfig;
//
//public class AuditContext {
//    private static final ThreadLocal<Long> currentUserId = new ThreadLocal<>();
//
//    public static void setUserId(Long userId) {
//        currentUserId.set(userId);
//    }
//
//    public static Long getUserId() {
//        return currentUserId.get();
//    }
//
//    public static void clear() {
//        currentUserId.remove();
//    }
//}
