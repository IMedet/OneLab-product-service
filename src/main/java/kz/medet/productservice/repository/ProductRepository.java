package kz.medet.productservice.repository;

import kz.medet.productservice.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String productName);
    Optional<Product> findByName(String productName);
}
