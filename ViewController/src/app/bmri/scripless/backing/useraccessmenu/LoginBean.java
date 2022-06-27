package app.bmri.scripless.backing.useraccessmenu;

import app.bmri.scripless.adfextensions.ADFUtils;

import app.bmri.scripless.adfextensions.JSFUtils;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import oracle.adf.view.rich.component.rich.input.RichInputText;

import oracle.binding.OperationBinding;

import weblogic.security.URLCallbackHandler;

import weblogic.servlet.security.ServletAuthentication;

public class LoginBean {
    private RichInputText inputLogin, inputPassword;
    //private String loginAction;
    private final static String APPUSERNAME = "scripless";
    private final static String APPPASSWORD = "welcome1";
    private final static String INVAPPUSERNAME = "invalid";
    private final static String INVAPPPASSWORD = "invalid1";
    
    public LoginBean() {
        super();
    }

    public void setInputLogin(RichInputText inputLogin) {
        this.inputLogin = inputLogin;
    }

    public RichInputText getInputLogin() {
        return inputLogin;
    }

    public void setInputPassword(RichInputText inputPassword) {
        this.inputPassword = inputPassword;
    }

    public RichInputText getInputPassword() {
        return inputPassword;
    }

    public String loginAction() throws LoginException {
        String action = "";
        String un = "";
        byte[] pw = ("").getBytes();
        FacesContext ctx = FacesContext.getCurrentInstance();
        HttpServletRequest request =
            (HttpServletRequest)ctx.getExternalContext().getRequest();
        try {
            OperationBinding login =
                ADFUtils.findOperation("authenticateUser");
            Map m = (Map)login.execute();

            String fullName = (String)m.get("FullName");
            HashMap userAccess = (HashMap)m.get("UserAccess");
            String userPassword = (String)m.get("Password");
            String userNameLogin = (String)m.get("UserName");
            String title = (String)m.get("Title");

            if (!userNameLogin.equalsIgnoreCase("INVALID")) {
                un = APPUSERNAME;
                pw = (APPPASSWORD).getBytes();
            } else {
                un = INVAPPUSERNAME;
                pw = (INVAPPPASSWORD).getBytes();
            }

            CallbackHandler handler = new URLCallbackHandler(un, pw);
            Subject mySubject =
                weblogic.security.services.Authentication.login(handler);
            weblogic.servlet.security.ServletAuthentication.runAs(mySubject,
                                                                  request);
            ServletAuthentication.generateNewSessionID(request);

            inputLogin.resetValue();
            inputPassword.resetValue();

            UserData userData =
                (UserData)JSFUtils.resolveExpression("#{UserData}");
            userData.setLoggedIn(Boolean.TRUE);
            userData.setFullName(fullName);
            userData.setUserAccess(userAccess);
            userData.setUserNameLogin(userNameLogin);
            userData.setUserPassword(userPassword);
            userData.setTitle(title);

            OperationBinding _adminModuleSession =
                ADFUtils.findOperation("setLoginToSession_AdminModule");
            _adminModuleSession.execute();
            
            OperationBinding _brokerModuleSession =
                ADFUtils.findOperation("setLoginToSession_BrokerModule");
            _brokerModuleSession.execute();

            OperationBinding _investorModuleSession =
                ADFUtils.findOperation("setLoginToSession_InvestorModule");
            _investorModuleSession.execute();

            OperationBinding _kseiModuleSession =
                ADFUtils.findOperation("setLoginToSession_KSEIModule");
            _kseiModuleSession.execute();

            String loginUrl =
                "/adfAuthentication?success_url=/faces/scriplessDash.jspx";
            HttpServletResponse response =
                (HttpServletResponse)ctx.getExternalContext().getResponse();
            sendForward(request, response, loginUrl);
            action = "home";
        } catch (FailedLoginException fle) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Incorrect Username or Password",
                                 "An incorrect Username or Password" +
                                 " was specified");
            ctx.addMessage(null, msg);
//            fle.getLocalizedMessage();
        } catch (LoginException le) {
            reportUnexpectedLoginError("LoginException", le);
        } catch (Exception e) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Incorrect Username or Password",
                                 "An incorrect Username or Password" +
                                 " was specified");
            ctx.addMessage(null, msg);
        }
        return action;
    }

    private void sendForward(HttpServletRequest request,
                             HttpServletResponse response, String forwardUrl) {
        FacesContext ctx = FacesContext.getCurrentInstance();
        RequestDispatcher dispatcher =
            request.getRequestDispatcher(forwardUrl);
        try {
            dispatcher.forward(request, response);
        } catch (ServletException se) {
            reportUnexpectedLoginError("ServletException", se);
        } catch (IOException ie) {
            reportUnexpectedLoginError("IOException", ie);
        }
        ctx.responseComplete();
    }

    private void reportUnexpectedLoginError(String errType, Exception e) {
        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unexpected error during login",
                             "Unexpected error during login (" + errType +
                             "), please consult logs for detail");
        FacesContext.getCurrentInstance().addMessage("d2:it35", msg);
        e.printStackTrace();
    }

    public String doLogout() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ExternalContext ectx = fctx.getExternalContext();
        String url =
            ectx.getRequestContextPath() + "/adfAuthentication?logout=true&end_url=/faces/scriplessDash.jspx";
        try {
            ectx.redirect(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        fctx.responseComplete();
        return null;
    }
}
