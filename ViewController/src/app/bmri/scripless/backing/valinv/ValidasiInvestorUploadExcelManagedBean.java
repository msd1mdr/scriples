package app.bmri.scripless.backing.valinv;

import app.bmri.scripless.adfextensions.ADFUtils;

import app.bmri.scripless.model.am.InvestorModuleImpl;

import app.bmri.scripless.model.vo.valinvestor.ValidationMsgViewImpl;
import app.bmri.scripless.model.vo.valinvestor.ValidationTmpViewImpl;

import app.bmri.scripless.model.vo.valinvestor.ValidationTmpViewRowImpl;

import java.io.IOException;

import java.io.InputStream;

import java.net.URLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.faces.application.FacesMessage;

import javax.faces.context.FacesContext;

import javax.faces.event.ActionEvent;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.sql.DataSource;

import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.jbo.JboException;
import oracle.jbo.Row;

import oracle.jbo.server.SequenceImpl;

import org.apache.myfaces.trinidad.model.UploadedFile;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

public class ValidasiInvestorUploadExcelManagedBean {

    private UploadedFile ufExcel;
    private RichInputFile rifExcel;
    private final int PARTID_LENGTH = 5;
    private final int SID_LENGTH = 15;
    private final int ACCNUM_LENGTH = 14;
    private final int MAX_ROWS = 30000;

