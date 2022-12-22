package springBoard.springBoard.global.jwt.service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface JwtService {
    String createAccessToken(String username);
    String createRefreshToken();

    void updateRefreshToken(String username, String refreshToken);
    void destroyRefreshToken(String username);
    void sendToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException;
    String extractAccessToken(HttpServletRequest request) throws IOException, ServletException;
    String extractRefreshToken(HttpServletRequest request) throws IOException, ServletException;
    String extractUsername(String accrssToken);

    void setAccessTokenHeader(HttpServletResponse response, String accessToken);
    void SetRefreshTokenHeader(HttpServletResponse response, String refreshToken);
}
