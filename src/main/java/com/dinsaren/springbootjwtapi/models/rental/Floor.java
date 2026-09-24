package com.dinsaren.springbootjwtapi.models.rental;

import com.dinsaren.springbootjwtapi.models.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Entity(name = "RentalFloor")
@Table(name = "floors", uniqueConstraints = {
        @UniqueConstraint(name = "uq_property_floor_name", columnNames = {"property_id", "name"})
})
@Data
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"property"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Floor extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "floor_order")
    private Integer floorOrder = 1;

    @Column(length = 50, nullable = false)
    private String status = "ACTIVE";
}
