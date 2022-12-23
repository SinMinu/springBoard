package springBoard.springBoard.domain.member;

import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import springBoard.springBoard.domain.BaseTimeEntity;

import javax.persistence.*;

@Table(name = "MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, length = 30, unique = true)
    private String username;
    private String password;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(nullable = false, length = 30)
    private String nickName;

    @Column(nullable = false, length = 30)
    private Integer age;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(length = 1000)
    private String refreshToken;//RefreshToken

    public void updateRefreshToken(String refreshToken){
        this.refreshToken = refreshToken;
    }

    public void destroyRefreshToken(){
        this.refreshToken = null;
    }

    /*
    * 정보수정
    * */
    public void updatePassword(PasswordEncoder passwordEncoder, String password){
        this.password = passwordEncoder.encode(password);
    }
    public void updateName(String name){
        this.name = name;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updateAge(int age){
        this.age = age;
    }

    /*
    * 패스워드 암호화
    * */
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }
}
