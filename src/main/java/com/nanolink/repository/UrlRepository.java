package com.nanolink.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanolink.models.Url;

public interface UrlRepository extends JpaRepository<Url, Long> {
    
    Optional<Url> findByShortCode(String shortCode);
    
    boolean existsByShortCode(String shortCode);

    Optional<Url> findByShortCodeAndIsActiveTrue(String shortCode);
}
