package org.odc.gestionstockapp.Web.Controllers.Implementation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.odc.gestionstockapp.Datas.Entities.UserEntity;
import org.odc.gestionstockapp.Services.Implementation.UserService;
import org.odc.gestionstockapp.Datas.Repositories.UserRepository;
import org.odc.gestionstockapp.Services.Implementation.JwtService;
import org.odc.gestionstockapp.Web.Dtos.AuthenticationRequestDto;
import org.odc.gestionstockapp.Web.Dtos.AuthenticationResponseDto;
import org.odc.gestionstockapp.Web.Dtos.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Tag(name = "Users", description = "API pour l'authentification")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtService jwtService,
            BCryptPasswordEncoder passwordEncoder,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
    }
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponseDto> register(@RequestBody UserDto request) {
        // Utiliser UserService pour créer l'utilisateur
        UserEntity user = userService.create(request);
        // Générer le token
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthenticationResponseDto(token));
    }

    @PostMapping
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody AuthenticationRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        UserEntity user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok(new AuthenticationResponseDto(token));
    }
}