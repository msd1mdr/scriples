package app.bmri.scripless.model.am.common;

import java.util.Map;

import oracle.jbo.ApplicationModule;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Mon May 16 12:41:25 ICT 2016
// ---------------------------------------------------------------------
public interface SecurityModule extends ApplicationModule {
    Map authenticateUser(String userLogin, String userPassword);
}
