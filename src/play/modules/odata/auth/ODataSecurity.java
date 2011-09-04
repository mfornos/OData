package play.modules.odata.auth;

import java.security.Principal;

import play.mvc.Http.Request;
import play.mvc.Http.Response;

public class ODataSecurity {
    public static Authenticator authenticator;

    public static Principal authenticate(Request request, Response response) {
        return authenticator.authenticate(request, response);
    }

    public static boolean hasAuth() {
        return authenticator != null;
    }

    public static boolean needAuthentication(Request request) {
        return authenticator.needAuthentication(request);
    }
}
