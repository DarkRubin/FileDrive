package org.roadmap.filedrive.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotEmpty
    @Email
    String email;

    @NotNull
    @NotEmpty
    String password;

}
