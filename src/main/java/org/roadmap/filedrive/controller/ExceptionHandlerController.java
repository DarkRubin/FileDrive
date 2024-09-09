package org.roadmap.filedrive.controller;

import org.roadmap.filedrive.exception.AccessDeniedException;
import org.roadmap.filedrive.exception.MinioUnknownException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(MinioUnknownException.class)
    @RequestMapping
    public String handleMinioUnknownException() {
        return "500";
    }

    @ExceptionHandler(IOException.class)
    @RequestMapping
    public String handleIOException() {
        return "500";
    }

    @ExceptionHandler(AccessDeniedException.class)
    @RequestMapping
    public String handleAccessDeniedException() {
        return "403";
    }

    @ExceptionHandler(SecurityException.class)
    @RequestMapping
    public String handleSecurityException() {
        return "main-not-auth";
    }
}
