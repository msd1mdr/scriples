package app.bmri.scripless.backing.genstmteom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.faces.event.PhaseEvent;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;

import oracle.adf.model.BindingContext;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.layout.RichPanelGroupLayout;
import oracle.adf.view.rich.component.rich.nav.RichCommandButton;
import oracle.adf.view.rich.component.rich.nav.RichCommandToolbarButton;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import org.apache.myfaces.trinidad.event.PollEvent;

public class GenerateStatementManagedBean {
    private RichPanelGroupLayout panelLayoutRunning;
    private RichPanelGroupLayout panelLayoutStopped;
    private RichPanelGroupLayout panelLayoutWarning;
    private RichPanelGroupLayout panelLayoutTaxInRunning;
    private RichPanelGroupLayout panelLayoutTaxInStopped;
    private RichPanelGroupLayout panelLayoutTaxInWarning;
    private RichCommandButton btnEnable;
    private RichCommandButton btnDisable;
    private RichCommandToolbarButton btnGenerate;
    private RichTable tblFileSending;
    private final String JOB_CEK_OUTGOING_STMT = "JOB_CEK_STATUS_OUTGOING_STMT";
    private final String PAGEFLOW_OUTGOING_STMT = "jobOutgoingStmt";
    private final String JOB_CEK_PAJAK_BUNGA = "JOB_CEK_STATUS_PAJAK_BUNGA";
    private final String PAGEFLOW_PAJAK_BUNGA = "jobPajakBunga";
    private final String JOB_GEN_FILE_PAJAK_BUNGA = "JOB_GEN_FILE_PAJAK_BUNGA";
    private final String PAGEFLOW_GEN_FILE_PAJAK_BUNGA = "jobGenFilePajakBunga";

    public GenerateStatementManagedBean() {
    }

    public void setPanelLayoutTaxInRunning(RichPanelGroupLayout panelLayoutTaxInRunning) {
        this.panelLayoutTaxInRunning = panelLayoutTaxInRunning;
    }

    public RichPanelGroupLayout getPanelLayoutTaxInRunning() {
        return panelLayoutTaxInRunning;
    }

    public void setPanelLayoutTaxInStopped(RichPanelGroupLayout panelLayoutTaxInStopped) {
        this.panelLayoutTaxInStopped = panelLayoutTaxInStopped;
    }

    public RichPanelGroupLayout getPanelLayoutTaxInStopped() {
        return panelLayoutTaxInStopped;
    }

    public void setPanelLayoutTaxInWarning(RichPanelGroupLayout panelLayoutTaxInWarning) {
        this.panelLayoutTaxInWarning = panelLayoutTaxInWarning;
    }

    public RichPanelGroupLayout getPanelLayoutTaxInWarning() {
        return panelLayoutTaxInWarning;
    }

    public void setPanelLayoutRunning(RichPanelGroupLayout panelLayoutRunning) {
        this.panelLayoutRunning = panelLayoutRunning;
    }

    public RichPanelGroupLayout getPanelLayoutRunning() {
        return panelLayoutRunning;
    }

    public void setPanelLayoutStopped(RichPanelGroupLayout panelLayoutStopped) {
        this.panelLayoutStopped = panelLayoutStopped;
    }

    public RichPanelGroupLayout getPanelLayoutStopped() {
        return panelLayoutStopped;
    }

    public void setBtnEnable(RichCommandButton btnEnable) {
        this.btnEnable = btnEnable;
    }

    public RichCommandButton getBtnEnable() {
        return btnEnable;
    }

    public void setBtnDisable(RichCommandButton btnDisable) {
        this.btnDisable = btnDisable;
    }

    public RichCommandButton getBtnDisable() {
        return btnDisable;
    }

    public void setBtnGenerate(RichCommandToolbarButton btnGenerate) {
        this.btnGenerate = btnGenerate;
    }

    public RichCommandToolbarButton getBtnGenerate() {
        return btnGenerate;
    }

    public void setPanelLayoutWarning(RichPanelGroupLayout panelLayoutWarning) {
        this.panelLayoutWarning = panelLayoutWarning;
    }

