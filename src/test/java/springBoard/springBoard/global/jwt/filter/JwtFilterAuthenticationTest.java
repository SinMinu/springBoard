package springBoard.springBoard.global.jwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import springBoard.springBoard.domain.member.Member;
import springBoard.springBoard.domain.member.Role;
import springBoard.springBoard.domain.member.repository.MemberRepository;
import springBoard.springBoard.global.jwt.service.JwtService;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JwtFilterAuthenticationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Autowired
    JwtService jwtService;

    PasswordEncoder delegatingPasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static String KEY_USERNAME = "username";
    private static String KEY_PASSWORD = "password";
    private static String USERNAME = "username";
    private static String PASSWORD = "123456789";

    private static String LOGIN_RUL = "/login";


    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String BEARER = "Bearer ";


    private ObjectMapper objectMapper = new ObjectMapper();



    private void clear(){
        em.flush();
        em.clear();
    }


    @BeforeEach
    private void init(){
        memberRepository.save(Member.builder().username(USERNAME).password(delegatingPasswordEncoder.encode(PASSWORD)).name("Member1").nickName("NickName1").role(Role.USER).age(22).build());
        clear();
    }



    private Map getUsernamePasswordMap(String username, String password){
        Map<String, String> map = new HashMap<>();
        map.put(KEY_USERNAME, username);
        map.put(KEY_PASSWORD, password);
        return map;
    }


    private Map getAccessAndRefreshToken() throws Exception {

        Map<String, String> map = getUsernamePasswordMap(USERNAME, PASSWORD);

        MvcResult result = mockMvc.perform(
                        post(LOGIN_RUL)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(map)))
                .andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);
        String refreshToken = result.getResponse().getHeader(refreshHeader);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put(accessHeader,accessToken);
        tokenMap.put(refreshHeader,refreshToken);

        return tokenMap;
    }






    /**
     * AccessToken : ???????????? ??????,
     * RefreshToken : ???????????? ??????
     */
    @Test
    public void Access_Refresh_??????_??????_X() throws Exception {
        //when, then
        mockMvc.perform(get(LOGIN_RUL+"123"))//login??? ?????? ?????? ????????? ??????
                .andExpect(status().isForbidden());
    }



    /**
     * AccessToken : ??????,
     * RefreshToken : ???????????? ??????
     */
    @Test
    public void AccessToken???_?????????_??????() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);

        //when, then
        mockMvc.perform(get(LOGIN_RUL+"123").header(accessHeader,BEARER+ accessToken))//login??? ?????? ?????? ????????? ??????
                .andExpectAll(status().isNotFound());//?????? ????????? ??????????????? NotFound

    }


    /**
     * AccessToken : ???????????? ??????,
     * RefreshToken : ???????????? ??????
     */
    @Test
    public void ????????????AccessToken???_?????????_??????X_???????????????_403() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);

        //when, then
        mockMvc.perform(get(LOGIN_RUL+"123").header(accessHeader,accessToken+"1"))//login??? ?????? ?????? ????????? ??????
                .andExpectAll(status().isForbidden());//?????? ????????? ??????????????? NotFound
    }
    /**
     * AccessToken : ???????????? ??????
     * RefreshToken : ??????
     */
    @Test
    public void ?????????RefreshToken???_?????????_AccessToken_?????????_200() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL + "123").header(refreshHeader, BEARER+refreshToken))//login??? ?????? ?????? ????????? ??????
                .andExpect(status().isOk()).andReturn();

        String accessToken = result.getResponse().getHeader(accessHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(accessToken).getSubject();
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }
    /**
     * AccessToken : ???????????? ??????
     * RefreshToken : ???????????? ??????
     */
    @Test
    public void ????????????RefreshToken???_?????????_403() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        mockMvc.perform(get(LOGIN_RUL + "123").header(refreshHeader, refreshToken))//Bearer??? ????????? ??????
                .andExpect(status().isForbidden());

        mockMvc.perform(get(LOGIN_RUL + "123").header(refreshHeader, BEARER+refreshToken+"1"))//???????????? ?????? ??????
                .andExpect(status().isForbidden());
    }
    /**
     * AccessToken : ??????
     * RefreshToken : ??????
     */
    @Test
    public void ?????????RefreshToken??????_?????????AccessToken_??????????????????_AccessToken_?????????_200() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL + "123")
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull();//refreshToken??? ??????????????? ??????
    }
    /**
     * AccessToken : ???????????? ??????
     * RefreshToken : ??????
     */
    @Test
    public void ?????????RefreshToken??????_????????????AccessToken_??????????????????_AccessToken_?????????_200() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL + "123")
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken + 1))
                .andExpect(status().isOk())
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);

        String subject = JWT.require(Algorithm.HMAC512(secret)).build().verify(responseAccessToken).getSubject();

        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
        assertThat(responseRefreshToken).isNull();//refreshToken??? ??????????????? ??????
    }
    /**
     * AccessToken : ??????
     * RefreshToken : ???????????? ??????
     */
    @Test
    public void ????????????RefreshToken??????_?????????AccessToken_??????????????????_????????????200_??????404_RefreshToken???_AccessToken??????_?????????????????????() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL + "123")
                        .header(refreshHeader, BEARER + refreshToken+1)
                        .header(accessHeader, BEARER + accessToken ))
                .andExpect(status().isNotFound())//?????? ????????? ??????????????? NotFound
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);


        assertThat(responseAccessToken).isNull();//accessToken??? ??????????????? ??????
        assertThat(responseRefreshToken).isNull();//refreshToken??? ??????????????? ??????
    }
    /**
     * AccessToken : ???????????? ??????
     * RefreshToken : ???????????? ??????
     */
    @Test
    public void ????????????RefreshToken??????_????????????AccessToken_??????????????????_403() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(get(LOGIN_RUL + "123")
                        .header(refreshHeader, BEARER + refreshToken+1)
                        .header(accessHeader, BEARER + accessToken+1 ))
                .andExpect(status().isForbidden())//?????? ????????? ??????????????? NotFound
                .andReturn();

        String responseAccessToken = result.getResponse().getHeader(accessHeader);
        String responseRefreshToken = result.getResponse().getHeader(refreshHeader);


        assertThat(responseAccessToken).isNull();//accessToken??? ??????????????? ??????
        assertThat(responseRefreshToken).isNull();//refreshToken??? ??????????????? ??????

    }
    @Test
    public void ?????????_?????????_?????????_????????????_X() throws Exception {
        //given
        Map accessAndRefreshToken = getAccessAndRefreshToken();
        String accessToken= (String) accessAndRefreshToken.get(accessHeader);
        String refreshToken= (String) accessAndRefreshToken.get(refreshHeader);

        //when, then
        MvcResult result = mockMvc.perform(post(LOGIN_RUL)  //get??? ?????? config?????? permitAll??? ????????? notFound
                        .header(refreshHeader, BEARER + refreshToken)
                        .header(accessHeader, BEARER + accessToken))
                .andExpect(status().isOk())
                .andReturn();

    }
}