package org.project.carpooling.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "segment_bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SegmentBooking {
    @Id
    @Column(name = "segment_booking_id", columnDefinition = "CHAR(36)")
    private String segmentBookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "segment_id", nullable = false)
    private RideSegment segment; // ManyToOne vers RideSegment

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking; // ManyToOne vers Booking

    @Column(name = "seats_reserved", nullable = false)
    private Integer seatsReserved;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (this.segmentBookingId == null) {
            this.segmentBookingId = UUID.randomUUID().toString();
        }
    }
}