package app.bmri.scripless.backing.useraccessmenu;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.nav.RichCommandToolbarButton;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import org.apache.myfaces.trinidad.event.ReturnEvent;

public class AdminUserRoleBackingBean {
    private RichTable tblMenuList;
    private RichOutputText txtStatusCount;
    private RichTable tblRoleList;
    private RichCommandToolbarButton btnDeleteRole;

    public AdminUserRoleBackingBean() {
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void returnListener(ReturnEvent returnEvent) {
        BindingContainer bindings = this.getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("Execute");
        operationBinding.execute();
        AdfFacesContext.getCurrentInstance().addPartialTarget(tblMenuList);
        AdfFacesContext.getCurrentInstance().addPartialTarget(txtStatusCount);
        AdfFacesContext.getCurrentInstance().addPartialTarget(btnDeleteRole);
    }

    public void setTblMenuList(RichTable tblMenuList) {
        this.tblMenuList = tblMenuList;
    }

    public RichTable getTblMenuList() {
        return tblMenuList;
    }

    public void setTxtStatusCount(RichOutputText txtStatusCount) {
        this.txtStatusCount = txtStatusCount;
    }

    public RichOutputText getTxtStatusCount() {
        return txtStatusCount;
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
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Commit");
            operationBinding.execute();
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_INFO, "New role has been created. Please assign menu.",
                                 "New role has been created. Please assign menu.");
            fctx.addMessage(null, msg);

            AdfFacesContext.getCurrentInstance().addPartialTarget(tblRoleList);

        } else {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Rollback");
            operationBinding.execute();

            AdfFacesContext.getCurrentInstance().addPartialTarget(tblRoleList);
        }
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
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Role has been deleted.",
                                 "Role has been deleted.");
            fctx.addMessage(null, msg);

        } else if (dialogEvent.getOutcome().name().equals("cancel")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Rollback");
            operationBinding.execute();
        }
    }

    public void setTblRoleList(RichTable tblRoleList) {
        this.tblRoleList = tblRoleList;
    }

    public RichTable getTblRoleList() {
        return tblRoleList;
    }

    public void setBtnDeleteRole(RichCommandToolbarButton btnDeleteRole) {
        this.btnDeleteRole = btnDeleteRole;
    }

    public RichCommandToolbarButton getBtnDeleteRole() {
        return btnDeleteRole;
    }
}