    public ValidasiInvestorUploadExcelManagedBean() {
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
            is = this.getUfExcel().getInputStream();
            contentType = this.getUfExcel().getContentType();
            String fileName = this.getUfExcel().getFilename();
            int fileNameLength = fileName.length();
            fileExt = fileName.substring(fileNameLength - 3);
        } catch (Exception ex) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "The operation can’t be completed because the file can't be found.",
                                 "The operation can’t be completed because the file can't be found.");
            fctx.addMessage(null, msg);
        }

        // Validate file type
        if (!contentType.equalsIgnoreCase("application/vnd.ms-excel") &&
            !fileExt.equalsIgnoreCase("xls")) {
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "File extension not valid, .xls file extension required.",
                                 "File extension not valid, .xls file extension required.");
            fctx.addMessage(null, msg);
        } else {
            HSSFWorkbook wb = new HSSFWorkbook(is);
            int sheetNum = wb.getNumberOfSheets();

            ValidationTmpViewImpl validationTmp =
                (ValidationTmpViewImpl)investorModule.getValidationTmpView1();

            if (sheetNum == 1) {

                HSSFSheet ws = wb.getSheetAt(0);

                int rowNum = ws.getLastRowNum() + 1;

                if (rowNum - 1 < MAX_ROWS) {

                    Map<Integer, String> mapPartId =
                        new HashMap<Integer, String>();
                    Map<Integer, String> mapSidNum =
                        new HashMap<Integer, String>();
                    Map<Integer, String> mapAccNum =
                        new HashMap<Integer, String>();

                    boolean dataPrep = true;

                    for (int i = 1; i < rowNum; i++) {
                        HSSFRow row = ws.getRow(i);
                        if (row != null) {
                            // Fetch data participant id
                            String partId, sidNum, accNum = null;
                            if (row.getPhysicalNumberOfCells() == 1 ||
                                row.getPhysicalNumberOfCells() == 3) {

                                // Add data participant into list
                                try {
                                    partId =
                                            row.getCell(0).getStringCellValue();
                                } catch (NullPointerException e) {
                                    partId = "";
                                } catch (Exception e) {
                                    Cell cell = row.getCell(0);
                                    cell.setCellType(Cell.CELL_TYPE_STRING);
                                    partId =
                                            row.getCell(0).getStringCellValue();
                                }

                                if (!partId.isEmpty() &&
                                    partId.length() <= PARTID_LENGTH) {
                                    mapPartId.put(i, partId);
                                } else if (partId.length() > PARTID_LENGTH) {
                                    FacesMessage msg =
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                         "Participant ID at line " +
                                                         i +
                                                         " has more character than allowed",
                                                         "Participant ID at line " +
                                                         i +
                                                         " has more character than allowed");
                                    fctx.addMessage(null, msg);
                                    dataPrep = false;
                                    break;
                                } else {
                                    // DO NOTHING
                                }

                                // Add sid number into list
                                try {
                                    sidNum =
                                            row.getCell(1).getStringCellValue();
                                } catch (NullPointerException e) {
                                    sidNum = "";
                                } catch (Exception e) {
                                    Cell cell = row.getCell(1);
                                    cell.setCellType(Cell.CELL_TYPE_STRING);
                                    sidNum =
                                            row.getCell(1).getStringCellValue();
                                }

                                if (!sidNum.isEmpty() &&
                                    sidNum.length() <= SID_LENGTH) {
                                    mapSidNum.put(i, sidNum);
                                } else if (sidNum.length() > SID_LENGTH) {
                                    FacesMessage msg =
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                         "SID Number at line " +
                                                         i +
                                                         " has more character than allowed",
                                                         "SID Number at line " +
                                                         i +
                                                         " has more character than allowed");
                                    fctx.addMessage(null, msg);
                                    dataPrep = false;
                                    break;
                                } else {
                                    // DO NOTHING
                                }

                                // Add account number into list
                                try {
                                    accNum =
                                            row.getCell(2).getStringCellValue();
                                } catch (NullPointerException e) {
                                    accNum = "";
                                } catch (Exception e) {
                                    Cell cell = row.getCell(2);
                                    cell.setCellType(Cell.CELL_TYPE_STRING);
                                    accNum =
                                            row.getCell(2).getStringCellValue();
                                }

                                if (!accNum.isEmpty() &&
                                    accNum.length() <= ACCNUM_LENGTH) {
                                    mapAccNum.put(i, accNum);
                                } else if (accNum.length() > ACCNUM_LENGTH) {
                                    FacesMessage msg =
                                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                         "Account Number at line " +
                                                         i +
                                                         " has more character than allowed",
                                                         "Account Number at line " +
                                                         i +
                                                         " has more character than allowed");
                                    fctx.addMessage(null, msg);
                                    dataPrep = false;
                                    break;
                                } else {
                                }
                            }
                        }
                    }

                    // Truncate VALIDATION_TMP table
                    truncateValidationTmp();

                    // Insert into VALIDATION_TMP table
                    Date today = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    String todayDate = sdf.format(today).toString();

                    SequenceImpl seq =
                        new SequenceImpl("VALIDATION_INVESTOR_SEQ",
                                         investorModule.getDBTransaction());
                    String seqNext = seq.getSequenceNumber().toString();

                    String participantId = null;
                    String sidNumber = null;
                    String accountNumber = null;

                    for (Object key : mapPartId.keySet()) {
                        int i = Integer.valueOf(key.toString());
                        participantId = mapPartId.get(i);
                        sidNumber = mapSidNum.get(i);
                        accountNumber = mapAccNum.get(i);
                        /*
                    System.out.println("============================");
                    System.out.println("("+i+") "+"PARTI ID: " + participantId);
                    System.out.println("("+i+") "+"SID NUM : " + sidNumber);
                    System.out.println("("+i+") "+"ACC NUM : " + accountNumber);
                    System.out.println("============================");
                    */
                        // Process insert create new row
                        Row validationTmpRow = validationTmp.createRow();
                        validationTmpRow.setAttribute("Batchref",
                                                      "A" + todayDate +
                                                      seqNext);
                        validationTmpRow.setAttribute("Detailref",
                                                      "A" + todayDate +
                                                      seqNext + i + "a");
                        validationTmpRow.setAttribute("Participantid",
                                                      participantId);
                        validationTmpRow.setAttribute("Sidnumber", sidNumber);
                        validationTmpRow.setAttribute("Accountnumber",
                                                      accountNumber);
                        validationTmp.insertRow(validationTmpRow);
                    }
                    // End Insert into VALIDATION_TMP table

                    try {
                        investorModule.getDBTransaction().commit();
                        nextNavigate = "successupload";
                    } catch (JboException ex) {
                        investorModule.getDBTransaction().rollback();
                        FacesMessage msg =
                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                             "The operation can’t be completed. Unhandled error.",
                                             "The operation can’t be completed. Unhandled error.");
                        fctx.addMessage(null, msg);
                    }
                } else {
                    FacesMessage msg =
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                         "The operation can’t be completed. Maximum rows allowed " +
                                         MAX_ROWS,
                                         "The operation can’t be completed. Maximum rows allowed " +
                                         MAX_ROWS);
                    fctx.addMessage(null, msg);
                }
            }
        }
        return nextNavigate;
    }

    public void truncateValidationTmp() {
        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        ValidationTmpViewImpl validationTmpView =
            (ValidationTmpViewImpl)investorModule.getValidationTmpView1();
        validationTmpView.executeQuery();

        long numRows = validationTmpView.getEstimatedRowCount();

        if (numRows > 0) {
            Row row;
            while ((row = validationTmpView.next()) != null) {
                row.remove();
            }
            investorModule.getDBTransaction().commit();
        }
    }

    public void setUfExcel(UploadedFile ufExcel) {
        this.ufExcel = ufExcel;
    }

    public UploadedFile getUfExcel() {
        return ufExcel;
    }

    public void setRifExcel(RichInputFile rifExcel) {
        this.rifExcel = rifExcel;
    }

    public RichInputFile getRifExcel() {
        return rifExcel;
    }

    public void confirmData(ActionEvent actionEvent) {

    }

    public String confirmData() {
        String nextNavigate = "";
        boolean loopErr = false;
        boolean submitValStatus = false;

        InvestorModuleImpl investorModule =
            (InvestorModuleImpl)ADFUtils.getApplicationModuleForDataControl("InvestorModuleDataControl");

        FacesContext fctx = FacesContext.getCurrentInstance();

        ValidationTmpViewImpl validationTmp =
            (ValidationTmpViewImpl)investorModule.getValidationTmpView1();
        validationTmp.executeQuery();

        long rowCount = validationTmp.getEstimatedRowCount();

        if (rowCount > 0) {
            ValidationMsgViewImpl validationMsg =
                (ValidationMsgViewImpl)investorModule.getValidationMsgView1();
            // Insert first row
            ValidationTmpViewRowImpl validationTmpRow =
                (ValidationTmpViewRowImpl)validationTmp.first();

            submitValStatus =
                    submitValidationMsg(validationTmpRow.getParticipantid().toString(),
                                        validationTmpRow.getSidnumber().toString(),
                                        validationTmpRow.getAccountnumber().toString());
            if (submitValStatus) {
            } else {
                loopErr = true;
            }

            /* ORIGINAL ADF (1 of 2) */
            /*
            Row validationMsgRow = validationMsg.createRow();
            validationMsgRow.setAttribute("Batchref",
                                          validationTmpRow.getBatchref().toString());
            validationMsgRow.setAttribute("Detailref",
                                          validationTmpRow.getDetailref().toString());
            validationMsgRow.setAttribute("Participantid",
                                          validationTmpRow.getParticipantid().toString());
            validationMsgRow.setAttribute("Sidnumber",
                                          validationTmpRow.getSidnumber().toString());
            validationMsgRow.setAttribute("Accountnumber",
                                          validationTmpRow.getAccountnumber().toString());
            validationMsg.insertRow(validationMsgRow);
            */
            
            if (!loopErr) {
            // Insert rest rows
            while (validationTmp.hasNext()) {
                validationTmpRow =
                        (ValidationTmpViewRowImpl)validationTmp.next();
                submitValStatus =
                        submitValidationMsg(validationTmpRow.getParticipantid().toString(),
                                            validationTmpRow.getSidnumber().toString(),
                                            validationTmpRow.getAccountnumber().toString());
                if (submitValStatus) {
                } else {
                    loopErr = true;
                    break;
                }

                /* ORIGINAL ADF (2 of 2) */
                /*
                validationMsgRow = validationMsg.createRow();
                validationMsgRow.setAttribute("Batchref",
                                              validationTmpRow.getBatchref().toString());
                validationMsgRow.setAttribute("Detailref",
                                              validationTmpRow.getDetailref().toString());
                validationMsgRow.setAttribute("Participantid",
                                              validationTmpRow.getParticipantid().toString());
                validationMsgRow.setAttribute("Sidnumber",
                                              validationTmpRow.getSidnumber().toString());
                validationMsgRow.setAttribute("Accountnumber",
                                              validationTmpRow.getAccountnumber().toString());
                validationMsg.insertRow(validationMsgRow);
                */
            }
            }

            try {
                //investorModule.getDBTransaction().commit();
                nextNavigate = "reupload";

                if (!loopErr) {
                    FacesMessage msg =
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                         "Data successfully upload and confirmed.",
                                         "Data successfully upload and confirmed.");
                    fctx.addMessage(null, msg);
                } else {
                    FacesMessage msg =
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                         "Failed to confirm investor. Process terminated.",
                                         "Failed to confirm investor. Process terminated.");
                    fctx.addMessage(null, msg);
                }
            } catch (JboException ex) {
                //investorModule.getDBTransaction().rollback();
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

}
