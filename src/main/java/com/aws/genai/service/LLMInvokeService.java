package com.aws.genai.service;


import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface LLMInvokeService {

    void init();

    void store(MultipartFile file);

    String extractTextFromImage(MultipartFile file, String prompt) throws Exception;

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);

    void deleteAll();

}
