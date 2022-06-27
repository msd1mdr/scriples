package app.bmri.scripless.backing.useraccessmenu;

import java.util.HashMap;

public class UserData {
    
    private HashMap userAccess;
    private Boolean loggedIn = Boolean.FALSE;    
    private String fullName;
    private String userPassword;
    private String userNameLogin;
    private String title;
    
    public UserData() {
    }

    public void setUserAccess(HashMap userAccess) {
        this.userAccess = userAccess;
    }

    public HashMap getUserAccess() {
        return userAccess;
    }

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserNameLogin(String userNameLogin) {
        this.userNameLogin = userNameLogin;
    }

    public String getUserNameLogin() {
        return userNameLogin;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
