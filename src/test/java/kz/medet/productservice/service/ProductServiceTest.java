package kz.medet.productservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.productservice.repository.ProductRepository;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.List;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import kz.medet.productservice.dto.CreateProductDto;
import kz.medet.productservice.dto.OrderRequest;
import kz.medet.productservice.dto.ProductResponse;
import kz.medet.productservice.dto.ProductWithOrderIdsDto;
import kz.medet.productservice.entity.Product;
import kz.medet.productservice.repository.ProductRepository;
import kz.medet.productservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private ProductService productService;

    private OrderRequest orderRequest;
    private CreateProductDto createProductDto;
    private Product product;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest(1L, List.of(1L, 2L));
        createProductDto = new CreateProductDto(1L, "Test Product", 100.0);
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(100.0);
    }

    @Test
    void listenProductRequests_WhenProductsExist_ShouldSendResponse() throws Exception {
        String message = "{}";
        when(objectMapper.readValue(message, OrderRequest.class)).thenReturn(orderRequest);
        when(productRepository.findAllById(orderRequest.getProductIds())).thenReturn(List.of(product));

        productService.listenProductRequests(message);

        verify(kafkaTemplate).send(eq("product.to.order.responses"), any(ProductResponse.class));
    }

    @Test
    void listenProductRequestsCreteDto_WhenValidRequest_ShouldSaveProductAndSendKafkaMessage() throws Exception {
        String message = "{}";
        when(objectMapper.readValue(message, CreateProductDto.class)).thenReturn(createProductDto);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.listenProductRequestsCreteDto(message);

        verify(productRepository).save(any(Product.class));
        verify(kafkaTemplate).send(eq("product.to.order.ProductCreated"), any(ProductWithOrderIdsDto.class));
    }

    @Test
    void listenProductRequestsCreteDto_WhenJsonParsingFails_ShouldNotThrowException() throws Exception {
        String message = "invalid_json";
        when(objectMapper.readValue(message, CreateProductDto.class)).thenThrow(new RuntimeException("Parsing Error"));

        assertDoesNotThrow(() -> productService.listenProductRequestsCreteDto(message));
        verify(kafkaTemplate, never()).send(anyString(), any());
    }
}
