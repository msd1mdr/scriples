package app.bmri.scripless.backing.anggotabursa;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;

public class AnggotaBursaManagedBean {

    private RichTable abTable;

    public AnggotaBursaManagedBean() {
    }

    public void editPopupFetchListener(PopupFetchEvent popupFetchEvent) {
        if (popupFetchEvent.getLaunchSourceClientId().contains("ctbInsert")) {
            BindingContainer bindings = this.getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("CreateInsert");
            operationBinding.execute();
        }
    }

    public void editPopupCancelListener(PopupCanceledEvent popupCanceledEvent) {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("Rollback");
        operationBinding.execute();
    }

    public void editDialogListener(DialogEvent dialogEvent) {       

        DCBindingContainer bindingContainer =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding dciter =
            bindingContainer.findIteratorBinding("AnggotaBursaView1Iterator");
        Row anggotaBursaRow = dciter.getCurrentRow();
        Integer rowStatus = Integer.parseInt(anggotaBursaRow.getAttribute("RowStatus").toString());

        FacesContext fctx = FacesContext.getCurrentInstance();
        if (dialogEvent.getOutcome().name().equals("ok")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Commit");
            operationBinding.execute();
            if (rowStatus == 0) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "New broker has been added.",
                                     "New broker has been added.");
                fctx.addMessage(null, msg);
            } else {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Broker data has been updated.",
                                     "Broker data has been updated.");
                fctx.addMessage(null, msg);
            }
            
            AdfFacesContext.getCurrentInstance().addPartialTarget(abTable);
            
        } else if (dialogEvent.getOutcome().name().equals("cancel")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Rollback");
            operationBinding.execute();
            
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Adding new broker process has been canceled.",
                                 "Adding new broker process has been canceled.");
            fctx.addMessage(null, msg);
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
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Broker has been deleted.",
                                 "Broker has been deleted.");
            fctx.addMessage(null, msg);
            
        } else if (dialogEvent.getOutcome().name().equals("cancel")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Rollback");
            operationBinding.execute();
        }
    }

    public void setAbTable(RichTable abTable) {
        this.abTable = abTable;
    }

    public RichTable getAbTable() {
        return abTable;
    }
}
