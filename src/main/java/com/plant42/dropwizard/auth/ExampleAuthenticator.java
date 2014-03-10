package com.plant42.dropwizard.auth;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;


public class ExampleAuthenticator implements Authenticator<String, User> {

    @Override
    public Optional<User> authenticate(String s) throws AuthenticationException {
        if ("secret".equals(s)) {
            return Optional.of(new User(s));
        }

        return Optional.absent();
    }
}
