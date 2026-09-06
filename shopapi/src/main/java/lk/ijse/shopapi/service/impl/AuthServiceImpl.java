package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.AuthResponse;
import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.exception.DuplicateResourceException;
import lk.ijse.shopapi.repository.*;
import lk.ijse.shopapi.security.JwtUtil;
import lk.ijse.shopapi.service.AuthService;
import lk.ijse.shopapi.util.enums.RoleName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + request.getEmail());
        }
        if (customerRepository.existsByNic(request.getNic())) {
            throw new DuplicateResourceException(
                    "NIC already registered: " + request.getNic());
        }

        Role customerRole = roleRepository.findByName(RoleName.ROLE_CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("ROLE_CUSTOMER missing"));

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .roles(Set.of(customerRole))
                .build();
        user = userRepository.save(user);

        Customer customer = Customer.builder()
                .user(user)
                .fullName(request.getFullName())
                .nic(request.getNic())
                .phone(request.getPhone())
                .build();
        customer = customerRepository.save(customer);

        cartRepository.save(Cart.builder().customer(customer).build());

        log.info("New customer registered: {}", user.getEmail());

        return buildAuthResponse(user, customer.getFullName());
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        String fullName = customerRepository.findByUserEmail(user.getEmail())
                .map(Customer::getFullName)
                .orElse(user.getEmail());

        log.info("User logged in: {}", user.getEmail());

        return buildAuthResponse(user, fullName);
    }

    private AuthResponse buildAuthResponse(User user, String fullName) {
        return AuthResponse.builder()
                .token(jwtUtil.generateToken(user.getEmail()))
                .tokenType("Bearer")
                .email(user.getEmail())
                .fullName(fullName)
                .roles(user.getRoles().stream()
                        .map(r -> r.getName().name())
                        .collect(Collectors.toSet()))
                .build();
    }
}