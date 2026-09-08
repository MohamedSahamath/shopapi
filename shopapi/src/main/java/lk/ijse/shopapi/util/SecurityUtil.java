package lk.ijse.shopapi.util;

import lk.ijse.shopapi.entity.Customer;
import lk.ijse.shopapi.exception.ResourceNotFoundException;
import lk.ijse.shopapi.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtil {

    private final CustomerRepository customerRepository;

    public String getCurrentEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    public Customer getCurrentCustomer() {
        String email = getCurrentEmail();
        return customerRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer profile not found for: " + email));
    }
}