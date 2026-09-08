package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.SupplierRequest;
import lk.ijse.shopapi.dto.response.SupplierResponse;

import java.util.List;

public interface SupplierService {
    SupplierResponse create(SupplierRequest request);
    List<SupplierResponse> findAll();
    SupplierResponse findById(Long id);
    SupplierResponse update(Long id, SupplierRequest request);
    void delete(Long id);
}