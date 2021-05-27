package advisor.access;

import java.time.Instant;

public class AuthCredentials {
    private final String accessToken;
    private final String refreshToken;
    private final Instant authTime;
    private final long expiresIn;

    public AuthCredentials(String accessToken, String refreshToken, Instant authTime, long expiresIn) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.authTime = authTime;
        this.expiresIn = expiresIn;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Instant getAuthTime() {
        return authTime;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}
