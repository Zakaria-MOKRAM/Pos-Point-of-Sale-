package ma.jway.rms.controllers;

import lombok.RequiredArgsConstructor;
import ma.jway.rms.dto.requests.AuthRequest;
import ma.jway.rms.dto.requests.RegisterRequest;
import ma.jway.rms.dto.responses.AuthResponse;
import ma.jway.rms.dto.responses.AvailableUsersResponse;
import ma.jway.rms.services.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @GetMapping("/users")
    public ResponseEntity<List<AvailableUsersResponse>> users() {
        return ResponseEntity.ok(authService.availableUsers());
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> register(@RequestBody AuthRequest request) {
        return ResponseEntity.ok(authService.authenticate(request));
    }
}
