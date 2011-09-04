package play.modules.odata.auth;

import java.security.Principal;

import play.mvc.Http.Request;
import play.mvc.Http.Response;

public interface Authenticator {
    Principal authenticate(Request request, Response response);

    boolean needAuthentication(Request request);
}
