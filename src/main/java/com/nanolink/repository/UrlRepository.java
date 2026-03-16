package com.nanolink.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nanolink.models.Url;

public interface UrlRepository extends JpaRepository<Url, Long> {
    
    List<Url> findAllByUser_EmailOrderByCreatedAtDesc(String email);

    Optional<Url> findByShortCode(String shortCode);

    Optional<Url> findByShortCodeAndUser_Email(String shortCode, String email);
    
    boolean existsByShortCode(String shortCode);

    Optional<Url> findByShortCodeAndIsActiveTrue(String shortCode);
}
