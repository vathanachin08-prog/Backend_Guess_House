package com.dinsaren.springbootjwtapi.models;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Table(name = "employees")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String position;

    @Column(nullable = false)
    private String department;

    @Column(name = "salary")
    @Builder.Default
    private Double salary = 0.0;

    @Column(name = "status")
    @Builder.Default
    private String status = "ACT";

    @Column(name = "profile_image")
    private String profileImage;
}
