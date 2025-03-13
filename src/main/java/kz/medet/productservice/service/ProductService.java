package kz.medet.productservice.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.productservice.dto.*;
import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.exceptions.CustomException;
import kz.medet.productservice.entity.Product;
import kz.medet.productservice.mapper.ProductMapper;
import kz.medet.productservice.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private KafkaTemplate<String, Object> kafkaTemplate;
    private ProductRepository productRepository;
    private ObjectMapper objectMapper;

    private ProductMapper productMapper;

    @Transactional
    public ProductDto createProduct(String productName, double productPrice) {
        Product product = new Product();
        product.setName(productName);
        product.setPrice(productPrice);
        productRepository.save(product);

        return new ProductDto(product.getId(), product.getName(),product.getPrice(),product.getDescription());
    }

    @Transactional
    public List<ProductDto> getProductsByIds(List<Long> productIds) {
        List<Product> products = productRepository.findAllById(productIds);

        return products.stream()
                .map(productMapper::toDto)
                .collect(Collectors.toList());
    }


//    @KafkaListener(topics = "order.to.product.requests", groupId = "product-group")
//    public void listenProductRequests(String message) {
//        log.info("Message: {}", message);
//        try {
//            OrderRequest orderRequest = objectMapper.readValue(message, OrderRequest.class);
//            List<Product> products = productRepository.findAllById(orderRequest.getProductIds());
//
//            ProductResponse response = new ProductResponse(orderRequest.getOrderId(), products);
//            kafkaTemplate.send("product.to.order.responses", response);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    @KafkaListener(topics = "order.to.product.createProduct", groupId = "product-group")
//    public void listenProductRequestsCreteDto(String message) {
//        log.info("Message: {}", message);
//        try {
//            CreateProductDto createProductDto = objectMapper.readValue(message, CreateProductDto.class);
//            Product product = new Product();
//            product.setName(createProductDto.getName());
//            product.setPrice(createProductDto.getPrice());
//
//            productRepository.save(product);
//
//            ProductWithOrderIdsDto productWithOrderIdsDto = new ProductWithOrderIdsDto();
//            productWithOrderIdsDto.setOrderId(createProductDto.getOrderId());
//            productWithOrderIdsDto.setProductId(product.getId());
//
//            kafkaTemplate.send("product.to.order.ProductCreated", productWithOrderIdsDto);
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
//        }
//    }

}
