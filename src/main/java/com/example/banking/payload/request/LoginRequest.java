package com.example.banking.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Data
public class LoginRequest {

    @NotBlank(message = "username must not be blank.")
    private String username;

    @NotBlank(message = "Password must not be blank.")
    private String password;

    public @NotBlank(message = "username must not be blank.") String getUsername() {
        return username;
    }

    public @NotBlank(message = "Password must not be blank.") String getPassword() {
        return password;
    }

    public void setUsername(@NotBlank(message = "username must not be blank.") String username) {
        this.username = username;
    }

    public void setPassword(@NotBlank(message = "Password must not be blank.") String password) {
        this.password = password;
    }
}
