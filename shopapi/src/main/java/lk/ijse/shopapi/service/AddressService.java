package lk.ijse.shopapi.service;

import lk.ijse.shopapi.dto.request.AddressRequest;
import lk.ijse.shopapi.dto.response.AddressResponse;

import java.util.List;

public interface AddressService {
    AddressResponse create(AddressRequest request);
    List<AddressResponse> findMyAddresses();
    AddressResponse update(Long id, AddressRequest request);
    void delete(Long id);
}