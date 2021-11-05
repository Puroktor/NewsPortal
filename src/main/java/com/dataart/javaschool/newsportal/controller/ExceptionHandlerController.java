package com.dataart.javaschool.newsportal.controller;

import com.dataart.javaschool.newsportal.controller.dto.ErrorDto;
import com.dataart.javaschool.newsportal.exception.WrongFileFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDto handleRuntimeException(RuntimeException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(WrongFileFormatException.class)
    @ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
    @ResponseBody
    public ErrorDto handleWrongFileFormatException(WrongFileFormatException e) {
        return new ErrorDto(e.getMessage());
    }

}
