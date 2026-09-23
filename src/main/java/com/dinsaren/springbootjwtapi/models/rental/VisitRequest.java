package com.dinsaren.springbootjwtapi.models.rental;

import com.dinsaren.springbootjwtapi.models.BaseEntity;
import com.dinsaren.springbootjwtapi.models.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

@Entity
@Table(name = "visit_requests")
@Data
@DynamicUpdate
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"student", "property", "room"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class VisitRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "requested_date", nullable = false)
    private LocalDate requestedDate;

    @Column(name = "requested_time", length = 50)
    private String requestedTime;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private VisitRequestStatus status = VisitRequestStatus.PENDING;
}
