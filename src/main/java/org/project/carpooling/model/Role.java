package org.project.carpooling.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")  // Explicitly name the column
    private Long roleId;  // Changed from user_id to follow Java naming conventions

    private String name; // ROLE_USER, ROLE_ADMIN

}
