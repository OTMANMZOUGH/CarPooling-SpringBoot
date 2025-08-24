package org.project.carpooling.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cities_id")
    private Long citiesId;

    @Column(name = "name_fr", nullable = false)
    private String nameFr;

    @Column(name = "name_ar", nullable = false)
    private String nameAr;

    private BigDecimal latitude;
    private BigDecimal longitude;
}