package com.aws.genai;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("llm")
public class LLMProperties {

    /**
     * Folder location for storing files
     */
    private String location = "upload-dir";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
