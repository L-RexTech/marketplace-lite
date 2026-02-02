package com.dgliger.marketplace.product.service;


import com.dgliger.marketplace.common.exception.BusinessException;
import com.dgliger.marketplace.common.exception.ResourceNotFoundException;
import com.dgliger.marketplace.common.exception.UnauthorizedException;
import com.dgliger.marketplace.product.dto.ProductRequest;
import com.dgliger.marketplace.product.dto.ProductResponse;
import com.dgliger.marketplace.product.entity.Product;
import com.dgliger.marketplace.product.enums.ProductStatus;
import com.dgliger.marketplace.product.mapper.ProductMapper;
import com.dgliger.marketplace.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public List<ProductResponse> getAllActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findByIdAndStatus(id, ProductStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        return productMapper.toResponse(product);
    }

    public List<ProductResponse> getProductsByCategory(String category) {
        return productRepository.findByCategoryAndStatus(category, ProductStatus.ACTIVE).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<ProductResponse> getSellerProducts(Long sellerId) {
        return productRepository.findBySellerIdAndStatus(sellerId, ProductStatus.ACTIVE).stream()
                .map(productMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, Long sellerId, List<String> roles) {
        if (!roles.contains("SELLER") && !roles.contains("ADMIN")) {
            throw new UnauthorizedException("Only sellers can create products");
        }

        Product product = productMapper.toEntity(request);
        product.setSellerId(sellerId);
        product.setStatus(ProductStatus.ACTIVE);

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponse(savedProduct);
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request, Long userId, List<String> roles) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!roles.contains("ADMIN") && !product.getSellerId().equals(userId)) {
            throw new UnauthorizedException("You can only update your own products");
        }

        productMapper.updateEntity(request, product);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toResponse(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id, Long userId, List<String> roles) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        if (!roles.contains("ADMIN") && !product.getSellerId().equals(userId)) {
            throw new UnauthorizedException("You can only delete your own products");
        }

        product.setStatus(ProductStatus.DELETED);
        productRepository.save(product);
    }

    @Transactional
    public void updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        if (product.getStock() < quantity) {
            throw new BusinessException("Insufficient stock for product: " + product.getName());
        }

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);
    }

    public boolean checkStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        return product.getStock() >= quantity;
    }

    @Transactional
    public void increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));

        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }
}
