package app.bmri.scripless.backing.resendstmt;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.input.RichSelectBooleanCheckbox;
import oracle.adf.view.rich.context.AdfFacesContext;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;

import oracle.adf.view.rich.event.DialogEvent;

import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;

public class ResendStatementManagedBean {
    private static final String ITERATOR_NAME = "ResendStatementView1Iterator";
    private RichSelectBooleanCheckbox checkRow;
    private RichSelectBooleanCheckbox checkAllRows;
    private RichTable tableResendStmt;
    private String selectedRowCount;

    public ResendStatementManagedBean() {
    }

    public void setCheckRow(RichSelectBooleanCheckbox checkRow) {
        this.checkRow = checkRow;
    }

    public RichSelectBooleanCheckbox getCheckRow() {
        return checkRow;
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

        AdfFacesContext.getCurrentInstance().addPartialTarget(tableResendStmt);
    }

    public void setCheckAllRows(RichSelectBooleanCheckbox checkAllRows) {
        this.checkAllRows = checkAllRows;
    }

    public RichSelectBooleanCheckbox getCheckAllRows() {
        return checkAllRows;
    }

    public void setTableResendStmt(RichTable tableResendStmt) {
        this.tableResendStmt = tableResendStmt;
    }

    public RichTable getTableResendStmt() {
        return tableResendStmt;
    }

    public DCBindingContainer getDCBindingContainer() {
        DCBindingContainer bindingsContainer =
            (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        return bindingsContainer;
    }

    public void confirmResendStmt(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        Connection conn = null;
        if (dialogEvent.getOutcome().name().equals("yes")) {
            DCBindingContainer bindings = this.getDCBindingContainer();
            DCIteratorBinding iterBinding =
                bindings.findIteratorBinding(ITERATOR_NAME);
            ViewObject vo = iterBinding.getViewObject();
            Row[] selectedRows = vo.getFilteredRows("SelectStatus", true);
            int rowCount = 0;

            for (Row row : selectedRows) {
                String extRef = (String)row.getAttribute("Extref");
                //System.out.println("EXTREF: " + row.getAttribute("Extref") + "; VCOUNT: " + rowCount);

                // Start resend with DB
                try {
                    Context ctx = new InitialContext();
                    DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                    conn = ds.getConnection();
                    PreparedStatement statement =
                        conn.prepareStatement("BEGIN SI_ADMIN.RESENDSTMT(?,?); END;");
                    statement.setString(1, extRef);
                    statement.setInt(2, rowCount);
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
                rowCount = rowCount + 1;
                // End resend with DB
            }

            //System.out.println("TOTAL RESEND: " + rowCount);

            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Total " +
                                 rowCount +
                                 " selected statement sucessfully resend.",
                                 "Total " + rowCount +
                                 " selected statement sucessfully resend.");
            fctx.addMessage(null, msg);
        }
    }

    public void setSelectedRowCount(String selectedRowCount) {
        this.selectedRowCount = selectedRowCount;
    }

    public String getSelectedRowCount() {
        DCBindingContainer bindings = this.getDCBindingContainer();
        DCIteratorBinding iterBinding =
            bindings.findIteratorBinding(ITERATOR_NAME);
        ViewObject vo = iterBinding.getViewObject();
        Row[] selectedRows = vo.getFilteredRows("SelectStatus", true);
        selectedRowCount = Long.toString(selectedRows.length);
        return selectedRowCount;
    }

    public void confirmResendStmtSingle(DialogEvent dialogEvent) {                

        FacesContext fctx = FacesContext.getCurrentInstance();
        DCBindingContainer bindings = this.getDCBindingContainer();
        DCIteratorBinding iterBinding =
            bindings.findIteratorBinding(ITERATOR_NAME);
        Row resendStatementRow = iterBinding.getCurrentRow();
        String extRefVal = resendStatementRow.getAttribute("Extref").toString();
        String stmtId = resendStatementRow.getAttribute("Statementid").toString();
        
        //System.out.println("EXTREF: " + extRefVal);
        
        Connection conn = null;
        int rowCount = 0;
        if (dialogEvent.getOutcome().name().equals("yes")) {

            // Start resend with DB
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();
                PreparedStatement statement =
                    conn.prepareStatement("BEGIN SI_ADMIN.RESENDSTMT(?,?); END;");
                statement.setString(1, extRefVal);
                statement.setInt(2, rowCount);
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
            new FacesMessage(FacesMessage.SEVERITY_INFO, "Statement ID " + stmtId + " sucessfully resend.",
                             "Statement ID " + stmtId + " sucessfully resend.");
        fctx.addMessage(null, msg);

    }
}
