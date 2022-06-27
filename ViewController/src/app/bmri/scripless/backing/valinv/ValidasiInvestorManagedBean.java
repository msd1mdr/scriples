package app.bmri.scripless.backing.valinv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.output.RichOutputText;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;

public class ValidasiInvestorManagedBean {
    private RichTable tblInvestor;
    private RichInputText participantId;
    private RichInputText sidNumber;
    private RichInputText accNumber;
    private RichOutputText estRowStatus;

    public ValidasiInvestorManagedBean() {
    }

    public void editPopupFetchListener(PopupFetchEvent popupFetchEvent) {
        /* ORIGINAL ADF (1 of 3) */
        /*
        if (popupFetchEvent.getLaunchSourceClientId().contains("ctbInsert")) {
            BindingContainer bindings = this.getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("CreateInsert");
            operationBinding.execute();
        }
        */
    }

    public void editPopupCancelListener(PopupCanceledEvent popupCanceledEvent) {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding =
            bindings.getOperationBinding("Rollback");
        operationBinding.execute();
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }

    public void editDialogListener(DialogEvent dialogEvent) {

        FacesContext fctx = FacesContext.getCurrentInstance();

        String partId = participantId.getValue().toString();
        String sidNum = sidNumber.getValue().toString();
        String accountNumber = accNumber.getValue().toString();

        /*
        System.out.println("-----------------------------");
        System.out.println("PART ID: " + partId);
        System.out.println("SID NUM: " + sidNum);
        System.out.println("ACC NUM: " + accountNumber);
        System.out.println("-----------------------------");
        */

        if (dialogEvent.getOutcome().name().equals("ok")) {
            boolean submitValStatus =
                submitValidationMsg(partId, sidNum, accountNumber);
            if (submitValStatus) {
                /* ORIGINAL ADF (2 of 3) */
                /*
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Commit");
            operationBinding.execute();
            */

                BindingContainer bindings = getBindings();
                OperationBinding operationBinding =
                    bindings.getOperationBinding("Execute1");
                operationBinding.execute();

                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "New investor has been added.",
                                     "New investor has been added.");
                fctx.addMessage(null, msg);

                //AdfFacesContext.getCurrentInstance().addPartialTarget(tblInvestor);
            } else {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "Failed to add new investor.",
                                     "Failed to add new investor.");
                fctx.addMessage(null, msg);
            }

        } else if (dialogEvent.getOutcome().name().equals("cancel")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Rollback");
            operationBinding.execute();

            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Adding new investor has been canceled.",
                                 "Adding new investor has been canceled.");
            fctx.addMessage(null, msg);
        }
    }

    public void setTblInvestor(RichTable tblInvestor) {
        this.tblInvestor = tblInvestor;
    }

    public RichTable getTblInvestor() {
        return tblInvestor;
    }

    public boolean submitValidationMsg(String partId, String sidNum,
                                       String accNum) {
        boolean statusInject = false;
        Connection conn = null;
        FacesContext fctx = FacesContext.getCurrentInstance();
        // Start Validation Message
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            PreparedStatement statement =
                conn.prepareStatement("BEGIN SI_INVDISPATCH.SUBMIT_VALIDATION (?, ?, ?); END;");
            statement.setString(1, partId);
            statement.setString(2, sidNum);
            statement.setString(3, accNum);
            statement.executeUpdate();
            statusInject = true;
        } catch (SQLException e) {
            statusInject = false;
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQL Exception:\n " +
                                 e, "SQL Exception:\n" +
                    e);
            fctx.addMessage(null, msg);
        } catch (Exception e) {
            statusInject = false;
            System.out.println(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                statusInject = false;
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "Unhandled error while connect into database.",
                                     "Unhandled error while connect into database.");
                fctx.addMessage(null, msg);
            }
        }
        // End Validation Message

        return statusInject;
    }

    public void setParticipantId(RichInputText participantId) {
        this.participantId = participantId;
    }

    public RichInputText getParticipantId() {
        return participantId;
    }

    public void setSidNumber(RichInputText sidNumber) {
        this.sidNumber = sidNumber;
    }

    public RichInputText getSidNumber() {
        return sidNumber;
    }

    public void setAccNumber(RichInputText accNumber) {
        this.accNumber = accNumber;
    }

    public RichInputText getAccNumber() {
        return accNumber;
    }

    public void setEstRowStatus(RichOutputText estRowStatus) {
        this.estRowStatus = estRowStatus;
    }

    public RichOutputText getEstRowStatus() {
        return estRowStatus;
    }

    public void uploadDialogListener(DialogEvent dialogEvent) {
        if (dialogEvent.getOutcome().name().equals("ok")) {
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Execute1");
            operationBinding.execute();
            
            AdfFacesContext.getCurrentInstance().addPartialTarget(tblInvestor);            
            AdfFacesContext.getCurrentInstance().addPartialTarget(estRowStatus);
        }
    }
}
