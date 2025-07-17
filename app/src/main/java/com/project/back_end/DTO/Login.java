package com.project.back_end.DTO;

public class Login {
    
// 1. 'email' field:
    //    - Represents the email address used for logging into the system.
    //    - Expected to contain a valid email address for authentication.
    private String email;

    // 2. 'password' field:
    //    - Represents the password associated with the email.
    //    - Used to verify the user's identity during login.
    //    - Typically hashed and verified securely.
    private String password;

    // 3. Constructor:
    //    - Uses the default constructor provided by Java.
    //    - Can be initialized with setters or via reflection during deserialization.

    // 4. Getters and Setters:

    // Gets the email value
    public String getEmail() {
        return email;
    }

    // Sets the email value
    public void setEmail(String email) {
        this.email = email;
    }

    // Gets the password value
    public String getPassword() {
        return password;
    }

    // Sets the password value
    public void setPassword(String password) {
        this.password = password;
    }
}
