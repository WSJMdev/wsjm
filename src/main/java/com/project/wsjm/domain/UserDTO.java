package com.project.wsjm.domain;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserDTO {
    private String email;
    private String pwd;
    private String auth;
}
