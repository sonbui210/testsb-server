package com.example.testsb2.api.auth;


import com.example.testsb2.api.auth.payload.LoginRequest;
import com.example.testsb2.api.auth.payload.RegisterRequest;
import com.example.testsb2.app.entity.Response;
import com.example.testsb2.app.security.CurrentUser;
import com.example.testsb2.app.security.JwtAuthenticationResponse;
import com.example.testsb2.app.security.JwtTokenProvider;
import com.example.testsb2.constant.Message;
import com.example.testsb2.data.model.CustomUserDetails;
import com.example.testsb2.data.model.Role;
import com.example.testsb2.data.model.User;
import com.example.testsb2.data.repository.RoleRepository;
import com.example.testsb2.helper.UserHelper;
import com.example.testsb2.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserHelper userHelper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserService userService;


    @GetMapping("/me")
    public ResponseEntity authenticateUser(@CurrentUser CustomUserDetails userDetails) {
        if (userDetails != null) {
            return Response.data(userDetails.getUser());
        } else {
            return Response.error(HttpStatus.UNAUTHORIZED, Message.UNAUTHORIZED);
        }

    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest request) {
        try {
            // X??c th???c t??? username v?? password.
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            // N???u kh??ng x???y ra exception t???c l?? th??ng tin h???p l???
            // Set th??ng tin authentication v??o Security Context
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Tr??? v??? jwt cho ng?????i d??ng.
            String jwt = tokenProvider.generateToken(authentication);
            return Response.data(new JwtAuthenticationResponse(jwt, authentication.getPrincipal()));
        } catch (BadCredentialsException e) {
            return Response.error(HttpStatus.BAD_REQUEST, "T??n ????ng nh???p ho???c t??i kho???n kh??ng ch??nh x??c.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequest request) {
        String error = userHelper.checkRegisterUserRequest(request);

        if (StringUtils.isEmpty(error)) {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setEmail(request.getEmail());
            user.setPhone(request.getPhone());

            Role role = roleRepository.findByCode("USER");
            user.setRoles(Collections.singleton(role));

            user = userService.save(user);

            return Response.data(user);
        } else {
            return Response.error(HttpStatus.BAD_REQUEST, error);
        }
    }

}
