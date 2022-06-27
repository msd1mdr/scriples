package app.bmri.scripless.backing.uploadstmt;

import app.bmri.scripless.adfextensions.ADFUtils;
import app.bmri.scripless.model.am.InvestorModuleImpl;

import app.bmri.scripless.model.vo.uploadstmt.BejstmtStgViewImpl;
import app.bmri.scripless.model.vo.uploadstmt.StatementTmpViewImpl;

import app.bmri.scripless.model.vo.uploadstmt.StatementTmpViewRowImpl;

import oracle.jbo.domain.Number;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import java.sql.SQLException;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.jbo.JboException;
import oracle.jbo.Row;

import org.apache.myfaces.trinidad.model.UploadedFile;


public class UploadDataStatementManagedBean {

    private UploadedFile ufCsv;
    private RichInputFile rifCsv;

    public UploadDataStatementManagedBean() {
        super();
    }

    public String uploadAction() throws IOException {

        String nextNavigate = "";

        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        FacesContext fctx = FacesContext.getCurrentInstance();

        InputStream is = null;
        String contentType = null;
        String fileExt = null;

        // Check file if already choose
        try {
            is = this.getUfCsv().getInputStream();
            contentType = this.getUfCsv().getContentType();
            String fileName = this.getUfCsv().getFilename();
            int fileNameLength = fileName.length();
            fileExt = fileName.substring(fileNameLength - 3);
        } catch (Exception ex) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "The operation can’t be completed because the file can't be found.",
                                 "The operation can’t be completed because the file can't be found.");
            fctx.addMessage(null, msg);
        }

        // Validate file type
        if (!contentType.equalsIgnoreCase("text/csv") &&
            !fileExt.equalsIgnoreCase("csv")) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "File extension not valid, .csv file extension required.",
                                 "File extension not valid, .csv file extension required.");
            fctx.addMessage(null, msg);
        } else {
            StatementTmpViewImpl statementTmp =
                (StatementTmpViewImpl)investorModule.getStatementTmpView1();

            // Truncate STATEMENT_TMP table
            truncateStatementTmp();

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(is));
            int lineNumber = 0;
            boolean dataFailed = false;
            try {
                String lineData;
                do {
                    lineData = reader.readLine();
                    if (lineData != null) {
                        lineNumber++;
                        String[] stmtArray = lineData.split(",");
                        
                        //System.out.println("LINE NUM: " + lineNumber + "; ARR SIZE: " + stmtArray.length);

                        if (stmtArray.length > 11 && stmtArray.length <= 13) {

                            //System.out.println("LINE NUM: " + lineNumber + "; ARR SIZE: " + stmtArray.length);

                            String extRef = stmtArray[0];
                            try {
                                extRef = stmtArray[0];
                            } catch (Exception e) {
                                extRef = "";
                            }

                            Integer seqNum = 0;
                            try {
                                seqNum = Integer.parseInt(stmtArray[1]);
                            } catch (NumberFormatException nfe) {
                                FacesMessage msg =
                                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                     "Can't identify number for SEQNUM at line " +
                                                     lineNumber,
                                                     "Can't identify number for OPENBAL at line " +
                                                     lineNumber);
                                fctx.addMessage(null, msg);
                            }

                            String ac;
                            try {
                                ac = stmtArray[2];
                            } catch (Exception e) {
                                ac = "";
                            }

                            String curCod;
                            try {
                                curCod = stmtArray[3];
                            } catch (Exception e) {
                                curCod = "";
                            }

                            String valDate;
                            try {
                                valDate = stmtArray[4];
                            } catch (Exception e) {
                                valDate = "";
                            }

                            Number openBal = new Number(0);
                            try {
                                openBal = new Number(stmtArray[5]);
                            } catch (SQLException e) {
                                FacesMessage msg =
                                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                     "Can't identify number for OPENBAL at line " +
                                                     lineNumber,
                                                     "Can't identify number for OPENBAL at line " +
                                                     lineNumber);
                                fctx.addMessage(null, msg);
                            }

                            String accRef;
                            try {
                                accRef = stmtArray[6];
                            } catch (Exception e) {
                                accRef = "";
                            }

                            String trxType;
                            try {
                                trxType = stmtArray[7];
                            } catch (Exception e) {
                                trxType = "";
                            }

                            String dc;
                            try {
                                dc = stmtArray[8];
                            } catch (Exception e) {
                                dc = "";
                            }

                            Number cashVal = new Number(0);
                            try {
                                cashVal = new Number(stmtArray[9]);
                            } catch (SQLException e) {
                                FacesMessage msg =
                                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                     "Can't identify number for CASHVAL at line " +
                                                     lineNumber,
                                                     "Can't identify number for CASHVAL at line " +
                                                     lineNumber);
                                fctx.addMessage(null, msg);
                            }

                            String description;
                            try {
                                description = stmtArray[10];
                            } catch (Exception e) {
                                description = "";
                            }

                            Number closeBal = new Number(0);
                            try {
                                closeBal = new Number(stmtArray[11]);
                            } catch (SQLException e) {
                                FacesMessage msg =
                                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                     "Can't identify number for CLOSEBAL at line " +
                                                     lineNumber,
                                                     "Can't identify number for CLOSEBAL at line " +
                                                     lineNumber);
                                fctx.addMessage(null, msg);
                            }

                            String notes;
                            if (stmtArray.length == 13) {
                                try {
                                    notes = stmtArray[12];
                                } catch (Exception e) {
                                    notes = "";
                                }
                            } else {
                                notes = "";
                            }

                            // Insert into STATEMENT_TMP table
                            Row statementTmpRow = statementTmp.createRow();
                            statementTmpRow.setAttribute("Extref", extRef);
                            statementTmpRow.setAttribute("Seqnum", seqNum);
                            statementTmpRow.setAttribute("Ac", ac);
                            statementTmpRow.setAttribute("Curcod", curCod);
                            statementTmpRow.setAttribute("Valdate", valDate);
                            statementTmpRow.setAttribute("Openbal", openBal);
                            statementTmpRow.setAttribute("Accref", accRef);
                            statementTmpRow.setAttribute("Trxtype", trxType);
                            statementTmpRow.setAttribute("Dc", dc);
                            statementTmpRow.setAttribute("Cashval", cashVal);
                            statementTmpRow.setAttribute("Description",
                                                          description);
                            statementTmpRow.setAttribute("Closebal",
                                                          closeBal);
                            statementTmpRow.setAttribute("Notes", notes);
                            statementTmp.insertRow(statementTmpRow);

                        } else {
                            dataFailed = true;
                            FacesMessage msg =
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                 "Data at line " + lineNumber +
                                                 " cannot be processed, there is missing or too much column.",
                                                 "Data at line " + lineNumber +
                                                 " cannot be processed, there is missing or too much column.");
                            fctx.addMessage(null, msg);
                            break;
                        }

                    } else {
                        try {
                            if (!dataFailed) {
                                investorModule.getDBTransaction().commit();
                                nextNavigate = "successupload";
                            } else {
                                nextNavigate = "";
                            }
                        } catch (JboException ex) {
                            investorModule.getDBTransaction().rollback();
                            nextNavigate = "";
                            FacesMessage msg =
                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                 "The operation can’t be completed. Unhandled error.",
                                                 ex.getDetailMessage());
                            fctx.addMessage(null, msg);
                        }
                    }
                } while (lineData != null);

            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        ;
                    }
                }
            }

        }

        return nextNavigate;
    }


    public void truncateStatementTmp() {
        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        StatementTmpViewImpl statementTmpView =
            (StatementTmpViewImpl)investorModule.getStatementTmpView1();
        statementTmpView.executeQuery();

        long numRows = statementTmpView.getEstimatedRowCount();

        if (numRows > 0) {
            Row row;
            while ((row = statementTmpView.next()) != null) {
                row.remove();
            }
            investorModule.getDBTransaction().commit();
        }
    }


    public void setUfCsv(UploadedFile ufCsv) {
        this.ufCsv = ufCsv;
    }

    public UploadedFile getUfCsv() {
        return ufCsv;
    }

    public void setRifCsv(RichInputFile rifCsv) {
        this.rifCsv = rifCsv;
    }

    public RichInputFile getRifCsv() {
        return rifCsv;
    }

    public String confirmUpload()  throws IOException {

        String nextNavigate = "";
        
        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        FacesContext fctx = FacesContext.getCurrentInstance();
        
        StatementTmpViewImpl statementTmpView =
            (StatementTmpViewImpl)investorModule.getStatementTmpView1();
        statementTmpView.executeQuery();

        long numRows = statementTmpView.getEstimatedRowCount();

        if (numRows > 0) {
            // Insert first row
            BejstmtStgViewImpl bejstmtStgView = (BejstmtStgViewImpl)investorModule.getBejstmtStgView1();
            
            StatementTmpViewRowImpl statementTmpRow = (StatementTmpViewRowImpl)statementTmpView.first();
            Row bejstmtStgRow = bejstmtStgView.createRow();
            bejstmtStgRow.setAttribute("Bankid", "BMAN2");
            bejstmtStgRow.setAttribute("Extref", statementTmpRow.getExtref());
            bejstmtStgRow.setAttribute("Acctno", statementTmpRow.getAc());
            bejstmtStgRow.setAttribute("Curcod", statementTmpRow.getCurcod());
            bejstmtStgRow.setAttribute("Valdat", statementTmpRow.getValdate());
            bejstmtStgRow.setAttribute("Opnbal", statementTmpRow.getOpenbal());
            bejstmtStgRow.setAttribute("Clsbal", statementTmpRow.getClosebal());
            bejstmtStgRow.setAttribute("Acnote", statementTmpRow.getNotes());
            bejstmtStgRow.setAttribute("Seqnum", statementTmpRow.getSeqnum());
            bejstmtStgRow.setAttribute("Drorcr", statementTmpRow.getDc());
            bejstmtStgRow.setAttribute("Trntyp", statementTmpRow.getTrxtype());
            bejstmtStgRow.setAttribute("Trnamt", statementTmpRow.getCashval());
            bejstmtStgRow.setAttribute("Trndsc", statementTmpRow.getDescription());
            bejstmtStgRow.setAttribute("Trnref", statementTmpRow.getAccref());
            bejstmtStgRow.setAttribute("Flag", "N");
            bejstmtStgView.insertRow(bejstmtStgRow);
            
            while (statementTmpView.hasNext()) {
                statementTmpRow = (StatementTmpViewRowImpl)statementTmpView.next();
                
                bejstmtStgRow = bejstmtStgView.createRow();
                bejstmtStgRow.setAttribute("Bankid", "BMAN2");
                bejstmtStgRow.setAttribute("Extref", statementTmpRow.getExtref());
                bejstmtStgRow.setAttribute("Acctno", statementTmpRow.getAc());
                bejstmtStgRow.setAttribute("Curcod", statementTmpRow.getCurcod());
                bejstmtStgRow.setAttribute("Valdat", statementTmpRow.getValdate());
                bejstmtStgRow.setAttribute("Opnbal", statementTmpRow.getOpenbal());
                bejstmtStgRow.setAttribute("Clsbal", statementTmpRow.getClosebal());
                bejstmtStgRow.setAttribute("Acnote", statementTmpRow.getNotes());
                bejstmtStgRow.setAttribute("Seqnum", statementTmpRow.getSeqnum());
                bejstmtStgRow.setAttribute("Drorcr", statementTmpRow.getDc());
                bejstmtStgRow.setAttribute("Trntyp", statementTmpRow.getTrxtype());
                bejstmtStgRow.setAttribute("Trnamt", statementTmpRow.getCashval());
                bejstmtStgRow.setAttribute("Trndsc", statementTmpRow.getDescription());
                bejstmtStgRow.setAttribute("Trnref", statementTmpRow.getAccref());
                bejstmtStgRow.setAttribute("Flag", "N");
                bejstmtStgView.insertRow(bejstmtStgRow);
            }
            
            try {
                investorModule.getDBTransaction().commit();
                nextNavigate = "reupload";

                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                                     numRows + " data successfully uploaded and confirmed.",
                                     numRows + " data successfully uploaded and confirmed.");
                fctx.addMessage(null, msg);
            } catch (JboException ex) {
                investorModule.getDBTransaction().rollback();
                nextNavigate = "reupload";
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "The operation can’t be completed. Unhandled error.",
                                     "The operation can’t be completed. Unhandled error.");
                fctx.addMessage(null, msg);
            } 
        }
        
        
        
        return nextNavigate;
    }
}
