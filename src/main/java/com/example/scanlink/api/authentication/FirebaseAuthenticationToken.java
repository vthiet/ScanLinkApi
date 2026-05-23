package com.example.scanlink.api.authentication;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;

public class FirebaseAuthenticationToken extends AbstractAuthenticationToken {

    private final FirebaseUserPrincipal principal;

    public FirebaseAuthenticationToken(FirebaseUserPrincipal principal) {
        super(AuthorityUtils.NO_AUTHORITIES);
        this.principal = principal;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }
}
