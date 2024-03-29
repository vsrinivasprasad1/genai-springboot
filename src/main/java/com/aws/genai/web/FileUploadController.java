package com.aws.genai.web;

import java.io.IOException;
import java.util.stream.Collectors;

import com.aws.genai.service.BedrockFileNotFoundException;
import com.aws.genai.service.LLMInvokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class FileUploadController {

    private final com.aws.genai.service.LLMInvokeService LLMInvokeService;

    @Autowired
    public FileUploadController(LLMInvokeService LLMInvokeService) {
        this.LLMInvokeService = LLMInvokeService;
    }

    @GetMapping("/")
    public String listUploadedFiles(Model model) throws IOException {

        model.addAttribute("files", LLMInvokeService.loadAll().map(
                        path -> MvcUriComponentsBuilder.fromMethodName(FileUploadController.class,
                                "serveFile", path.getFileName().toString()).build().toUri().toString())
                .collect(Collectors.toList()));

        return "uploadForm";
    }

    @GetMapping("/files/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {

        Resource file = LLMInvokeService.loadAsResource(filename);

        if (file == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

    @PostMapping("/")
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {

        LLMInvokeService.store(file);
        redirectAttributes.addFlashAttribute("message",
                "You successfully uploaded " + file.getOriginalFilename() + "!");

        return "redirect:/";
    }

    @PostMapping("/parse")
    public String extractTextFromImage(@RequestParam("file") MultipartFile file, @RequestParam("prompt") String prompt,
                                       RedirectAttributes redirectAttributes) {
        String response = "";
        try {
            if(file == null || prompt.isEmpty()) {
                redirectAttributes.addFlashAttribute("message",
                        "Invalid Input Values");
                return "redirect:/";
            }
            response = LLMInvokeService.extractTextFromImage(file, prompt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        redirectAttributes.addFlashAttribute("message",
                response);

        return "redirect:/";
    }

    @ExceptionHandler(BedrockFileNotFoundException.class)
    public ResponseEntity<?> handleStorageFileNotFound(BedrockFileNotFoundException exc) {
        return ResponseEntity.notFound().build();
    }

}
