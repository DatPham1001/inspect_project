package com.imedia.config.application;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.imedia.model.BaseResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Log4j2
public class GlobalDefaultException extends ResponseEntityExceptionHandler {
    static final Gson gson = new Gson();

    @ExceptionHandler(RuntimeException.class)
    @RequestMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        log.info("=====GLOBAL EXCEPTION=====", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BaseResponse(500));
    }

    @ExceptionHandler(JsonParseException.class)
    @RequestMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> handleParseException(JsonParseException e) {
        log.info("=====GLOBAL EXCEPTION=====", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(666));
    }

    @ExceptionHandler(Exception.class)
    @RequestMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> handleParseException(Exception e) {
        log.info("=====GLOBAL EXCEPTION=====", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(666));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status,
                                                                  WebRequest request) {
        log.info("=====GLOBAL EXCEPTION=====" + request.toString(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse(666));
    }
}
