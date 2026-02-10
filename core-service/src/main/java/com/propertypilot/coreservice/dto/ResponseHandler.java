package com.propertypilot.coreservice.dto;

import com.propertypilot.coreservice.exceptionCustom.ErrorCode;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseHandler<T> {

    private int code;
    private String type;
    private String message;
    private T data;

    // -------------------------
    // SUCCESS
    // -------------------------
    public static <T> ResponseHandler<T> success(T data, String message) {
        return ResponseHandler.<T>builder()
                .code(0)
                .type("SUCCESS")
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResponseHandler<T> success(String message) {
        return success(null, message);
    }

    // -------------------------
    // ERROR (int + message)
    // -------------------------
    public static <T> ResponseHandler<T> error(int code, String message) {
        return ResponseHandler.<T>builder()
                .code(code)
                .type("ERROR")
                .message(message)
                .data(null)
                .build();
    }

    // -------------------------
    // ERROR (ErrorCode + message)
    // -------------------------
    public static <T> ResponseHandler<T> error(ErrorCode errorCode, String message) {
        return ResponseHandler.<T>builder()
                .code(errorCode.getCode())
                .type("ERROR")
                .message(message)
                .data(null)
                .build();
    }

    // -------------------------
    // ERROR (solo ErrorCode)
    // -------------------------
    public static <T> ResponseHandler<T> error(ErrorCode errorCode) {
        return ResponseHandler.<T>builder()
                .code(errorCode.getCode())
                .type("ERROR")
                .message(errorCode.getDefaultMessage())
                .data(null)
                .build();
    }
}