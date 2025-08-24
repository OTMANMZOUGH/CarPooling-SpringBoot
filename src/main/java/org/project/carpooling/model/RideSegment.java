package org.project.carpooling.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ride_segments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideSegment {
    @Id
    @Column(name = "segment_id", columnDefinition = "CHAR(36)")
    private String segmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ride_id", nullable = false)
    private Ride ride;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "start_cities_id", nullable = false)
    private City startCity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_cities_id", nullable = false)
    private City endCity;

    @Column(name = "segment_order", nullable = false)
    private Integer segmentOrder; // Correction du nom

    @Column(name = "distance_km")
    private BigDecimal distanceKm;

    @Column(name = "estimated_duration_min")
    private Integer estimatedDurationMin;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats;

    @Enumerated(EnumType.STRING)
    private SegmentStatus status;

    @Column(name = "actual_departure_time")
    private LocalDateTime actualDepartureTime;

    @Column(name = "actual_arrival_time")
    private LocalDateTime actualArrivalTime;

    @OneToMany(mappedBy = "segment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<SegmentBooking> segmentBookings = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.segmentId == null) {
            this.segmentId = UUID.randomUUID().toString();
        }
    }
}