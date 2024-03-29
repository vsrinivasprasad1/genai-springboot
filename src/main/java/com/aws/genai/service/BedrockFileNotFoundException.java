package com.aws.genai.service;

public class BedrockFileNotFoundException extends BedrockException {

    public BedrockFileNotFoundException(String message) {
        super(message);
    }

    public BedrockFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}