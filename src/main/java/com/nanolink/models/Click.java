package com.nanolink.models;

import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
public class Click implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Url url;

    @CreationTimestamp
    @Column(name = "clicked_at", nullable = false, updatable = false)
    private LocalDateTime clickedAt;

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
