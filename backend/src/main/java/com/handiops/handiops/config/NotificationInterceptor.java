package com.handiops.handiops.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor لإضافة الكوكيز تلقائياً للإشعارات
 * Interceptor to automatically add notification cookies
 */
@Component
public class NotificationInterceptor implements HandlerInterceptor {

    private static final String SUCCESS_MESSAGE = "success_message";
    private static final String ERROR_MESSAGE = "error_message";
    private static final String WARNING_MESSAGE = "warning_message";
    private static final String INFO_MESSAGE = "info_message";
    private static final int COOKIE_MAX_AGE = 15; // 15 seconds

    @Override
    public void afterCompletion(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            Exception ex) {
        
        // إضافة كوكي الخطأ إذا حدث استثناء
        if (ex != null) {
            addNotificationCookie(response, ERROR_MESSAGE, ex.getMessage());
        }
    }

    /**
     * إضافة كوكي إشعار
     * Add notification cookie
     */
    public static void addNotificationCookie(
            HttpServletResponse response,
            String type,
            String message) {
        
        Cookie cookie = new Cookie(type, message);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(false); // يجب أن يكون false ليكون قابل للقراءة من JavaScript
        response.addCookie(cookie);
    }

    /**
     * إضافة كوكي نجاح
     */
    public static void addSuccessCookie(HttpServletResponse response, String message) {
        addNotificationCookie(response, SUCCESS_MESSAGE, message);
    }

    /**
     * إضافة كوكي خطأ
     */
    public static void addErrorCookie(HttpServletResponse response, String message) {
        addNotificationCookie(response, ERROR_MESSAGE, message);
    }

    /**
     * إضافة كوكي تحذير
     */
    public static void addWarningCookie(HttpServletResponse response, String message) {
        addNotificationCookie(response, WARNING_MESSAGE, message);
    }

    /**
     * إضافة كوكي معلومات
     */
    public static void addInfoCookie(HttpServletResponse response, String message) {
        addNotificationCookie(response, INFO_MESSAGE, message);
    }
}
