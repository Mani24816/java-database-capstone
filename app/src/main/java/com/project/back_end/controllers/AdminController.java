
package com.project.back_end.controllers;

@RestController  // 1. Marks this class as a REST controller to handle HTTP requests and return JSON
@RequestMapping("${api.path}admin")  

public class AdminController {

private final AdminService adminService;

    // 2. Constructor injection of AdminService for clean separation of concerns
    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 3. Endpoint for admin login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        // Delegates to the service layer for validation logic
        return adminService.validateAdmin(admin);
    }
}

