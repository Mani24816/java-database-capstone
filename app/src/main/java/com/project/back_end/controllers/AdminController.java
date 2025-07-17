package com.project.back_end.controller;

import com.project.back_end.model.Admin;
import com.project.back_end.service.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("${api.path}" + "admin")  // Example: if api.path=/api/, then path = /api/admin
public class AdminController {

    @Autowired
    private Service service;

    /**
     * Handles admin login by validating credentials.
     * @param admin Admin object containing username and password
     * @return ResponseEntity with JWT token or error message
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        return service.validateAdmin(admin);
    }
}
