package kz.medet.productservice.service;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.productservice.dto.CreateProductDto;
import kz.medet.orderservice.entity.Order;
import kz.medet.orderservice.exceptions.CustomException;
import kz.medet.productservice.dto.OrderRequest;
import kz.medet.productservice.dto.ProductResponse;
import kz.medet.productservice.dto.ProductWithOrderIdsDto;
import kz.medet.productservice.entity.Product;
import kz.medet.productservice.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ProductService {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ProductService(KafkaTemplate<String, Object> kafkaTemplate,
                          ProductRepository productRepository,
                          ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.productRepository = productRepository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "order.to.product.requests", groupId = "product-group")
    public void listenProductRequests(String message) {
        log.info("Message: {}", message);
        try {
            OrderRequest orderRequest = objectMapper.readValue(message, OrderRequest.class);
            List<Product> products = productRepository.findAllById(orderRequest.getProductIds());

            ProductResponse response = new ProductResponse(orderRequest.getOrderId(), products);
            kafkaTemplate.send("product.to.order.responses", response);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @KafkaListener(topics = "order.to.product.createProduct", groupId = "product-group")
    public void listenProductRequestsCreteDto(String message) {
        log.info("Message: {}", message);
        try {
            CreateProductDto createProductDto = objectMapper.readValue(message, CreateProductDto.class);
            Product product = new Product();
            product.setName(createProductDto.getName());
            product.setPrice(createProductDto.getPrice());

            productRepository.save(product);

            ProductWithOrderIdsDto productWithOrderIdsDto = new ProductWithOrderIdsDto();
            productWithOrderIdsDto.setOrderId(createProductDto.getOrderId());
            productWithOrderIdsDto.setProductId(product.getId());

            kafkaTemplate.send("product.to.order.ProductCreated", productWithOrderIdsDto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

}
