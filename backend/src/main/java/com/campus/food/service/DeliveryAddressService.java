package com.campus.food.service;

import com.campus.food.entity.DeliveryAddress;
import com.campus.food.mapper.DeliveryAddressMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DeliveryAddressService {

    private final DeliveryAddressMapper deliveryAddressMapper;

    public DeliveryAddressService(
            DeliveryAddressMapper deliveryAddressMapper
    ) {
        this.deliveryAddressMapper = deliveryAddressMapper;
    }

    public List<DeliveryAddress> findByStudentId(Long studentId) {
        return deliveryAddressMapper.findByStudentId(studentId);
    }

    @Transactional
    public DeliveryAddress add(
            Long studentId,
            DeliveryAddress address
    ) {
        validateAddress(address);

        address.setStudentId(studentId);
        address.setStatus(1);

        if (address.getIsDefault() == null) {
            address.setIsDefault(0);
        }

        if (address.getIsDefault() == 1) {
            deliveryAddressMapper.clearDefaultByStudentId(studentId);
        }

        deliveryAddressMapper.insert(address);

        return address;
    }

    @Transactional
    public void update(
            Long studentId,
            Long addressId,
            DeliveryAddress address
    ) {
        DeliveryAddress oldAddress =
                requireOwnedAddress(studentId, addressId);

        validateAddress(address);

        address.setId(addressId);
        address.setStudentId(studentId);
        address.setStatus(oldAddress.getStatus());

        if (address.getIsDefault() == null) {
            address.setIsDefault(oldAddress.getIsDefault());
        }

        if (address.getIsDefault() == 1) {
            deliveryAddressMapper.clearDefaultByStudentId(studentId);
        }

        deliveryAddressMapper.update(address);
    }

    public void delete(
            Long studentId,
            Long addressId
    ) {
        requireOwnedAddress(studentId, addressId);

        deliveryAddressMapper.deleteById(addressId);
    }

    private DeliveryAddress requireOwnedAddress(
            Long studentId,
            Long addressId
    ) {
        DeliveryAddress address =
                deliveryAddressMapper.findById(addressId);

        if (address == null || address.getStatus() != 1) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "地址不存在"
            );
        }

        if (!studentId.equals(address.getStudentId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "无权操作其他学生的地址"
            );
        }

        return address;
    }

    private void validateAddress(DeliveryAddress address) {
        if (address.getContactName() == null
                || address.getContactName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "联系人不能为空"
            );
        }

        if (address.getPhone() == null
                || address.getPhone().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "联系电话不能为空"
            );
        }

        if (address.getDetail() == null
                || address.getDetail().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "详细地址不能为空"
            );
        }
    }
}