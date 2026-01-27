package com.dgliger.marketplace.order.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class UserPrincipal {
    private Long userId;
    private String email;
    private List<String> roles;
}
