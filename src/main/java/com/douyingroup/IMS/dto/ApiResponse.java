package com.douyingroup.IMS.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    // Static factory methods for creating responses
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(0, "Success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<T>(0, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<T>(code, message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<T>(1, message, null);
    }
}