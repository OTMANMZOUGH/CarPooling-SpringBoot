package org.project.carpooling.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String username;
    private String phone;
    private String password;
}
