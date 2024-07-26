package com.aws.genai.web;

import com.aws.genai.service.BedrockFileNotFoundException;
import com.aws.genai.service.LLMInvokeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.stream.Collectors;

@Controller
public class FileUploadController {

    private final com.aws.genai.service.LLMInvokeService LLMInvokeService;
    private static final Logger logger = LoggerFactory.getLogger(FileUploadController.class);

    @Autowired
    public FileUploadController(LLMInvokeService LLMInvokeService) {
        this.LLMInvokeService = LLMInvokeService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {
        logger.info("Listing uploaded files");

        model.addAttribute("files", LLMInvokeService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        logger.info("Serving file: {}", filename);

        Resource file = LLMInvokeService.loadAsResource(filename);

        if (file == null) {
            logger.warn("File not found: {}", filename);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        logger.info("Handling file upload: {}", file.getOriginalFilename());

        LLMInvokeService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PostMapping(value = "/parse")
    public String extractTextFromImage(@RequestPart("file") MultipartFile file, @RequestParam("prompt") String prompt,
                                       RedirectAttributes redirectAttributes) {
        logger.info("Extracting text from image with prompt: {}", prompt);

        String response = "";
        try {
            if (file == null || prompt.isEmpty()) {
                logger.warn("Invalid input values");
                redirectAttributes.addFlashAttribute("message", "Invalid Input Values");
                return "redirect:/";
            }
            response = LLMInvokeService.extractTextFromImage(file, prompt);
        } catch (Exception e) {
            logger.error("Error extracting text from image", e);
        }
        redirectAttributes.addFlashAttribute("message", response);

        return "redirect:/";
    }

    @ExceptionHandler(BedrockFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(BedrockFileNotFoundException exc) {
        logger.error("File not found exception", exc);
        return ResponseEntity.notFound().build();
    }
}
