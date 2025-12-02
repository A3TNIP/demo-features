package com.aajumaharjan.demofeatures.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping(value = "/login", produces = "text/html")
    public ResponseEntity<String> login() {
        // Inline HTML to avoid view resolver issues when loaded via pluginloader
        String html = """
                <!doctype html>
                <html lang="en">
                <head><meta charset="UTF-8"><title>Login</title></head>
                <body>
                  <h2>Login</h2>
                  <form method="post" action="/login">
                    <label>Username: <input type="text" name="username" required></label><br>
                    <label>Password: <input type="password" name="password" required></label><br>
                    <button type="submit">Login</button>
                  </form>
                </body>
                </html>
                """;
        return ResponseEntity.ok(html);
    }
}