    public RichPanelGroupLayout getPanelLayoutWarning() {
        return panelLayoutWarning;
    }
    
    public void setTblFileSending(RichTable tblFileSending) {
        this.tblFileSending = tblFileSending;
    }

    public RichTable getTblFileSending() {
        return tblFileSending;
    }
    
    public void jobEnable(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map pageFlowScope = adfCtx.getPageFlowScope();
        Connection conn = null;
        if (dialogEvent.getOutcome().name().equals("yes")) {

            // Start enable job statement DB
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();
                PreparedStatement statement =
                    conn.prepareStatement("BEGIN SI_ADMIN.STMT_JOB(?); END;");
                statement.setString(1, "ENABLE");
                statement.executeUpdate();
            } catch (SQLException e) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "SQL Exception:\n " + e,
                                     "SQL Exception:\n" +
                        e);
                fctx.addMessage(null, msg);
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
            // End enable job statement DB

            // Set Into Page Flow Scope
            pageFlowScope.put("jobStatus", "SCHEDULED");

            // Partial target
            AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutRunning);
            AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutStopped);
            AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutWarning);
            AdfFacesContext.getCurrentInstance().addPartialTarget(btnEnable);
            AdfFacesContext.getCurrentInstance().addPartialTarget(btnDisable);
            AdfFacesContext.getCurrentInstance().addPartialTarget(btnGenerate);
        }
    }

    public void jobDisable(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map pageFlowScope = adfCtx.getPageFlowScope();
        Connection conn = null;
        if (dialogEvent.getOutcome().name().equals("yes")) {

            // Start disable job statement DB
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();
                PreparedStatement statement =
                    conn.prepareStatement("BEGIN SI_ADMIN.STMT_JOB(?); END;");
                statement.setString(1, "DISABLE");
                statement.executeUpdate();
            } catch (SQLException e) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "SQL Exception: " + e,
                                     "SQL Exception: " + e);
                fctx.addMessage(null, msg);
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
            // End disable job statement DB

            // Set Into Page Flow Scope
            pageFlowScope.put("jobStatus", "DISABLED");

            // Partial target
            AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutRunning);
            AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutStopped);
            AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutWarning);
            AdfFacesContext.getCurrentInstance().addPartialTarget(btnEnable);
            AdfFacesContext.getCurrentInstance().addPartialTarget(btnDisable);
            AdfFacesContext.getCurrentInstance().addPartialTarget(btnGenerate);
        }
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }
    
    public void genStatement(DialogEvent dialogEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        Connection conn = null;
        if (dialogEvent.getOutcome().name().equals("yes")) {

            // Start resend with DB
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();
                /*
                PreparedStatement statement =
                    conn.prepareStatement("BEGIN SI_ADMIN.SI_INVDISPATCH_2.file_bunga_pajak; END;");
                */
                PreparedStatement statement =
                    conn.prepareStatement("BEGIN " + getJobParam(JOB_GEN_FILE_PAJAK_BUNGA, PAGEFLOW_GEN_FILE_PAJAK_BUNGA) + "; END;");
                statement.executeUpdate();
            } catch (SQLException e) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "SQL Exception: " + e,
                                     "SQL Exception: " + e);
                fctx.addMessage(null, msg);
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

            // Partial target
            BindingContainer bindings = getBindings();
            OperationBinding operationBinding =
                bindings.getOperationBinding("Execute");
            operationBinding.execute();
            
            AdfFacesContext.getCurrentInstance().addPartialTarget(tblFileSending);
        }
    }

    public void checkJobStatus(PhaseEvent phaseEvent) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map pageFlowScope = adfCtx.getPageFlowScope();
        Connection conn = null;
        String jobOutStmtState = "";
        String jobTaxInState = "";
        
        /* START: GET STATUS OF GOING STATEMENT JOB */
        if (pageFlowScope.get("jobStatus") == null) {
            // Start get current job status from DB
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();
                
                /*
                 PreparedStatement statement =
                    conn.prepareStatement("SELECT JOB_NAME, STATE FROM DBA_SCHEDULER_JOBS WHERE JOB_NAME = 'si_invdispatch2.outgoing_stmt'");
                */
                PreparedStatement statement =
                    conn.prepareStatement("SELECT JOB_NAME, STATE FROM DBA_SCHEDULER_JOBS WHERE JOB_NAME = '" + getJobParam(JOB_CEK_OUTGOING_STMT, PAGEFLOW_OUTGOING_STMT) + "'");
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    jobOutStmtState = rs.getString("STATE");
                    //System.out.println("JOB STATE: " + jobOutStmtState);
                }

                // Set Into Page Flow Scope
                pageFlowScope.put("jobStatus", jobOutStmtState);
                //System.out.println("The initial value is= " + adfCtx.getPageFlowScope().get("jobStatus"));

                if (adfCtx.getPageFlowScope().get("jobStatus") == "") {
                    pageFlowScope.put("jobStatus", "UNCHECK");
                }

            } catch (SQLException e) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "SQL Exception: " + e,
                                     "SQL Exception: " + e);
                fctx.addMessage(null, msg);
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
            // End get current job status from DB
        } else {
            // DO NOTHING
        }

        // Partial target
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutRunning);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutStopped);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutWarning);
        AdfFacesContext.getCurrentInstance().addPartialTarget(btnEnable);
        AdfFacesContext.getCurrentInstance().addPartialTarget(btnDisable);
        AdfFacesContext.getCurrentInstance().addPartialTarget(btnGenerate);
        /* END: GET STATUS OF GOING STATEMENT JOB */        

        /* START: GET STATUS OF PAJAK BUNGA JOB */
        if (pageFlowScope.get("jobTaxInStatus") == null) {
            // Start get current tax interest job status from DB
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();
                /*
                PreparedStatement statement =
                    conn.prepareStatement("SELECT JOB_NAME, STATE FROM DBA_SCHEDULER_JOBS WHERE JOB_NAME = 'file_pajak_bunga'");
                */
                PreparedStatement statement =
                    conn.prepareStatement("SELECT JOB_NAME, STATE FROM DBA_SCHEDULER_JOBS WHERE JOB_NAME = '" + getJobParam(JOB_CEK_PAJAK_BUNGA, PAGEFLOW_PAJAK_BUNGA) + "'");
                
                ResultSet rs = statement.executeQuery();

                while (rs.next()) {
                    jobTaxInState = rs.getString("STATE");
                    //System.out.println("JOB STATE: " + jobTaxInState);
                }

                // Set Into Page Flow Scope
                pageFlowScope.put("jobTaxInStatus", jobTaxInState);
                //System.out.println("The initial value is= " + adfCtx.getPageFlowScope().get("jobTaxInStatus"));

                if (adfCtx.getPageFlowScope().get("jobTaxInStatus") == "") {
                    pageFlowScope.put("jobTaxInStatus", "UNCHECK");
                }

            } catch (SQLException e) {
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "SQL Exception: " + e,
                                     "SQL Exception: " + e);
                fctx.addMessage(null, msg);
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
            // End get current tax interest job status from DB
        } else {
            // DO NOTHING
        }

        // Partial target
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutTaxInRunning);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutTaxInStopped);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutTaxInWarning);
        /* END: GET STATUS OF PAJAK BUNGA JOB */

    }

    public void checkStatusJobPoll(PollEvent pollEvent) {

        FacesContext fctx = FacesContext.getCurrentInstance();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map pageFlowScope = adfCtx.getPageFlowScope();
        Connection conn = null;
        String jobOutgoingStmt = (String)adfCtx.getPageFlowScope().get("jobOutgoingStmt");
        String jobPajakBunga = (String)adfCtx.getPageFlowScope().get("jobPajakBunga");
        String jobOutStmtState = "";
        String jobTaxInState = "";

        //System.out.println("JOB OUTGOING STMT= " + adfCtx.getPageFlowScope().get("jobOutgoingStmt"));
        //System.out.println("JOB PAJAK BUNGA  = " + adfCtx.getPageFlowScope().get("jobPajakBunga"));
        //System.out.println("The value poll=" + adfCtx.getPageFlowScope().get("jobStatus"));
        
        /* START: GET STATUS OF GOING STATEMENT JOB */
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            /*
            PreparedStatement statement =
                conn.prepareStatement("SELECT JOB_NAME, STATE FROM DBA_SCHEDULER_JOBS WHERE JOB_NAME = 'si_invdispatch2.outgoing_stmt'");
            */
            PreparedStatement statement =
                conn.prepareStatement("SELECT JOB_NAME, STATE FROM DBA_SCHEDULER_JOBS WHERE JOB_NAME = '"+jobOutgoingStmt+"'");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                jobOutStmtState = rs.getString("STATE");
                //System.out.println("JOB STATE: " + jobOutStmtState);
            }

            // Set Into Page Flow Scope
            pageFlowScope.put("jobStatus", jobOutStmtState);

            if (adfCtx.getPageFlowScope().get("jobStatus") == "") {
                pageFlowScope.put("jobStatus", "UNCHECK");
            }
        } catch (SQLException e) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "SQL Exception: " + e,
                                     "SQL Exception: " + e);
            fctx.addMessage(null, msg);
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

        // Partial target
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutRunning);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutStopped);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutWarning);
        AdfFacesContext.getCurrentInstance().addPartialTarget(btnEnable);
        AdfFacesContext.getCurrentInstance().addPartialTarget(btnDisable);
        AdfFacesContext.getCurrentInstance().addPartialTarget(btnGenerate);        
        /* END: GET STATUS OF GOING STATEMENT JOB */

        /* START: GET STATUS OF PAJAK BUNGA JOB */
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            /*
            PreparedStatement statement =
                conn.prepareStatement("SELECT JOB_NAME, STATE FROM DBA_SCHEDULER_JOBS WHERE JOB_NAME = 'file_pajak_bunga'");
            */
            PreparedStatement statement =
                conn.prepareStatement("SELECT JOB_NAME, STATE FROM DBA_SCHEDULER_JOBS WHERE JOB_NAME = '"+jobPajakBunga+"'");
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                jobTaxInState = rs.getString("STATE");
                //System.out.println("JOB TAX IN STATE: " + jobTaxInState);
            }

            // Set Into Page Flow Scope
            pageFlowScope.put("jobTaxInStatus", jobTaxInState);

            if (adfCtx.getPageFlowScope().get("jobTaxInStatus") == "") {
                pageFlowScope.put("jobTaxInStatus", "UNCHECK");
            }
        } catch (SQLException e) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "SQL Exception: " + e,
                                     "SQL Exception: " + e);
            fctx.addMessage(null, msg);
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

        // Partial target
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutTaxInRunning);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutTaxInStopped);
        AdfFacesContext.getCurrentInstance().addPartialTarget(panelLayoutTaxInWarning);
        /* END: GET STATUS OF PAJAK BUNGA JOB */
    }
    
    public String getJobParam(String configKey, String scopeName) {

        FacesContext fctx = FacesContext.getCurrentInstance();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map pageFlowScope = adfCtx.getPageFlowScope();
        Connection conn = null;
        String paramValue = "";

        // Start get current job status from DB
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            PreparedStatement statement =
                conn.prepareStatement("SELECT JC.CONFIG_VALUE FROM JSF_CONFIGS JC WHERE JC.CONFIG_KEY = ?");
            statement.setString(1, configKey);
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                paramValue = rs.getString("CONFIG_VALUE");
                //System.out.println("CONFIG VALUE: " + paramValue);
            }

            // Set Into Page Flow Scope
            pageFlowScope.put(scopeName, paramValue);
            //System.out.println("The initial value is= " + adfCtx.getPageFlowScope().get(scopeName));

        } catch (SQLException e) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                 "SQL Exception: " + e,
                                 "SQL Exception: " + e);
            fctx.addMessage(null, msg);
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
        // End get current job status from DB
        return paramValue;
    }
}
