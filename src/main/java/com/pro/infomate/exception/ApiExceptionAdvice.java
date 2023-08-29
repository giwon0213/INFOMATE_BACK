package com.pro.infomate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ApiExceptionAdvice {

    @ExceptionHandler(NotFindDataException.class)
    public ResponseEntity<Response> notFindDataException(NotFindDataException e){

        log.info("[ApiExceptionAdvice](notFindDataException) error : {} ", e);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new Response(HttpStatus.NO_CONTENT,e.getMessage(), e.getMessage()));
    }

    @ExceptionHandler(NotEnoughDateException.class)
    public ResponseEntity<Response> notEnoughDataException(NotEnoughDateException e){

        log.info("[ApiExceptionAdvice](notEnoughDataException) erroer : {} ", e);

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(new Response(HttpStatus.NOT_ACCEPTABLE,e.getMessage(), e.getMessage()));
    }

    @ExceptionHandler(NotAuthenticationServer.class)
    public ResponseEntity<Response> notAuthenticationServer(NotAuthenticationServer e){

        log.info("[ApiExceptionAdvice](notAuthenticationServer) error : {} ", e);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(new Response(HttpStatus.NO_CONTENT,e.getMessage(),e.getMessage()));
    }

    @ExceptionHandler(AlreadyRequstException.class)
    public ResponseEntity<Response> AlreadyRequestException(AlreadyRequstException e){

        log.info("[ApiExceptionAdvice](AlreadyRequestException) error : {} ", e);

        return ResponseEntity.status(HttpStatus.ALREADY_REPORTED)
                .body(new Response(HttpStatus.ALREADY_REPORTED, e.getMessage(),e.getMessage()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Response> InvalidRequestException(InvalidRequestException e){

        log.info("[ApiExceptionAdvice](AlreadyRequestException) error : {} ", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Response(HttpStatus.BAD_REQUEST, e.getMessage(),e.getMessage()));
    }

}
