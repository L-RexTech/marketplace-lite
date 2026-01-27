package com.dgliger.marketplace.order.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "http://localhost:8082/api/products")
public interface ProductClient {

    @GetMapping("/{id}/check-stock")
    Boolean checkStock(@PathVariable Long id, @RequestParam Integer quantity);

    @PostMapping("/{id}/update-stock")
    void updateStock(@PathVariable Long id, @RequestParam Integer quantity);
}