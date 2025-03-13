package kz.medet.productservice.controller;

import kz.medet.productservice.dto.ProductDto;
import kz.medet.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestParam String productName,
                                                    @RequestParam double productPrice) {
        ProductDto productDto = productService.createProduct(productName, productPrice);
        return new ResponseEntity<>(productDto, HttpStatus.CREATED);
    }

    @PostMapping("/products/byIds")
    public ResponseEntity<List<ProductDto>> getProductsByIds(@RequestBody List<Long> productIds) {
        return ResponseEntity.ok(productService.getProductsByIds(productIds));
    }

}
