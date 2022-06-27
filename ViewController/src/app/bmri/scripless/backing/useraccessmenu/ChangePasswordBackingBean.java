package app.bmri.scripless.backing.useraccessmenu;

import app.bmri.scripless.adfextensions.ADFUtils;
import app.bmri.scripless.adfextensions.JSFUtils;

import app.bmri.scripless.model.am.AdminModuleImpl;
import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.input.RichInputText;

import org.apache.commons.codec.digest.DigestUtils;

public class ChangePasswordBackingBean {
    private RichInputText newPassword;
    private RichInputText confirmPassword;
    private RichInputText oldPassword;
    private RichInputText userLoginName;

    public ChangePasswordBackingBean() {
        super();
    }

    public void setNewPassword(RichInputText newPassword) {
        this.newPassword = newPassword;
    }

    public RichInputText getNewPassword() {
        return newPassword;
    }

    public void setConfirmPassword(RichInputText confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public RichInputText getConfirmPassword() {
        return confirmPassword;
    }

    public void setOldPassword(RichInputText oldPassword) {
        this.oldPassword = oldPassword;
    }

    public RichInputText getOldPassword() {
        return oldPassword;
    }

    public void setUserLoginName(RichInputText userLoginName) {
        this.userLoginName = userLoginName;
    }

    public RichInputText getUserLoginName() {
        return userLoginName;
    }

    public String changePassword() {
        FacesContext fctx = FacesContext.getCurrentInstance();
        String action = null;
        String newPass = (String)newPassword.getValue();
        String oldPass = (String)oldPassword.getValue();
        String confPass = (String)confirmPassword.getValue();

        UserData userData =
            (UserData)JSFUtils.resolveExpression("#{UserData}");
        String currentUserName = userData.getUserNameLogin();
        String currentPassword = userData.getUserPassword();

        String encPassword = currentPassword;
        String encOldPassword = oldPass;
        
        if (encPassword.equals(encOldPassword)) {
            if (newPass.equals(confPass)) {
                AdminModuleImpl am =
                    (AdminModuleImpl)ADFUtils.getApplicationModuleForDataControl("AdminModuleDataControl");
                am.changePassword(currentUserName, newPass);
                //System.out.println("NEW PASWD: " + DigestUtils.sha1Hex(newPass));
                
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Your password has been changed.",
                                     "Your password has been changed.");
                fctx.addMessage(null, msg);
                
                action = "close";
            } else {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Confirmation password didnt match.",
                                     "Confirmation password didnt match.");
                fctx.addMessage(null, msg);
                action = null;
            }
        } else {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Your old password seems to be wrong.",
                                 "Your old password seems to be wrong.");
            fctx.addMessage(null, msg);
            action = null;
        }

        return action;
    }
}
