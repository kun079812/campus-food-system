package com.campus.food.service;

import com.campus.food.entity.Stall;
import com.campus.food.mapper.StallMapper;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class StallService {

    private final StallMapper stallMapper;

    public StallService(StallMapper stallMapper) {
        this.stallMapper = stallMapper;
    }

    public Stall findByMerchantId(Long merchantId) {
        return stallMapper.findByMerchantId(merchantId);
    }

    public List<Stall> findEnabledByCanteenId(Long canteenId) {
        return stallMapper.findEnabledByCanteenId(canteenId);
    }
    public Stall getByMerchantId(Long merchantId) {
        Stall stall = stallMapper.findByMerchantId(merchantId);

        if (stall == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "当前商家没有关联档口"
            );
        }

        return stall;
    }

    public void updateByMerchant(
            Long merchantId,
            Stall requestStall
    ) {
        Stall stall = getByMerchantId(merchantId);

        if (requestStall.getName() == null
                || requestStall.getName().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "档口名称不能为空"
            );
        }

        if (requestStall.getBusinessHours() == null
                || requestStall.getBusinessHours().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "营业时间不能为空"
            );
        }

        stall.setName(requestStall.getName());
        stall.setBusinessHours(requestStall.getBusinessHours());

        stallMapper.update(stall);
    }
    public void updateStatusByMerchant(
            Long merchantId,
            Integer status
    ) {
        if (status == null || (status != 0 && status != 1)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "档口状态只能是 0 或 1"
            );
        }

        Stall stall = getByMerchantId(merchantId);

        stallMapper.updateStatus(stall.getId(), status);
    }
}