package com.dataart.javaschool.newsportal.controller;

import com.dataart.javaschool.newsportal.controller.dto.ErrorDto;
import com.dataart.javaschool.newsportal.exception.EmptyPageException;
import com.dataart.javaschool.newsportal.exception.TooBigFileException;
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


    @ExceptionHandler(TooBigFileException.class)
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    @ResponseBody
    public ErrorDto handleWrongFileFormatException(TooBigFileException e) {
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler(EmptyPageException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDto handleWrongFileFormatException(EmptyPageException e) {
        return new ErrorDto(e.getMessage());
    }

}
