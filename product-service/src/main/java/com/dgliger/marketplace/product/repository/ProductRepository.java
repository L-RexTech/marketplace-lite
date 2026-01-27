package com.dgliger.marketplace.product.repository;



import com.dgliger.marketplace.product.entity.Product;
import com.dgliger.marketplace.product.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByStatus(ProductStatus status);
    List<Product> findBySellerIdAndStatus(Long sellerId, ProductStatus status);
    Optional<Product> findByIdAndStatus(Long id, ProductStatus status);
    List<Product> findByCategoryAndStatus(String category, ProductStatus status);
}