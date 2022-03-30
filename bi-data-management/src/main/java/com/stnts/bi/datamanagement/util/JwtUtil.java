package com.stnts.bi.datamanagement.util;

import com.stnts.bi.datamanagement.constant.EnvironmentProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 刘天元
 */
@Service
@Slf4j
public class JwtUtil {

    private final EnvironmentProperties environmentProperties;

    public JwtUtil(EnvironmentProperties environmentProperties) {
        this.environmentProperties = environmentProperties;
    }

    private static final String USER_INFO_KEY = "user_info";

    private static final long TOKEN_EXPIRED_SECOND = 60 * 30;

    private static final Integer DATA_SIZE_64 = 64;

    public String get(Map<String, String> userInfo) {

        Map<String, Object> claims = new HashMap<>(5);
        
        claims.put(USER_INFO_KEY, userInfo);

        // 添加自定义参数
        JwtBuilder jwtBuilder = Jwts.builder()
                .setClaims(claims);

        long currentTimeMillis = System.currentTimeMillis();
        // 设置过期时间
        jwtBuilder.setExpiration(new Date(currentTimeMillis + TOKEN_EXPIRED_SECOND * 1000));

        SecretKey secretKey = getSecretKey();
        jwtBuilder.signWith(secretKey);

        String jwsStr = jwtBuilder.compact();
        return jwsStr;
    }


    public Map<String, String> verify(String token) {
        SecretKey secretKey = getSecretKey();
        Jws<Claims> jws = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build().parseClaimsJws(token);

        Claims claims = jws.getBody();

        Map<String, String> userInfo = claims.get(USER_INFO_KEY, Map.class);

        return userInfo;
    }

    /**
     * SecretKey 根据 SECRET 的编码方式解码后得到：
     * Base64 编码：SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
     * Base64URL 编码：SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretString));
     * 未编码：SecretKey key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
     * */
    private SecretKey getSecretKey() {
        byte[] encodeKey = Decoders.BASE64.decode(handleLength(environmentProperties.getAppSecret()));
        return Keys.hmacShaKeyFor(encodeKey);
    }

    /**
     * 明文的长度需要时16位的倍数
     * @param str
     * @return
     */
    private String handleLength(String str) {
        if(str.length() < DATA_SIZE_64) {
            StringBuilder strBuilder = new StringBuilder(str);
            while (strBuilder.length() < DATA_SIZE_64) {
                strBuilder.append('0');
            }
            str = strBuilder.toString();
        }
        return str;
    }
}
