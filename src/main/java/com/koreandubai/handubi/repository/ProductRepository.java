package com.koreandubai.handubi.repository;

import com.koreandubai.handubi.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIsActiveTrueAndDeadlineIsNullOrIsActiveTrueAndDeadlineAfter(LocalDateTime now);
}
