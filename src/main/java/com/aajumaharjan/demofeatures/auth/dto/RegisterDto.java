package com.aajumaharjan.demofeatures.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RegisterDto {
    private String name;
    private String email;
    private String password;
    private List<String> roles;
}
