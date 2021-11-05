package com.dataart.javaschool.newsportal.exception;

public class WrongFileFormatException extends IllegalArgumentException {
    public WrongFileFormatException(String s) {
        super(s);
    }
}
