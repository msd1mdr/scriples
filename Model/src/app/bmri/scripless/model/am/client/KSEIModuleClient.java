package app.bmri.scripless.model.am.client;

import app.bmri.scripless.model.am.common.KSEIModule;

import oracle.jbo.client.remote.ApplicationModuleImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Sun Aug 09 02:13:34 ICT 2020
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class KSEIModuleClient extends ApplicationModuleImpl implements KSEIModule {
    /**
     * This is the default constructor (do not remove).
     */
    public KSEIModuleClient() {
    }

    public void setLoginToSession_KSEIModule(String key, String val) {
        Object _ret =
            this.riInvokeExportedMethod(this,"setLoginToSession_KSEIModule",new String [] {"java.lang.String","java.lang.String"},new Object[] {key, val});
        return;
    }
}
