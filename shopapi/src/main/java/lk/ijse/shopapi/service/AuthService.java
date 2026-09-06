package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.*;
import lk.ijse.shopapi.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}