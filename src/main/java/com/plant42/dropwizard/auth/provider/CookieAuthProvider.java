package com.plant42.dropwizard.auth.provider;

import com.google.common.base.Optional;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.yammer.dropwizard.auth.Auth;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A Jersey provider for HTTP Cookie authentication
 *
 * @param <T> the principle type
 */
public class CookieAuthProvider<T> implements InjectableProvider<Auth, Parameter> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieAuthProvider.class);
    private final Authenticator<String, T> authenticator;
    private final String cookie;


    /**
     * Creates a new CookieAuthProvider with the given {@link Authenticator} and cookie name
     *
     *
     * @param authenticator  the authenticator that will take the {@link String} and
     *                       convert them to instances of {@code T}
     *
     * @param cookie the name of the cookie
     */
    public CookieAuthProvider(Authenticator<String, T> authenticator, String cookie) {
        this.authenticator = authenticator;
        this.cookie = cookie;
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<?> getInjectable(ComponentContext componentContext, Auth auth, Parameter parameter) {
        return new CookieAuthInjectable<T>(authenticator, cookie, auth.required());
    }



    private static class CookieAuthInjectable<T> extends AbstractHttpContextInjectable<T> {

        private final Authenticator<String, T> authenticator;
        private final String cookie;
        private final boolean required;

        private CookieAuthInjectable(Authenticator<String, T> authenticator, String cookie, boolean required) {
            this.authenticator = authenticator;
            this.cookie = cookie;
            this.required = required;
        }

        @Override
        public T getValue(HttpContext httpContext) {
            final Cookie cookie = httpContext.getRequest().getCookies().get(this.cookie);

            if (cookie != null) {
                final String value = cookie.getValue();
                try {
                    final Optional<T> result = authenticator.authenticate(value);
                    if (result.isPresent()) {
                        return result.get();
                    }
                } catch (AuthenticationException e) {
                    e.printStackTrace();
                }
            }

            if (required) {
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity("Credentials are required to access this resource")
                                .type(MediaType.TEXT_PLAIN_TYPE)
                                .build());
            }

            return null;
        }
    }


}
