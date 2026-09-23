package com.dinsaren.springbootjwtapi.models.rental;

import com.dinsaren.springbootjwtapi.models.BaseEntity;
import com.dinsaren.springbootjwtapi.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"rooms", "owner"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Property extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", length = 50, nullable = false)
    private PropertyType propertyType = PropertyType.APARTMENT;

    @Column(length = 255)
    private String address;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String district;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private PropertyStatus status = PropertyStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", length = 50, nullable = false)
    private PropertyVerificationStatus verificationStatus = PropertyVerificationStatus.PENDING;

    @Column(name = "main_image", length = 500)
    private String mainImage;

    @Column(columnDefinition = "TEXT")
    private String images;

    @JsonIgnore
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();
}
