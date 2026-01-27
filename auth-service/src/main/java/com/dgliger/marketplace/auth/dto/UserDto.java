package com.dgliger.marketplace.auth.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserDto {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private List<String> roles;
    private Boolean enabled;
}