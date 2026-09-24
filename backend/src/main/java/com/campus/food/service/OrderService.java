package com.campus.food.service;

import com.campus.food.entity.CartItem;
import com.campus.food.entity.DeliveryAddress;
import com.campus.food.entity.Dish;
import com.campus.food.entity.MerchantDailyStatistics;
import com.campus.food.entity.MerchantDashboard;
import com.campus.food.entity.MerchantDishSales;
import com.campus.food.entity.OrderEntity;
import com.campus.food.entity.OrderItem;
import com.campus.food.entity.OrderStatusLog;
import com.campus.food.entity.Payment;
import com.campus.food.entity.Stall;
import com.campus.food.mapper.CartItemMapper;
import com.campus.food.mapper.DeliveryAddressMapper;
import com.campus.food.mapper.DishMapper;
import com.campus.food.mapper.OrderItemMapper;
import com.campus.food.mapper.OrderMapper;
import com.campus.food.mapper.PaymentMapper;
import com.campus.food.vo.OrderCreateRequest;
import com.campus.food.vo.OrderDetailVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderStatusLogService orderStatusLogService;
    private final StallService stallService;
    private final CartItemMapper cartItemMapper;
    private final DishMapper dishMapper;
    private final DeliveryAddressMapper deliveryAddressMapper;
    private final PaymentMapper paymentMapper;

    public OrderService(
            OrderMapper orderMapper,
            OrderItemMapper orderItemMapper,
            OrderStatusLogService orderStatusLogService,
            StallService stallService,
            CartItemMapper cartItemMapper,
            DishMapper dishMapper,
            DeliveryAddressMapper deliveryAddressMapper,
            PaymentMapper paymentMapper
    ) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.orderStatusLogService = orderStatusLogService;
        this.stallService = stallService;
        this.cartItemMapper = cartItemMapper;
        this.dishMapper = dishMapper;
        this.deliveryAddressMapper = deliveryAddressMapper;
        this.paymentMapper = paymentMapper;
    }

    public List<OrderEntity> findByStallId(Long stallId) {
        return orderMapper.findByStallId(stallId);
    }

    public List<OrderEntity> findByMerchantId(Long merchantId) {
        Stall stall = requireMerchantStall(merchantId);

        return orderMapper.findByStallId(stall.getId());
    }

    public List<OrderEntity> findByStudentId(Long studentId) {
        return orderMapper.findByUserId(studentId);
    }

    public OrderDetailVO getDetailByStudent(
            Long studentId,
            Long orderId
    ) {
        OrderEntity order = requireStudentOrder(studentId, orderId);

        return buildOrderDetail(order);
    }

    public OrderDetailVO getDetailByMerchant(
            Long merchantId,
            Long orderId
    ) {
        verifyOrderOwnership(merchantId, orderId);

        OrderEntity order = orderMapper.findById(orderId);

        return buildOrderDetail(order);
    }

    public MerchantDashboard getDashboardByMerchantId(Long merchantId) {
        Stall stall = requireMerchantStall(merchantId);

        MerchantDashboard dashboard = new MerchantDashboard();

        dashboard.setTodayOrderCount(
                orderMapper.countTodayOrder(stall.getId())
        );

        dashboard.setTodaySales(
                orderMapper.sumTodaySales(stall.getId())
        );

        dashboard.setPendingOrderCount(
                orderMapper.countPendingOrder(stall.getId())
        );

        dashboard.setLatestOrders(
                orderMapper.findLatestByStallId(stall.getId())
        );

        return dashboard;
    }

    public List<MerchantDailyStatistics> getDailyStatistics(
            Long merchantId,
            String startDate,
            String endDate
    ) {
        LocalDate start;
        LocalDate end;

        try {
            start = LocalDate.parse(startDate);
            end = LocalDate.parse(endDate);
        } catch (DateTimeParseException exception) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "日期格式必须是 yyyy-MM-dd"
            );
        }

        if (start.isAfter(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "开始日期不能晚于结束日期"
            );
        }

        if (start.plusDays(31).isBefore(end)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "一次最多查询 32 天"
            );
        }

        Stall stall = requireMerchantStall(merchantId);

        return orderMapper.findDailyStatistics(
                stall.getId(),
                startDate,
                endDate
        );
    }

    public List<MerchantDishSales> getDishSalesByMerchantId(
            Long merchantId
    ) {
        Stall stall = requireMerchantStall(merchantId);

        return orderItemMapper.findDishSalesByStallId(stall.getId());
    }

    @Transactional
    public OrderEntity createByStudent(
            Long studentId,
            OrderCreateRequest request
    ) {
        if (request == null
                || request.getItemIds() == null
                || request.getItemIds().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "请选择需要结算的购物车菜品"
            );
        }

        List<Long> itemIds = request.getItemIds();

        if (itemIds.contains(null)
                || new HashSet<>(itemIds).size() != itemIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "购物车项参数不正确"
            );
        }

        List<CartItem> cartItems =
                cartItemMapper.findByUserIdAndIds(
                        studentId,
                        itemIds
                );

        if (cartItems.size() != itemIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "存在无效的购物车项"
            );
        }

        String deliveryType = request.getPickupType();

        if (!isPickupOrder(deliveryType)
                && !isDeliveryOrder(deliveryType)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "配送方式只能是 PICKUP 或 DELIVERY"
            );
        }

        String remark = normalizeRemark(request.getRemark());

        Long addressId = null;

        if (isDeliveryOrder(deliveryType)) {
            DeliveryAddress address = deliveryAddressMapper.findById(
                    request.getAddressId()
            );

            if (address == null
                    || !Integer.valueOf(1).equals(address.getStatus())
                    || !studentId.equals(address.getStudentId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "配送地址不存在或不属于当前学生"
                );
            }

            addressId = address.getId();
        }

        Long stallId = null;
        double totalAmount = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Dish dish = requireAvailableDish(cartItem.getDishId());

            if (cartItem.getQuantity() == null
                    || cartItem.getQuantity() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "购物车菜品数量不正确"
                );
            }

            if (stallId == null) {
                stallId = dish.getStallId();
            } else if (!stallId.equals(dish.getStallId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "一次下单只能购买同一档口的菜品"
                );
            }

            if (dish.getStock() < cartItem.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        dish.getName() + " 库存不足"
                );
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setDishId(dish.getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(dish.getPrice());

            orderItems.add(orderItem);

            totalAmount += dish.getPrice() * cartItem.getQuantity();
        }

        OrderEntity order = new OrderEntity();
        order.setOrderNo(createOrderNo());
        order.setUserId(studentId);
        order.setStallId(stallId);
        order.setTotalAmount(totalAmount);
        order.setPayAmount(0.00);
        order.setStatus("WAIT_PAY");
        order.setDeliveryType(deliveryType);
        order.setAddressId(addressId);
        order.setRemark(remark);

        orderMapper.insert(order);

        for (OrderItem orderItem : orderItems) {
            orderItem.setOrderId(order.getId());
            orderItemMapper.insert(orderItem);
        }

        cartItemMapper.deleteByUserIdAndIds(
                studentId,
                itemIds
        );

        recordStatus(order.getId(), "CREATE", "WAIT_PAY");

        return order;
    }

    @Transactional
    public Payment payByStudent(
            Long studentId,
            Long orderId,
            String paymentMethod
    ) {
        if (!"MOCK".equals(paymentMethod)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "本系统只支持 MOCK 模拟支付"
            );
        }

        OrderEntity order = requireStudentOrder(studentId, orderId);

        if (!"WAIT_PAY".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "只有待支付订单可以支付"
            );
        }

        if (paymentMapper.findByOrderId(orderId) != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "该订单已经支付过"
            );
        }

        List<OrderItem> orderItems =
                orderItemMapper.findByOrderId(orderId);

        for (OrderItem orderItem : orderItems) {
            Dish dish = requireAvailableDish(orderItem.getDishId());

            if (dish.getStock() < orderItem.getQuantity()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        dish.getName() + " 库存不足"
                );
            }

            int result = dishMapper.decreaseStock(
                    orderItem.getDishId(),
                    orderItem.getQuantity()
            );

            if (result != 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "菜品库存不足"
                );
            }
        }

        Payment payment = new Payment();
        payment.setPaymentNo(createPaymentNo(orderId));
        payment.setOrderId(orderId);
        payment.setUserId(studentId);
        payment.setPayAmount(order.getTotalAmount());
        payment.setPayMethod("MOCK");
        payment.setPayStatus("SUCCESS");

        paymentMapper.insert(payment);

        orderMapper.updatePayAmount(
                order.getId(),
                order.getTotalAmount()
        );

        updateStatusAndRecord(order, "WAIT_ACCEPT");

        return payment;
    }

    @Transactional
    public void cancelByStudent(Long studentId, Long orderId) {
        OrderEntity order = requireStudentOrder(studentId, orderId);

        if (!"WAIT_PAY".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "只有待支付订单可以取消"
            );
        }

        updateStatusAndRecord(order, "CANCELLED");
    }

    @Transactional
    public void confirmByStudent(Long studentId, Long orderId) {
        OrderEntity order = requireStudentOrder(studentId, orderId);

        if (!"READY_PICKUP".equals(order.getStatus())
                && !"DELIVERED".equals(order.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "当前订单不能确认完成"
            );
        }

        updateStatusAndRecord(order, "COMPLETED");
    }

    @Transactional
    public int changeStatusByMerchant(
            Long merchantId,
            Long orderId,
            String status
    ) {
        verifyOrderOwnership(merchantId, orderId);

        return changeStatus(orderId, status);
    }

    public int changeStatus(Long orderId, String status) {
        OrderEntity order = orderMapper.findById(orderId);

        if (order == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "订单不存在"
            );
        }

        if (!canChangeStatus(
                order.getStatus(),
                status,
                order.getDeliveryType()
        )) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "订单状态不能从 "
                            + order.getStatus()
                            + " 修改为 "
                            + status
            );
        }

        updateStatusAndRecord(order, status);

        return 1;
    }

    public void verifyOrderOwnership(
            Long merchantId,
            Long orderId
    ) {
        Stall stall = requireMerchantStall(merchantId);
        OrderEntity order = orderMapper.findById(orderId);

        if (order == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "订单不存在"
            );
        }

        if (!stall.getId().equals(order.getStallId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "无权操作其他档口的订单"
            );
        }
    }

    public void verifyStudentOrderOwnership(
            Long studentId,
            Long orderId
    ) {
        requireStudentOrder(studentId, orderId);
    }

    private OrderEntity requireStudentOrder(
            Long studentId,
            Long orderId
    ) {
        OrderEntity order = orderMapper.findById(orderId);

        if (order == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "订单不存在"
            );
        }

        if (!studentId.equals(order.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "无权操作其他学生的订单"
            );
        }

        return order;
    }

    private Dish requireAvailableDish(Long dishId) {
        Dish dish = dishMapper.findEnabledById(dishId);

        if (dish == null
                || !Integer.valueOf(1).equals(dish.getStatus())
                || dish.getStock() == null
                || dish.getStock() <= 0
                || dish.getPrice() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "购物车中存在无效菜品"
            );
        }

        return dish;
    }

    private void updateStatusAndRecord(
            OrderEntity order,
            String newStatus
    ) {
        orderMapper.changeStatus(order.getId(), newStatus);
        recordStatus(order.getId(), order.getStatus(), newStatus);
    }

    private void recordStatus(
            Long orderId,
            String oldStatus,
            String newStatus
    ) {
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderId);
        log.setOldStatus(oldStatus);
        log.setNewStatus(newStatus);

        orderStatusLogService.insert(log);
    }

    private String createOrderNo() {
        return "ORD"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 20)
                .toUpperCase();
    }

    private String normalizeRemark(String remark) {
        if (remark == null || remark.isBlank()) {
            return null;
        }

        String normalizedRemark = remark.trim();

        if (normalizedRemark.length() > 255) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "订单备注不能超过 255 个字"
            );
        }

        return normalizedRemark;
    }

    private String createPaymentNo(Long orderId) {
        return "PAY" + System.currentTimeMillis() + orderId;
    }

    private Stall requireMerchantStall(Long merchantId) {
        Stall stall = stallService.findByMerchantId(merchantId);

        if (stall == null) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "当前商家没有关联档口"
            );
        }

        return stall;
    }

    private boolean canChangeStatus(
            String oldStatus,
            String newStatus,
            String deliveryType
    ) {
        if ("WAIT_ACCEPT".equals(oldStatus)
                && "MAKING".equals(newStatus)) {
            return true;
        }

        if (isPickupOrder(deliveryType)
                && "MAKING".equals(oldStatus)
                && "READY_PICKUP".equals(newStatus)) {
            return true;
        }

        if (isDeliveryOrder(deliveryType)
                && "MAKING".equals(oldStatus)
                && "DELIVERING".equals(newStatus)) {
            return true;
        }

        return isDeliveryOrder(deliveryType)
                && "DELIVERING".equals(oldStatus)
                && "DELIVERED".equals(newStatus);
    }

    private boolean isPickupOrder(String deliveryType) {
        return "PICKUP".equals(deliveryType)
                || "SELF_PICKUP".equals(deliveryType);
    }

    private boolean isDeliveryOrder(String deliveryType) {
        return "DELIVERY".equals(deliveryType);
    }

    private OrderDetailVO buildOrderDetail(OrderEntity order) {
        OrderDetailVO detail = new OrderDetailVO();

        detail.setOrder(order);

        detail.setOrderItems(
                orderItemMapper.findByOrderId(order.getId())
        );

        detail.setStatusLogs(
                orderStatusLogService.findByOrderId(order.getId())
        );

        if (order.getAddressId() != null) {
            detail.setAddress(
                    deliveryAddressMapper.findById(
                            order.getAddressId()
                    )
            );
        }

        return detail;
    }

    public List<OrderEntity> findByMerchantIdAndStatus(
            Long merchantId,
            String status
    ) {
        Stall stall = requireMerchantStall(merchantId);

        if (status == null || status.isBlank()) {
            return orderMapper.findByStallId(stall.getId());
        }

        validateOrderStatus(status);

        return orderMapper.findByStallIdAndStatus(
                stall.getId(),
                status
        );
    }

    public List<OrderEntity> findByStudentIdAndStatus(
            Long studentId,
            String status
    ) {
        if (status == null || status.isBlank()) {
            return orderMapper.findByUserId(studentId);
        }

        validateOrderStatus(status);

        return orderMapper.findByUserIdAndStatus(
                studentId,
                status
        );
    }

    private void validateOrderStatus(String status) {
        if (!"WAIT_PAY".equals(status)
                && !"WAIT_ACCEPT".equals(status)
                && !"MAKING".equals(status)
                && !"READY_PICKUP".equals(status)
                && !"DELIVERING".equals(status)
                && !"DELIVERED".equals(status)
                && !"COMPLETED".equals(status)
                && !"CANCELLED".equals(status)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "订单状态参数不正确"
            );
        }
    }
}