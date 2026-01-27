package com.dgliger.marketplace.order.mapper;



import com.dgliger.marketplace.order.dto.OrderItemDto;
import com.dgliger.marketplace.order.dto.OrderResponse;
import com.dgliger.marketplace.order.entity.Order;
import com.dgliger.marketplace.order.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "items", target = "items")
    OrderResponse toResponse(Order order);

    List<OrderResponse> toResponseList(List<Order> orders);

    @Mapping(target = "order", ignore = true)
    OrderItem toEntity(OrderItemDto dto);

    OrderItemDto toDto(OrderItem item);
}