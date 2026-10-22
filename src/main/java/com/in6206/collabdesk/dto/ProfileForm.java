package com.in6206.collabdesk.dto;

import com.in6206.collabdesk.entity.CreatorProfile;
import com.in6206.collabdesk.entity.Platform;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProfileForm {
    @NotBlank @Size(max = 100)
    private String displayName;
    @NotNull
    private Platform platform;
    @NotBlank @Size(max = 60)
    private String handle;
    @NotNull @Min(0)
    private Integer followerCount;
    @Size(max = 60)
    private String niche;
    @NotNull @DecimalMin("0")
    private BigDecimal baseRate;
    @Size(max = 500)
    private String bio;

    public static ProfileForm from(CreatorProfile c) {
        ProfileForm f = new ProfileForm();
        f.setDisplayName(c.getUser().getDisplayName());
        f.setPlatform(c.getPlatform());
        f.setHandle(c.getHandle());
        f.setFollowerCount(c.getFollowerCount());
        f.setNiche(c.getNiche());
        f.setBaseRate(c.getBaseRate());
        f.setBio(c.getBio());
        return f;
    }
}
