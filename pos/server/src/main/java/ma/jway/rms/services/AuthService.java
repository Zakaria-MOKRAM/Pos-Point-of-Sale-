package ma.jway.rms.services;

import lombok.RequiredArgsConstructor;
import ma.jway.rms.dto.models.User;
import ma.jway.rms.dto.requests.AuthRequest;
import ma.jway.rms.dto.requests.RegisterRequest;
import ma.jway.rms.dto.responses.AuthResponse;
import ma.jway.rms.dto.responses.AvailableUsersResponse;
import ma.jway.rms.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        userRepository.save(user);

        var token = jwtService.generateToken(user);

        return AuthResponse.builder().token(token).firstname(user.getFirstname()).lastname(user.getLastname())
                .username(user.getUsername()).role(user.getRole().name()).id(user.getId()).build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()));

        var user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        var token = jwtService.generateToken(user);

        return AuthResponse.builder().token(token).firstname(user.getFirstname()).lastname(user.getLastname())
                .username(user.getUsername()).role(user.getRole().name()).id(user.getId()).build();
    }

    public List<AvailableUsersResponse> availableUsers() {
        List<AvailableUsersResponse> availableUsers = new ArrayList<>();
        List<User> users = userRepository.findAll();
        for (User user : users) {
            availableUsers.add(new AvailableUsersResponse(
                    user.getFirstname(),
                    user.getLastname(),
                    user.getUsername(),
                    user.getRole()));
        }
        return availableUsers;
    }
}
