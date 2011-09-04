package play.modules.odata.auth;

import java.security.Principal;

public class GenericPrincipal implements Principal {

    private final String name;

    public GenericPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

}
