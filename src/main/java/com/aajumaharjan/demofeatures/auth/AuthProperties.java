package com.aajumaharjan.demofeatures.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "plugins.demo-features.auth")
public class AuthProperties {

    private Jwt jwt = new Jwt();
    @Value("${plugins.demo-features.auth.type}")
    private Type type = Type.STATELESS;
    private Password password = new Password();
    @Value("${plugins.demo-features.auth.public-routes:}")
    private List<String> publicRoutes = new ArrayList<>();

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
        private Token token = new Token();
        private Signing signing = new Signing();
        private Authorities authorities = new Authorities();
        private Header header = new Header();

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }

        public Signing getSigning() {
            return signing;
        }

        public void setSigning(Signing signing) {
            this.signing = signing;
        }

        public Authorities getAuthorities() {
            return authorities;
        }

        public void setAuthorities(Authorities authorities) {
            this.authorities = authorities;
        }

        public Header getHeader() {
            return header;
        }

        public void setHeader(Header header) {
            this.header = header;
        }

        // Convenience accessors so existing callers keep working
        public long getTokenValidity() {
            return token.getValidity();
        }

        public void setTokenValidity(long tokenValidity) {
            token.setValidity(tokenValidity);
        }

        public String getSigningKey() {
            return signing.getKey();
        }

        public void setSigningKey(String signingKey) {
            signing.setKey(signingKey);
        }

        public String getAuthoritiesKey() {
            return authorities.getKey();
        }

        public void setAuthoritiesKey(String authoritiesKey) {
            authorities.setKey(authoritiesKey);
        }

        public String getTokenPrefix() {
            return token.getPrefix();
        }

        public void setTokenPrefix(String tokenPrefix) {
            token.setPrefix(tokenPrefix);
        }

        public String getHeaderString() {
            return header.getString();
        }

        public void setHeaderString(String headerString) {
            header.setString(headerString);
        }

        public static class Token {
            private long validity;
            private String prefix;

            public long getValidity() {
                return validity;
            }

            public void setValidity(long validity) {
                this.validity = validity;
            }

            public String getPrefix() {
                return prefix;
            }

            public void setPrefix(String prefix) {
                this.prefix = prefix;
            }
        }

        public static class Signing {
            private String key;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }

        public static class Authorities {
            private String key;

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }
        }

        public static class Header {
            private String string;

            public String getString() {
                return string;
            }

            public void setString(String string) {
                this.string = string;
            }
        }
    }

    public static class Password {
        public enum Encoder {BCRYPT, PBKDF2}

        @Value("${plugins.demo-features.auth.password.encoder:BCRYPT}")
        private Encoder encoder;

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
