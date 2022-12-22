package springBoard.springBoard.global.jwt.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
@Setter(value = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService {

    /*
    * 1
    * */
    @Value("${jwt.secret")
    private String secret;
    @Value("${jwt.access.expiration}")
    private long accessTokenValidityInSeconds;

}

