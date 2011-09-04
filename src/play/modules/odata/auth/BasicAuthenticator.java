package play.modules.odata.auth;

import java.security.Principal;

import play.mvc.Http.Header;
import play.mvc.Http.Request;
import play.mvc.Http.Response;

public abstract class BasicAuthenticator implements Authenticator {

    @Override
    public Principal authenticate(Request request, Response response) {
        Header h = request.headers.get("authorization");
        Principal principal = null;

        if (h != null) {
            principal = login(request.user, request.password);
        }

        if (principal == null) {
            response.status = 401;
            response.setHeader("Content-Length", "0");
            response.setHeader("WWW-Authenticate", "Basic");
        }

        return principal;
    }

    @Override
    public boolean needAuthentication(Request request) {
        return (!("GET".equals(request.method) || "HEAD".equals(request.method)));
    }

    protected abstract Principal login(String user, String password);
}
