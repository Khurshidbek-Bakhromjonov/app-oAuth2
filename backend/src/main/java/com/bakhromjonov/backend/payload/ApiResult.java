package com.bakhromjonov.backend.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString
public class ApiResult<E> {

    private final boolean success;

    private String message;

    private E data;

    private List<ErrorData> errors;


    private ApiResult(E data) {
        this.success = true;
        this.data = data;
    }
    private ApiResult(String message) {
        this.success = true;
        this.message=message;
    }

    public ApiResult(boolean success) {
        this.success = success;
    }

    private ApiResult(List<ErrorData> errors) {
        this.success = false;
        this.errors = errors;
    }

    public ApiResult() {
        this.success = true;
    }

    public static <T> ApiResult<T> successResponse(T data) {
        return new ApiResult<>(data);
    }
    public static <T> ApiResult <T> successResponse(boolean success){
        return new ApiResult<>(true);
    }



    public static <T> ApiResult<T> successResponse() {
        return new ApiResult<>();
    }

    public static <T> ApiResult<T> successResponse(String message) {
        return new ApiResult<>(message);
    }

    public static ApiResult<ErrorData> failResponse(String msg, Integer errorCode) {
        return new ApiResult<>(List.of(new ErrorData(errorCode, msg)));
    }


}
