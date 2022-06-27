package app.bmri.scripless.backing.useraccessmenu;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.adf.view.rich.event.PopupCanceledEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;

public class AdminMenuManagedBean {
    private RichTable tblMenu;

    public AdminMenuManagedBean() {
    }

    public void editPopupCancelListener(PopupCanceledEvent popupCanceledEvent) {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("Rollback");
        operationBinding.execute();
    }

    public void editDialogListener(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        if (dialogEvent.getOutcome().name().equals("ok")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Commit");
            operationBinding.execute();
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Menu has been updated.",
                                 "Menu has been updated.");
            fctx.addMessage(null, msg);

            AdfFacesContext.getCurrentInstance().addPartialTarget(tblMenu);

        } 
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void setTblMenu(RichTable tblMenu) {
        this.tblMenu = tblMenu;
    }

    public RichTable getTblMenu() {
        return tblMenu;
    }
}
