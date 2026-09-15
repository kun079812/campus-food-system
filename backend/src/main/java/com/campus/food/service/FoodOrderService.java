package com.campus.food.service;

import com.campus.food.entity.FoodOrder;
import com.campus.food.entity.OrderStatusLog;
import com.campus.food.mapper.FoodOrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodOrderService {

    private final FoodOrderMapper foodOrderMapper;

    private final OrderStatusLogService orderStatusLogService;


    public FoodOrderService(
            FoodOrderMapper foodOrderMapper,
            OrderStatusLogService orderStatusLogService
    ) {
        this.foodOrderMapper = foodOrderMapper;
        this.orderStatusLogService = orderStatusLogService;
    }


    public List<FoodOrder> findByStallId(Long stallId) {
        return foodOrderMapper.findByStallId(stallId);
    }


    public int updateStatus(Long id, String status) {

        FoodOrder oldOrder = foodOrderMapper.findById(id);


        String oldStatus = oldOrder.getStatus();


        // 判断订单状态是否允许流转
        if (!canChangeStatus(oldStatus, status)) {

            throw new RuntimeException(
                    "订单状态不能从 " + oldStatus + " 修改为 " + status
            );

        }


        int result = foodOrderMapper.updateStatus(id, status);


        OrderStatusLog log = new OrderStatusLog();

        log.setOrderId(id);
        log.setOldStatus(oldStatus);
        log.setNewStatus(status);


        orderStatusLogService.insert(log);


        return result;
    }
    private boolean canChangeStatus(
            String oldStatus,
            String newStatus
    ) {


        if (oldStatus.equals("PENDING")
                && newStatus.equals("ACCEPTED")) {

            return true;

        }


        if (oldStatus.equals("ACCEPTED")
                && newStatus.equals("READY")) {

            return true;

        }


        if (oldStatus.equals("READY")
                && newStatus.equals("COMPLETED")) {

            return true;

        }


        return false;

    }

}