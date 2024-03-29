package com.aws.genai.service;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.stream.Stream;

import com.aws.genai.LLMProperties;
import com.aws.genai.service.bedrock.InvokeModelWithResponseStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;


@Service
public class BedrockInvokeService implements LLMInvokeService {

    private final Path rootLocation;

    @Autowired
    public BedrockInvokeService(LLMProperties properties) {

        if (properties.getLocation().trim().length() == 0) {
            throw new BedrockException("File upload location can not be Empty.");
        }

        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public void store(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new BedrockException("Failed to store empty file.");
            }
            Path destinationFile = this.rootLocation.resolve(
                            Paths.get(file.getOriginalFilename()))
                    .normalize().toAbsolutePath();
            if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
                // This is a security check
                throw new BedrockException(
                        "Cannot store file outside current directory.");
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destinationFile,
                        StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new BedrockException("Failed to store file.", e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootLocation, 1)
                    .filter(path -> !path.equals(this.rootLocation))
                    .map(this.rootLocation::relativize);
        } catch (IOException e) {
            throw new BedrockException("Failed to read stored files", e);
        }

    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new BedrockFileNotFoundException(
                        "Could not read file: " + filename);

            }
        } catch (MalformedURLException e) {
            throw new BedrockFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new BedrockException("Could not initialize storage", e);
        }
    }

    @Override
    public String extractTextFromImage(MultipartFile file, String prompt) throws Exception {
        boolean t = false;
        return InvokeModelWithResponseStream.invokeClaude3Sonnet(encodeImage(file.getInputStream()), prompt, t);

    }

    public String encodeImage(InputStream inputStream) {
        byte[] imageBytes = new byte[8];
        try (var imageResponse = inputStream) {
            imageBytes = org.apache.commons.io.IOUtils.toByteArray(imageResponse); // Use Apache Commons IO for efficient handling
        } catch (IOException ioException) {
            System.err.println("Error: " + ioException.getMessage());
        }
        // Base64 encode the image bytes
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
