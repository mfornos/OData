package controllers;

import play.Logger;
import play.modules.odata.ODataPlugin;
import play.modules.odata.auth.ODataSecurity;
import play.mvc.Controller;

public class ODataController extends Controller {
    public static void serve() {
        Logger.debug("ODataController invoked: %s", request.url);

        if (ODataSecurity.hasAuth() && ODataSecurity.needAuthentication(request)) {
            if (ODataSecurity.authenticate(request, response) != null) {
                ODataPlugin.container.handle(request, response);
            }
        } else {
            ODataPlugin.container.handle(request, response);
        }
    }
}
