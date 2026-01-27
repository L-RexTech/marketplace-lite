package com.dgliger.marketplace.product.mapper;



import com.dgliger.marketplace.product.dto.ProductRequest;
import com.dgliger.marketplace.product.dto.ProductResponse;
import com.dgliger.marketplace.product.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductMapper {
    ProductResponse toResponse(Product product);
    Product toEntity(ProductRequest request);
    void updateEntity(ProductRequest request, @MappingTarget Product product);
}
