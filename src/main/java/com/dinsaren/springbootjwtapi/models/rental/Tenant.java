package com.dinsaren.springbootjwtapi.models.rental;

import com.dinsaren.springbootjwtapi.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;

@Entity(name = "RentalTenant")
@Table(name = "tenants")
@Data
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"property", "room"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tenant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "room_number", nullable = false, length = 50)
    private String roomNumber;

    @Column(length = 100)
    private String floor;

    @Column(name = "phone_number", length = 50)
    private String phoneNumber;

    @Column(length = 150)
    private String email;

    @Column(length = 50, nullable = false)
    private String status = "ACTIVE";

    @Column(name = "joined_date")
    private LocalDateTime joinedDate = LocalDateTime.now();
}

