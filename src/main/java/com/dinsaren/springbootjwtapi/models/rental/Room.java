package com.dinsaren.springbootjwtapi.models.rental;

import com.dinsaren.springbootjwtapi.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity(name = "RentalRoom")
@Table(name = "rooms", uniqueConstraints = {
        @UniqueConstraint(name = "uq_property_room_number", columnNames = {"property_id", "room_number"})
})
@Data
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"property", "facilities"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "room_type", length = 50, nullable = false)
    private RoomType roomType = RoomType.SINGLE;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_preference", length = 50, nullable = false)
    private GenderPreference genderPreference = GenderPreference.ANY;

    @Column(nullable = false)
    private Boolean available = true;

    private Integer floor;

    private Double area;

    @Column(columnDefinition = "TEXT")
    private String images;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "room_facilities",
            joinColumns = @JoinColumn(name = "room_id"),
            inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    private Set<Facility> facilities = new HashSet<>();
}
