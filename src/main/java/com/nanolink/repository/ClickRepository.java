package com.nanolink.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nanolink.models.Click;
import com.nanolink.models.Url;

public interface ClickRepository extends JpaRepository<Click, Long> {

    List<Click> findByUrl(Url url);

    List<Click> findByUrlOrderByClickedAtDesc(Url url);
    
    long countByUrl(Url url);
    
    List<Click> findByUrlAndClickedAtBetween(Url url, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT DATE(c.clickedAt) as date, COUNT(c) as count " +
           "FROM Click c WHERE c.url = :url " +
           "GROUP BY DATE(c.clickedAt) " +
           "ORDER BY DATE(c.clickedAt)")
    List<Object[]> countClicksByDate(@Param("url") Url url);
    
    @Query("SELECT c.country, COUNT(c) as count " +
           "FROM Click c WHERE c.url = :url AND c.country IS NOT NULL " +
           "GROUP BY c.country " +
           "ORDER BY count DESC")
    List<Object[]> getTopCountries(@Param("url") Url url);
    
    @Query("SELECT c.browser, COUNT(c) as count " +
           "FROM Click c WHERE c.url = :url AND c.browser IS NOT NULL " +
           "GROUP BY c.browser " +
           "ORDER BY count DESC")
    List<Object[]> getBrowserDistribution(@Param("url") Url url);
}
