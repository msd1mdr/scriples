package app.bmri.scripless.backing.useraccessmenu;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.layout.RichPanelFormLayout;
import oracle.adf.view.rich.component.rich.nav.RichCommandToolbarButton;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import org.apache.myfaces.trinidad.event.ReturnEvent;

public class AdminUserAccessManagedBean {
    private RichTable usrAccTable;
    private RichInputText inpPassword;
    private final static String DEFAULT_PASSWORD = "investor1";
    private RichTable tblAssignedRole;
    private RichOutputText statusCounter;
    private RichCommandToolbarButton btnDelUser;
    private RichOutputText txtStatusCount;
    private RichPanelFormLayout panelFormUser;
    private RichInputText confirmPassword;

    public AdminUserAccessManagedBean() {
    }

    public void addPopupFetchListener(PopupFetchEvent popupFetchEvent) {
        if (popupFetchEvent.getLaunchSourceClientId().contains("ctbInsert")) {
            BindingContainer bindings = this.getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("CreateInsert");
            operationBinding.execute();
        }
    }

    public void addPopupCancelListener(PopupCanceledEvent popupCanceledEvent) {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("Rollback");
        operationBinding.execute();
    }

    public void addDialogListener(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        if (dialogEvent.getOutcome().name().equals("ok")) {
            inpPassword.setValue(DEFAULT_PASSWORD);
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Commit");
            operationBinding.execute();
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_INFO, "New user has been added. Please assign role.",
                                 "New user has been added. Please assign role.");
            fctx.addMessage(null, msg);

            AdfFacesContext.getCurrentInstance().addPartialTarget(usrAccTable);

        } else {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Rollback");
            operationBinding.execute();

            AdfFacesContext.getCurrentInstance().addPartialTarget(usrAccTable);
        }
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void deleteDialogListener(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        if (dialogEvent.getOutcome().name().equals("ok")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Delete");
            operationBinding.execute();
            OperationBinding operationBindingCommit =
                bindings.getOperationBinding("Commit");
            operationBindingCommit.execute();

            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_INFO, "User has been deleted.",
                                 "User has been deleted.");
            fctx.addMessage(null, msg);

        } else if (dialogEvent.getOutcome().name().equals("cancel")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Rollback");
            operationBinding.execute();
        }
    }

    public void setUsrAccTable(RichTable usrAccTable) {
        this.usrAccTable = usrAccTable;
    }

    public RichTable getUsrAccTable() {
        return usrAccTable;
    }

    public void setInpPassword(RichInputText inpPassword) {
        this.inpPassword = inpPassword;
    }

    public RichInputText getInpPassword() {
        return inpPassword;
    }

    public void resetUserPassword(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        if (dialogEvent.getOutcome().name().equals("ok")) {

            String newPass = (String)inpPassword.getValue();
            String confPass = (String)confirmPassword.getValue();
            if (newPass.equals(confPass)) {
                BindingContainer bindings = getBindings();
                OperationBinding operationBindingCommit =
                    bindings.getOperationBinding("Commit");
                operationBindingCommit.execute();

                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "User password has been changed.",
                                     "User password has been changed.");
                fctx.addMessage(null, msg);
            } else {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Confirmation password didnt match. Password failed to changed.",
                                     "Confirmation password didnt match. Password failed to changed.");
                fctx.addMessage(null, msg);
            }

        } else if (dialogEvent.getOutcome().name().equals("cancel")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Rollback");
            operationBinding.execute();
        }
    }

    public void returnListener(ReturnEvent returnEvent) {
        BindingContainer bindings = this.getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("Execute");
        operationBinding.execute();
        AdfFacesContext.getCurrentInstance().addPartialTarget(tblAssignedRole);
        AdfFacesContext.getCurrentInstance().addPartialTarget(btnDelUser);
        AdfFacesContext.getCurrentInstance().addPartialTarget(txtStatusCount);
    }

    public void setTblAssignedRole(RichTable tblAssignedRole) {
        this.tblAssignedRole = tblAssignedRole;
    }

    public RichTable getTblAssignedRole() {
        return tblAssignedRole;
    }

    public void setBtnDelUser(RichCommandToolbarButton btnDelUser) {
        this.btnDelUser = btnDelUser;
    }

    public RichCommandToolbarButton getBtnDelUser() {
        return btnDelUser;
    }

    public void setTxtStatusCount(RichOutputText txtStatusCount) {
        this.txtStatusCount = txtStatusCount;
    }

    public RichOutputText getTxtStatusCount() {
        return txtStatusCount;
    }

    public void newUserReturnListener(ReturnEvent returnEvent) {
        BindingContainer bindings = this.getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("ExecuteUserList");
        operationBinding.execute();
        AdfFacesContext.getCurrentInstance().addPartialTarget(usrAccTable);
    }

    public void editUserReturnListener(ReturnEvent returnEvent) {
        BindingContainer bindings = this.getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("ExecuteUserList");
        operationBinding.execute();
        AdfFacesContext.getCurrentInstance().addPartialTarget(usrAccTable);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelFormUser);
    }

    public void setPanelFormUser(RichPanelFormLayout panelFormUser) {
        this.panelFormUser = panelFormUser;
    }

    public RichPanelFormLayout getPanelFormUser() {
        return panelFormUser;
    }

    public void setConfirmPassword(RichInputText confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public RichInputText getConfirmPassword() {
        return confirmPassword;
    }
}
