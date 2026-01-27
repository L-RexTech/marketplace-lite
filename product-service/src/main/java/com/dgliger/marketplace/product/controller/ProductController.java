package com.dgliger.marketplace.product.controller;


import com.dgliger.marketplace.common.dto.ApiResponse;
import com.dgliger.marketplace.product.dto.ProductRequest;
import com.dgliger.marketplace.product.dto.ProductResponse;
import com.dgliger.marketplace.product.security.UserPrincipal;
import com.dgliger.marketplace.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getAllProducts() {
        List<ProductResponse> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getProductById(@PathVariable Long id) {
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponse> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/seller")
    public ResponseEntity<ApiResponse<List<ProductResponse>>> getSellerProducts(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<ProductResponse> products = productService.getSellerProducts(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        ProductResponse product = productService.createProduct(request, principal.getUserId(), principal.getRoles());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Product created successfully", product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {

        ProductResponse product = productService.updateProduct(id, request, principal.getUserId(), principal.getRoles());
        return ResponseEntity.ok(ApiResponse.success("Product updated successfully", product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal) {

        productService.deleteProduct(id, principal.getUserId(), principal.getRoles());
        return ResponseEntity.ok(ApiResponse.success("Product deleted successfully", null));
    }

    // Internal endpoint for order service
    @PostMapping("/{id}/update-stock")
    public ResponseEntity<Void> updateStock(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.updateStock(id, quantity);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/check-stock")
    public ResponseEntity<Boolean> checkStock(@PathVariable Long id, @RequestParam Integer quantity) {
        boolean available = productService.checkStock(id, quantity);
        return ResponseEntity.ok(available);
    }
}
