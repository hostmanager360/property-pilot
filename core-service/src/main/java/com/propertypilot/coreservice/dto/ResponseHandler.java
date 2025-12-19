package com.propertypilot.coreservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResponseHandler<T> {
    private int code;
    private String type;
    private String message;
    private T data;

    public static <T> ResponseHandler<T> success(T data, String message) {
        ResponseHandler<T> response = new ResponseHandler<>();
        response.code = 0;
        response.type = "SUCCESS";
        response.message = message;
        response.data = data;
        return response;
    }

    public static <T> ResponseHandler<T> error(int code, String message) {
        ResponseHandler<T> response = new ResponseHandler<>();
        response.code = code;
        response.type = "ERROR";
        response.message = message;
        response.data = null;
        return response;
    }
}
