package app.bmri.scripless.backing.uploadstatic;

import app.bmri.scripless.adfextensions.ADFUtils;
import app.bmri.scripless.model.am.InvestorModuleImpl;

import app.bmri.scripless.model.vo.uploadstatic.BejstatStgViewImpl;
import app.bmri.scripless.model.vo.uploadstatic.StaticTmpViewImpl;

import app.bmri.scripless.model.vo.uploadstatic.StaticTmpViewRowImpl;

import java.io.BufferedReader;
import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.jbo.JboException;
import oracle.jbo.Row;

import org.apache.myfaces.trinidad.model.UploadedFile;

public class UploadDataStaticManagedBean {

    private UploadedFile ufCsv;
    private RichInputFile rifCsv;

    public UploadDataStaticManagedBean() {
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
            StaticTmpViewImpl staticTmp =
                (StaticTmpViewImpl)investorModule.getStaticTmpView1();

            // Truncate STATIC_TMP table
            truncateStaticTmp();

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

                        if (stmtArray.length > 8 && stmtArray.length <= 10) {

                            //System.out.println("LINE NUM: " + lineNumber + "; ARR SIZE: " + stmtArray.length);

                            String extRef = stmtArray[0];
                            try {
                                extRef = stmtArray[0];
                            } catch (Exception e) {
                                extRef = "";
                            }

                            String participantId;
                            try {
                                participantId = stmtArray[1];
                            } catch (Exception e) {
                                participantId = "";
                            }

                            String participantName;
                            try {
                                participantName = stmtArray[2];
                            } catch (Exception e) {
                                participantName = "";
                            }

                            String investorName;
                            try {
                                investorName = stmtArray[3];
                            } catch (Exception e) {
                                investorName = "";
                            }

                            String sidNumber;
                            try {
                                sidNumber = stmtArray[4];
                            } catch (Exception e) {
                                sidNumber = "";
                            }

                            String accountNumber;
                            try {
                                accountNumber = stmtArray[5];
                            } catch (Exception e) {
                                accountNumber = "";
                            }

                            String bankAccNumber;
                            try {
                                bankAccNumber = stmtArray[6];
                            } catch (Exception e) {
                                bankAccNumber = "";
                            }

                            String bankCode;
                            try {
                                bankCode = stmtArray[7];
                            } catch (Exception e) {
                                bankCode = "";
                            }

                            String activityDate;
                            try {
                                activityDate = stmtArray[8];
                            } catch (Exception e) {
                                activityDate = "";
                            }

                            String activity;
                            if (stmtArray.length == 10) {
                                try {
                                    activity = stmtArray[9];
                                } catch (Exception e) {
                                    activity = "";
                                }
                            } else {
                                activity = "";
                            }

                            // Insert into STATIC_TMP table
                            Row staticTmpRow = staticTmp.createRow();
                            staticTmpRow.setAttribute("Extref", extRef);
                            staticTmpRow.setAttribute("Participantid",
                                                      participantId);
                            staticTmpRow.setAttribute("Participantname",
                                                      participantName);
                            staticTmpRow.setAttribute("Investorname",
                                                      investorName);
                            staticTmpRow.setAttribute("Sidnumber", sidNumber);
                            staticTmpRow.setAttribute("Accountnumber",
                                                      accountNumber);
                            staticTmpRow.setAttribute("Bankaccnumber",
                                                      bankAccNumber);
                            staticTmpRow.setAttribute("Bankcode", bankCode);
                            staticTmpRow.setAttribute("Activitydate",
                                                      activityDate);
                            staticTmpRow.setAttribute("Activity", activity);
                            staticTmp.insertRow(staticTmpRow);

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
                            nextNavigate = "";
                            investorModule.getDBTransaction().rollback();
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


    public void truncateStaticTmp() {
        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        StaticTmpViewImpl staticTmpView =
            (StaticTmpViewImpl)investorModule.getStaticTmpView1();
        staticTmpView.executeQuery();

        long numRows = staticTmpView.getEstimatedRowCount();

        if (numRows > 0) {
            Row row;
            while ((row = staticTmpView.next()) != null) {
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


    public String confirmUpload() throws IOException {

        String nextNavigate = "";

        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        FacesContext fctx = FacesContext.getCurrentInstance();

        StaticTmpViewImpl staticTmpView =
            (StaticTmpViewImpl)investorModule.getStaticTmpView1();
        staticTmpView.executeQuery();

        long numRows = staticTmpView.getEstimatedRowCount();

        if (numRows > 0) {
            // Insert first row
            BejstatStgViewImpl bejstatStgView = investorModule.getBejstatStgView1();

            StaticTmpViewRowImpl staticTmpRow =
                (StaticTmpViewRowImpl)staticTmpView.first();
            Row bejstatStgRow = bejstatStgView.createRow();
            bejstatStgRow.setAttribute("Extref", staticTmpRow.getExtref());
            bejstatStgRow.setAttribute("Partid",
                                       staticTmpRow.getParticipantid());
            bejstatStgRow.setAttribute("Partname",
                                       staticTmpRow.getParticipantname());
            bejstatStgRow.setAttribute("Ivstname",
                                       staticTmpRow.getInvestorname());
            bejstatStgRow.setAttribute("Sivstid", staticTmpRow.getSidnumber());
            bejstatStgRow.setAttribute("Sacctno",
                                       staticTmpRow.getAccountnumber());
            bejstatStgRow.setAttribute("Acctno",
                                       staticTmpRow.getBankaccnumber());
            bejstatStgRow.setAttribute("Bnkcod", staticTmpRow.getBankcode());
            bejstatStgRow.setAttribute("Actdate",
                                       staticTmpRow.getActivitydate());
            bejstatStgRow.setAttribute("Activity", staticTmpRow.getActivity());
            bejstatStgRow.setAttribute("Flag", "N");
            bejstatStgView.insertRow(bejstatStgRow);

            while (staticTmpView.hasNext()) {
                staticTmpRow = (StaticTmpViewRowImpl)staticTmpView.next();

                bejstatStgRow = bejstatStgView.createRow();
                bejstatStgRow.setAttribute("Extref", staticTmpRow.getExtref());
                bejstatStgRow.setAttribute("Partid",
                                           staticTmpRow.getParticipantid());
                bejstatStgRow.setAttribute("Partname",
                                           staticTmpRow.getParticipantname());
                bejstatStgRow.setAttribute("Ivstname",
                                           staticTmpRow.getInvestorname());
                bejstatStgRow.setAttribute("Sivstid",
                                           staticTmpRow.getSidnumber());
                bejstatStgRow.setAttribute("Sacctno",
                                           staticTmpRow.getAccountnumber());
                bejstatStgRow.setAttribute("Acctno",
                                           staticTmpRow.getBankaccnumber());
                bejstatStgRow.setAttribute("Bnkcod",
                                           staticTmpRow.getBankcode());
                bejstatStgRow.setAttribute("Actdate",
                                           staticTmpRow.getActivitydate());
                bejstatStgRow.setAttribute("Activity",
                                           staticTmpRow.getActivity());
                bejstatStgRow.setAttribute("Flag", "N");
                bejstatStgView.insertRow(bejstatStgRow);
            }

            try {
                investorModule.getDBTransaction().commit();
                nextNavigate = "reupload";

                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_INFO, numRows +
                                     " data successfully uploaded and confirmed.",
                                     numRows +
                                     " data successfully uploaded and confirmed.");
                fctx.addMessage(null, msg);
            } catch (JboException ex) {
                investorModule.getDBTransaction().rollback();
                
                nextNavigate = "reupload";
                FacesMessage msg =
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                     "The operation can’t be completed. Unhandled error. Please retry.",
                                     "The operation can’t be completed. Unhandled error. Please retry.");
                fctx.addMessage(null, msg);
            }
        }


        return nextNavigate;
    }
}
