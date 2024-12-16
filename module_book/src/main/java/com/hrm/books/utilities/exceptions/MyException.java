package com.hrm.books.utilities.exceptions;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

import static com.hrm.books.utilities.constants.Constants.DATE_TIME_FORMATTER_SLASH;

@Configuration
public class MyException extends RuntimeException {
    ApiErrors<String> apiErrors;

    public MyException() {
    }

//    public MyException(String message) {
//        super(message);
//        apiErrors = new ApiErrors(100, message, DATE_TIME_FORMATTER.format(LocalDateTime.now()));
//    }

    private void initializeErrorApi(int errorCode, String message) {
        apiErrors = new ApiErrors<>(errorCode, message, DATE_TIME_FORMATTER_SLASH.format(LocalDateTime.now()));
    }

    public class ErrorNotFound extends MyException {
        public ErrorNotFound() {
            super();
        }

        public ErrorNotFound(String message) {
            initializeErrorApi(HttpStatus.NOT_FOUND.value(), message);
        }
    }

    public class ErrorMaxLimit extends MyException {
        public ErrorMaxLimit() {
            super();
        }

        public ErrorMaxLimit(String message) {
            initializeErrorApi(HttpStatus.CONFLICT.value(), message);
        }
    }

    public class myGlobalError extends MyException {
        public myGlobalError() {
        }
        public myGlobalError(String message) {
            initializeErrorApi(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
        }
    }

//    @RestController
    @RestControllerAdvice
    public class RestExceptionHandler {
        @ExceptionHandler(value = myGlobalError.class)
        public ResponseEntity<ApiErrors<String>> handleGlobal() {
            return new ResponseEntity<>(apiErrors, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(value = ErrorNotFound.class)
        public ResponseEntity<ApiErrors<String>> handleNotFound() {
            return new ResponseEntity<>(apiErrors, HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(value = ErrorMaxLimit.class)
        public ResponseEntity<ApiErrors<String>> handleMaxLimit() {
            return new ResponseEntity<>(apiErrors, HttpStatus.CONFLICT);
        }

//        @ExceptionHandler(value = MyException.class)
//        public ResponseEntity<ApiErrors> hadnle() {
//            return new ResponseEntity<>(apiErrors, HttpStatus.CONFLICT);
//        }
    }
}

/*
package com.hrm.books.utilities.exceptions;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class MyException extends RuntimeException {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public MyException() {
        super();
    }

    private MyException(String message) {
        super(message);
    }

    private MyException(int errorCode, String message) {
        super(message);
    }

    public class myNotFound extends MyException {
        private final ApiErrors apiErrors;

        public myNotFound() {
            super();
            this.apiErrors = new ApiErrors(404, "Not Found", dateTimeFormatter.format(LocalDateTime.now()));
        }

        public myNotFound(String message) {
            super(message);
            this.apiErrors = new ApiErrors(404, message, dateTimeFormatter.format(LocalDateTime.now()));
        }

        public ApiErrors getApiErrors() {
            return apiErrors;
        }
    }

    public class myMaxLimit extends MyException {
        private final ApiErrors apiErrors;

        public myMaxLimit() {
            super();
            this.apiErrors = new ApiErrors(409, "Max Limit Exceeded", dateTimeFormatter.format(LocalDateTime.now()));
        }

        public myMaxLimit(String message) {
            super(message);
            this.apiErrors = new ApiErrors(409, message, dateTimeFormatter.format(LocalDateTime.now()));
        }

        public ApiErrors getApiErrors() {
            return apiErrors;
        }
    }

    public class myGlobalError extends MyException {
        private final ApiErrors apiErrors;

        public myGlobalError() {
            super();
            this.apiErrors = new ApiErrors(500, "Internal Server Error", dateTimeFormatter.format(LocalDateTime.now()));
        }

        public myGlobalError(String message) {
            super(message);
            this.apiErrors = new ApiErrors(500, message, dateTimeFormatter.format(LocalDateTime.now()));
        }

        public ApiErrors getApiErrors() {
            return apiErrors;
        }
    }

    @RestController
    @RestControllerAdvice
    public class RestExceptionHandler {

        @ExceptionHandler(value = myGlobalError.class)
        public ResponseEntity<ApiErrors> handleGlobal(myGlobalError ex) {
            return new ResponseEntity<>(ex.getApiErrors(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        @ExceptionHandler(value = myNotFound.class)
        public ResponseEntity<ApiErrors> handleNotFound(myNotFound ex) {
            return new ResponseEntity<>(ex.getApiErrors(), HttpStatus.NOT_FOUND);
        }

        @ExceptionHandler(value = myMaxLimit.class)
        public ResponseEntity<ApiErrors> handleMaxLimit(myMaxLimit ex) {
            return new ResponseEntity<>(ex.getApiErrors(), HttpStatus.CONFLICT);
        }
    }
}
*/