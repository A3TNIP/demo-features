package com.aajumaharjan.demofeatures.auth;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "plugins.demo-features.auth")
public class AuthProperties {

    private Jwt jwt = new Jwt();
    private Type type = Type.STATELESS;
    private Password password = new Password();
    private List<String> publicRoutes = new ArrayList<>(List.of("/auth/**"));

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setType(String type) {
        this.type = parseEnum("auth.type", type, Type.class);
    }

    public Password getPassword() {
        return password;
    }

    public void setPassword(Password password) {
        this.password = password;
    }

    public List<String> getPublicRoutes() {
        return publicRoutes;
    }

    public void setPublicRoutes(List<String> publicRoutes) {
        this.publicRoutes = publicRoutes;
    }

    public static class Jwt {
        private long tokenValidity = 18000;
        private String signingKey = "lecSQFIERRoVz/AOV1LtEEp3nAvy65naUOYX//rbdHeB2BE/7uiGplCLgDhS3aJTb5vPQV8TtIHrbJqcSEraEw==";
        private String authoritiesKey = "roles";
        private String tokenPrefix = "Bearer";
        private String headerString = "Authorization";

        public long getTokenValidity() {
            return tokenValidity;
        }

        public void setTokenValidity(long tokenValidity) {
            this.tokenValidity = tokenValidity;
        }

        public String getSigningKey() {
            return signingKey;
        }

        public void setSigningKey(String signingKey) {
            this.signingKey = signingKey;
        }

        public String getAuthoritiesKey() {
            return authoritiesKey;
        }

        public void setAuthoritiesKey(String authoritiesKey) {
            this.authoritiesKey = authoritiesKey;
        }

        public String getTokenPrefix() {
            return tokenPrefix;
        }

        public void setTokenPrefix(String tokenPrefix) {
            this.tokenPrefix = tokenPrefix;
        }

        public String getHeaderString() {
            return headerString;
        }

        public void setHeaderString(String headerString) {
            this.headerString = headerString;
        }
    }

    public static class Password {
        public enum Encoder {BCRYPT, PBKDF2}

        private Encoder encoder = Encoder.BCRYPT;

        public Encoder getEncoder() {
            return encoder;
        }

        public void setEncoder(Encoder encoder) {
            this.encoder = encoder;
        }

        public void setEncoder(String encoder) {
            this.encoder = parseEnum("auth.password.encoder", encoder, Encoder.class);
        }
    }

    public enum Type {STATELESS, STATEFUL}

    private static <E extends Enum<E>> E parseEnum(String key, String value, Class<E> enumClass) {
        try {
            return Enum.valueOf(enumClass, value.toUpperCase());
        } catch (IllegalArgumentException ex) {
            String allowed = String.join(",", Arrays.stream(enumClass.getEnumConstants()).map(Enum::name).toList());
            throw new IllegalArgumentException("Invalid value for " + key + ": " + value + ". Allowed: " + allowed);
        }
    }
}
