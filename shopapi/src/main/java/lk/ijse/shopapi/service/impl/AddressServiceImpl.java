package lk.ijse.shopapi.service.impl;

import lk.ijse.shopapi.dto.request.AddressRequest;
import lk.ijse.shopapi.dto.response.AddressResponse;
import lk.ijse.shopapi.entity.*;
import lk.ijse.shopapi.exception.*;
import lk.ijse.shopapi.repository.CustomerAddressRepository;
import lk.ijse.shopapi.service.AddressService;
import lk.ijse.shopapi.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final CustomerAddressRepository addressRepository;
    private final SecurityUtil securityUtil;

    @Override
    @Transactional
    public AddressResponse create(AddressRequest request) {
        Customer customer = securityUtil.getCurrentCustomer();

        boolean makeDefault = Boolean.TRUE.equals(request.getIsDefault())
                || addressRepository.findByCustomerId(customer.getId()).isEmpty();

        if (makeDefault) {
            clearExistingDefault(customer.getId());
        }

        CustomerAddress address = CustomerAddress.builder()
                .customer(customer)
                .line1(request.getLine1())
                .line2(request.getLine2())
                .city(request.getCity())
                .postalCode(request.getPostalCode())
                .isDefault(makeDefault)
                .build();

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> findMyAddresses() {
        Customer customer = securityUtil.getCurrentCustomer();
        return addressRepository.findByCustomerId(customer.getId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public AddressResponse update(Long id, AddressRequest request) {
        CustomerAddress address = getOwnedAddressOrThrow(id);

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            clearExistingDefault(address.getCustomer().getId());
            address.setIsDefault(true);
        }

        address.setLine1(request.getLine1());
        address.setLine2(request.getLine2());
        address.setCity(request.getCity());
        address.setPostalCode(request.getPostalCode());

        return toResponse(addressRepository.save(address));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        addressRepository.delete(getOwnedAddressOrThrow(id));
    }

    private CustomerAddress getOwnedAddressOrThrow(Long id) {
        Customer customer = securityUtil.getCurrentCustomer();
        CustomerAddress address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + id));

        if (!address.getCustomer().getId().equals(customer.getId())) {
            throw new BadRequestException("This address does not belong to you");
        }
        return address;
    }

    private void clearExistingDefault(Long customerId) {
        addressRepository.findByCustomerId(customerId).forEach(a -> {
            if (Boolean.TRUE.equals(a.getIsDefault())) {
                a.setIsDefault(false);
                addressRepository.save(a);
            }
        });
    }

    private AddressResponse toResponse(CustomerAddress a) {
        return AddressResponse.builder()
                .id(a.getId())
                .line1(a.getLine1())
                .line2(a.getLine2())
                .city(a.getCity())
                .postalCode(a.getPostalCode())
                .isDefault(a.getIsDefault())
                .build();
    }
}