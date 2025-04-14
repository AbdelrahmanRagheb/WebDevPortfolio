package com.taskasync.gateway_server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/user")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.ok("User Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/task")
    public ResponseEntity<String> taskFallback() {
        return ResponseEntity.ok("Task Service is currently unavailable. Please try again later.");
    }
}
