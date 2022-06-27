package app.bmri.scripless.backing.statuskirim;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class StatusPengirimanManagedBean {
    private static final String ITERATOR_NAME = "Filestmt2sentView1Iterator";
    private RichSelectBooleanCheckbox checkAllRows;
    private RichSelectBooleanCheckbox checkRow;
    private RichTable tblStatusKirim;

    public StatusPengirimanManagedBean() {
    }

    public void checkAll(ValueChangeEvent valueChangeEvent) {
        boolean isSelected =
            ((Boolean)valueChangeEvent.getNewValue()).booleanValue();
        String RowSelectFlagAttribute = "SelectStatus";
        DCBindingContainer bindingContainer =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding dciter =
            bindingContainer.findIteratorBinding(ITERATOR_NAME);
        ViewObject vo = dciter.getViewObject();
        Row row = null;
        vo.reset();
        RowSetIterator rs = vo.createRowSetIterator(null);
        rs.reset();
        while (rs.hasNext()) {
            row = rs.next();
            if (isSelected) {
                row.setAttribute(RowSelectFlagAttribute, true);
            } else {
                row.setAttribute(RowSelectFlagAttribute, false);
            }
        }
        rs.closeRowSetIterator();

        AdfFacesContext.getCurrentInstance().addPartialTarget(tblStatusKirim);
    }

    public void setCheckAllRows(RichSelectBooleanCheckbox checkAllRows) {
        this.checkAllRows = checkAllRows;
    }

    public RichSelectBooleanCheckbox getCheckAllRows() {
        return checkAllRows;
    }

    public void setCheckRow(RichSelectBooleanCheckbox checkRow) {
        this.checkRow = checkRow;
    }

    public RichSelectBooleanCheckbox getCheckRow() {
        return checkRow;
    }

    public void setTblStatusKirim(RichTable tblStatusKirim) {
        this.tblStatusKirim = tblStatusKirim;
    }

    public RichTable getTblStatusKirim() {
        return tblStatusKirim;
    }

    public DCBindingContainer getDCBindingContainer() {
        DCBindingContainer bindingsContainer =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        return bindingsContainer;
    }

    public void confirmResendStatusKirim(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        Connection conn = null;
        int rowCount = 0;
        if (dialogEvent.getOutcome().name().equals("yes")) {
            DCBindingContainer bindings = this.getDCBindingContainer();
            DCIteratorBinding iterBinding =
                bindings.findIteratorBinding(ITERATOR_NAME);
            ViewObject vo = iterBinding.getViewObject();
            Row[] selectedRows = vo.getFilteredRows("SelectStatus", true);
            if (selectedRows.length > 0) {
                String kodeAb = "";
                for (Row row : selectedRows) {
                    kodeAb =
                            kodeAb + "'" + (String)row.getAttribute("KodeAb") +
                            "', ";
                    rowCount = rowCount + 1;
                }
                kodeAb = kodeAb.substring(0, kodeAb.length() - 2);
                System.out.println("KODE AB  : " + kodeAb);

                // Start resend with DB
                try {
                    Context ctx = new InitialContext();
                    DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                    conn = ds.getConnection();
                    PreparedStatement statement =
                        conn.prepareStatement("UPDATE file_sending_q SET status = 'READY' WHERE kode_ab in (" +
                                              kodeAb +
                                              ") AND status = 'DONE'");
                    statement.executeUpdate();
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    try {
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException sqle) {
                        FacesMessage msg =
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                             "Unhandled error while connect into database.",
                                             "Unhandled error while connect into database.");
                        fctx.addMessage(null, msg);
                    }
                }
                // End resend with DB
            }
        }

        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Total " + rowCount +
                             " selected statement sucessfully resend.",
                             "Total " + rowCount +
                             " selected statement sucessfully resend.");
        fctx.addMessage(null, msg);
    }

    public void confirmResendStatusKirimSingle(DialogEvent dialogEvent) {

        FacesContext fctx = FacesContext.getCurrentInstance();
        DCBindingContainer bindings = this.getDCBindingContainer();
        DCIteratorBinding iterBinding =
            bindings.findIteratorBinding(ITERATOR_NAME);
        Row resendStatementRow = iterBinding.getCurrentRow();
        String kodeAb = resendStatementRow.getAttribute("KodeAb").toString();

        System.out.println("KODE AB: " + kodeAb);

        Connection conn = null;
        if (dialogEvent.getOutcome().name().equals("yes")) {

            // Start resend with DB
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();
                PreparedStatement statement =
                    conn.prepareStatement("UPDATE file_sending_q SET sent_status = 'READY' WHERE kode_ab = '" +
                                          kodeAb +
                                          "' AND status = 'DONE'");                    
                statement.executeUpdate();
            } catch (Exception e) {
                System.out.println(e);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException sqle) {
                    FacesMessage msg =
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                         "Unhandled error while connect into database.",
                                         "Unhandled error while connect into database.");
                    fctx.addMessage(null, msg);
                }
            }
            // End resend with DB
        }

        FacesMessage msg =
            new FacesMessage(FacesMessage.SEVERITY_INFO, kodeAb +
                             " sucessfully resend.",
                             kodeAb + " sucessfully resend.");
        fctx.addMessage(null, msg);
    }
}
