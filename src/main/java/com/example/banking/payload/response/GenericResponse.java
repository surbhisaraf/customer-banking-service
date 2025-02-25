package com.example.banking.payload.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class GenericResponse<T> {
    private String message;

    private T data;

    public GenericResponse(String message) {
        this(message, null); // Call the second constructor with `null`
    }

    public GenericResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
