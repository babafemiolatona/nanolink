package com.nanolink.models;

import lombok.*;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "clicks", indexes = {
    @Index(name = "idx_url_id", columnList = "url_id"),
    @Index(name = "idx_clicked_at", columnList = "clicked_at")
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Click {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    private Url url;

    @CreationTimestamp
    @Column(name = "clicked_at", nullable = false, updatable = false)
    private LocalDate clickedAt;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Column(name = "referrer", length = 512)
    private String referrer;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "device_type", length = 50)
    private String deviceType;

    @Column(name = "browser", length = 50)
    private String browser;

    @Column(name = "operating_system", length = 50)
    private String operatingSystem;
}
