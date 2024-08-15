package org.roadmap.filedrive.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppUserDTO {

    @NotNull
    @Email
    String email;

    @NotNull
    @Size(min = 8, max = 32)
    String password;

}
