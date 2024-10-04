package ua.edu.ukma.cleaning;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
class LoginResponse {
    private String accessToken;
    private String refreshToken;
}
