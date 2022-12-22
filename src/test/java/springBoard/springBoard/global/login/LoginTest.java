package springBoard.springBoard.global.login;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import springBoard.springBoard.domain.member.repository.MemberRepository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    EntityManager em;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    ObjectMapper objectMapper = new ObjectMapper();

    private static String KEY_USERNAME = "username";
}
