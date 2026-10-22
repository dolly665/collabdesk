package com.in6206.collabdesk.dto;

import com.in6206.collabdesk.entity.Platform;
import com.in6206.collabdesk.entity.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegisterForm {
    @NotBlank @Size(min = 3, max = 50) @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "letters, numbers and _ only")
    private String username;
    @NotBlank @Size(max = 100)
    private String displayName;
    @NotBlank @Email
    private String email;
    @NotBlank @Size(min = 6, max = 64)
    private String password;
    @NotNull
    private Role role = Role.CREATOR;

    // creator fields
    private Platform platform;
    @Size(max = 60)
    private String handle;
    @Min(0)
    private Integer followerCount;
    @Size(max = 60)
    private String niche;
    @DecimalMin("0")
    private BigDecimal baseRate;

    // brand fields
    @Size(max = 100)
    private String companyName;
    @Size(max = 60)
    private String industry;
}
