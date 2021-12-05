package ru.sfedu.model;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Result<T>{
    private String status;
    private String message;
    private List<T> body;

    public Result(String status, String message, List<T> body) {
        this.status = status;
        this.message = message;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result<?> result = (Result<?>) o;
        return Objects.equals(status, result.status) && Objects.equals(message, result.message) && Objects.equals(body, result.body);
    }

    @Override
    public String toString() {
        return "Result{" +
                "Status='" + status + '\'' +
                ", Message='" + message + '\'' +
                ", body=" + Arrays.toString(body.toArray()) +
                '}';
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<T> getBody() {
        return body;
    }
}
