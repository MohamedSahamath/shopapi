package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.SupplierRequest;
import lk.ijse.shopapi.dto.response.SupplierResponse;
import lk.ijse.shopapi.entity.Supplier;
import lk.ijse.shopapi.exception.ResourceNotFoundException;
import lk.ijse.shopapi.repository.SupplierRepository;
import lk.ijse.shopapi.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;

    @Override
    @Transactional
    public SupplierResponse create(SupplierRequest request) {
        Supplier supplier = Supplier.builder()
                .companyName(request.getCompanyName())
                .contactPerson(request.getContactPerson())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .active(true)
                .build();

        supplier = supplierRepository.save(supplier);
        log.info("Supplier created: {}", supplier.getCompanyName());
        return toResponse(supplier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupplierResponse> findAll() {
        return supplierRepository.findByActiveTrue().stream()
                .map(this::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse findById(Long id) {
        return toResponse(getSupplierOrThrow(id));
    }

    @Override
    @Transactional
    public SupplierResponse update(Long id, SupplierRequest request) {
        Supplier supplier = getSupplierOrThrow(id);

        supplier.setCompanyName(request.getCompanyName());
        supplier.setContactPerson(request.getContactPerson());
        supplier.setPhone(request.getPhone());
        supplier.setEmail(request.getEmail());
        supplier.setAddress(request.getAddress());

        return toResponse(supplierRepository.save(supplier));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Supplier supplier = getSupplierOrThrow(id);
        supplier.setActive(false);
        supplierRepository.save(supplier);
        log.info("Supplier deactivated: {}", supplier.getCompanyName());
    }

    private Supplier getSupplierOrThrow(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Supplier not found with id: " + id));
    }

    private SupplierResponse toResponse(Supplier s) {
        return SupplierResponse.builder()
                .id(s.getId())
                .companyName(s.getCompanyName())
                .contactPerson(s.getContactPerson())
                .phone(s.getPhone())
                .email(s.getEmail())
                .address(s.getAddress())
                .active(s.getActive())
                .build();
    }
}