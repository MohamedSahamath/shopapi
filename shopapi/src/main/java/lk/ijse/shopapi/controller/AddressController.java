package lk.ijse.shopapi.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lk.ijse.shopapi.dto.request.AddressRequest;
import lk.ijse.shopapi.dto.response.*;
import lk.ijse.shopapi.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Addresses", description = "Customer delivery addresses")
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<AddressResponse>>> findMyAddresses() {
        return ResponseEntity.ok(ApiResponse.success(
                "Addresses retrieved", addressService.findMyAddresses()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<AddressResponse>> create(
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Address added",
                        addressService.create(request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AddressResponse>> update(
            @PathVariable Long id, @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                "Address updated", addressService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        addressService.delete(id);
        return ResponseEntity.noContent().build();
    }
}