package app.bmri.scripless.backing.sendfilesftp;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;

import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;

import java.io.InputStreamReader;
import java.io.Reader;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


import javax.sql.DataSource;

import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.input.RichInputDate;
import oracle.adf.view.rich.component.rich.input.RichInputFile;

import oracle.adf.view.rich.component.rich.input.RichSelectOneRadio;

import oracle.jbo.format.DefaultDateFormatter;
import oracle.jbo.format.FormatErrorException;

import org.apache.myfaces.trinidad.model.UploadedFile;

public class SendFileSftpKsei {

    private UploadedFile uFile;
    private RichInputFile riFile;
    private RichInputDate inpTanggal;
    private RichSelectOneRadio inpJenisMsg;
    private static String jnsMsgStatic = "STATIC";
    private static String jnsMsgStatement = "STATEMENT";
    private static String jnsMsgBalance = "BALANCE";
    private static String filePrefixStatic = "DataStaticInv_BMAN2_";
    private static String filePrefixStatement = "RecActStmt_BMAN2_";
    private static String filePrefixBalance = "RecBalance_BMAN2_";
    private static String ftRecipient = "KSEI";
    private static String ftSendMethod = "FTP";
    private static String statusSuccess = "SUCCESS";
    private static String statusError = "ERROR";
    private static String ftSendErrorMsg = "Gagal kirim FTP";
    private static int validTglLen = 8;
    private static String sqlMaxDayCtr =
        "SELECT MAX(DAILY_COUNTER) + 1 AS DAYCTR FROM SI_ADMIN.FILE_TRANSMISION FT WHERE FT.SUBMODUL = ? AND FT.VALDATE = ?";
    private static String sqlMaxFtId = 
        "SELECT NVL(MAX(FT.ID) + 1, 1) AS FT_ID FROM SI_ADMIN.FILE_TRANSMISION FT";
    private static String sqlMaxFtRetry = 
        "SELECT NVL(MAX(FT.RETRY) + 1, 1) AS FT_RETRY FROM SI_ADMIN.FILE_TRANSMISION FT WHERE FT.RECIPIENT = ? AND FT.SUBMODUL = ? AND FT.VALDATE = ? AND SEND_STATUS = 'ERROR'";
    private static String ftpParamSql =
        "SELECT PRM.VALUE1 FROM SI_ADMIN.PARAMETER PRM WHERE PRM.KODE IN ('P21','P22','P23', 'P24', 'P25') ORDER BY PRM.KODE";
    private static String sqlInsFileTransmision =
        "INSERT INTO SI_ADMIN.FILE_TRANSMISION (ID, FILENAME, DAILY_COUNTER, SEND_METHOD, RECIPIENT, SUBMODUL, VALDATE, SEND_STATUS, RETRY, ERRORMSG, SEND_TIME, RECORD_NUMBER) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public SendFileSftpKsei() {
        super();
    }

    public String sftpSend() throws IOException {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ADFContext adfCtx = ADFContext.getCurrent();
        Map pageFlowScope = adfCtx.getPageFlowScope();
        List ftpConnParam = new ArrayList();
        String sftpHost = "";
        String sftpPort = "22";
        String sftpUser = "";
        String sftpPasw = "";
        String sftpRemoteDir = "";
        String sftpLocalDir = "";
        String nextNavigate = "";
        DefaultDateFormatter ddf = new DefaultDateFormatter();
        String tanggal = "";
        String jnsMsg = "";
        Connection conn = null;
        int dayCtr = 0;
        String dayCtrStr = "";
        String newFileName = "";
        int lines = 0;
        int fileSize = 0;
        
        // Initial pageflowscope message
        pageFlowScope.put("sendStatus", statusSuccess);
        pageFlowScope.put("errMsg", "");

        try {
            tanggal = ddf.format("yyyy-MM-dd", inpTanggal.getValue());
        } catch (FormatErrorException e) {
            System.out.println("ERR FMT EXC: " + e.getLocalizedMessage());
            tanggal = "";
        } catch (Exception e) {
            System.out.println("ERR EXC: " + e.getLocalizedMessage());
            tanggal = "";
        }

        jnsMsg = inpJenisMsg.getValue().toString();
        tanggal = tanggal.replace("-", "");

        if (tanggal.length() == validTglLen) {
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();

                //Get SFTP connection parameter
                try {
                    PreparedStatement getFtpParam =
                        conn.prepareStatement(ftpParamSql);
                    ResultSet rs = getFtpParam.executeQuery();
                    while (rs.next()) {
                        ftpConnParam.add(rs.getString("VALUE1"));
                    }
                } catch (SQLException sqle) {
                    pageFlowScope.put("sendStatus", statusError);
                    pageFlowScope.put("errMsg", sqle.getLocalizedMessage());
                    
//                    FacesMessage msg =
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                         sqle.getLocalizedMessage(),
//                                         sqle.getLocalizedMessage());
//                    fctx.addMessage(null, msg);
                }

                if (ftpConnParam.size() == 5) {
                    sftpHost = ftpConnParam.get(0).toString();
                    sftpUser = ftpConnParam.get(1).toString();
                    sftpPasw = ftpConnParam.get(2).toString();
                    sftpRemoteDir = ftpConnParam.get(3).toString();
                    sftpLocalDir = ftpConnParam.get(4).toString();

                    PreparedStatement getDailyCounter =
                        conn.prepareStatement(sqlMaxDayCtr);
                    getDailyCounter.setString(1, jnsMsg);
                    getDailyCounter.setString(2, tanggal);
                    ResultSet rs = getDailyCounter.executeQuery();
                    while (rs.next()) {
                        dayCtr =
                                rs.getInt("DAYCTR") == 0 ? 1 : rs.getInt("DAYCTR");
                    }
                    dayCtrStr = "0" + dayCtr;

                    //Renaming the file
                    if (jnsMsg.equals(jnsMsgStatic)) {
                        newFileName =
                                filePrefixStatic + tanggal + "_" + dayCtrStr +
                                ".fsp";
                    } else if (jnsMsg.equals(jnsMsgStatement)) {
                        newFileName =
                                filePrefixStatement + tanggal + "_" + dayCtrStr +
                                ".fsp";
                    } else if (jnsMsg.equals(jnsMsgBalance)) {
                        newFileName =
                                filePrefixBalance + tanggal + "_" + dayCtrStr +
                                ".fsp";
                    } else {
                        pageFlowScope.put("sendStatus", statusError);
                        pageFlowScope.put("errMsg", "Format tanggal invalid.");
                        
//                        FacesMessage msg =
//                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                             "Format tanggal invalid.",
//                                             "Format tanggal invalid.");
//                        fctx.addMessage(null, msg);
                    }

                    //Upload to local server directory
                    //Check file if already choose
                    InputStream is = null;
                    try {
                        is = this.getUFile().getInputStream();
                        fileSize = is.available();
                    } catch (Exception ex) {                            
                        pageFlowScope.put("sendStatus", statusError);
                        pageFlowScope.put("errMsg", "The operation can’t be completed because the file can't be found.");
                            
//                        FacesMessage msg =
//                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                             "The operation can’t be completed because the file can't be found.",
//                                             "The operation can’t be completed because the file can't be found.");
//                        fctx.addMessage(null, msg);
                    }
                    
                    if (fileSize > 0) {
                        Reader reader = new InputStreamReader(is);
                        BufferedReader br = new BufferedReader(reader);
                        while (br.readLine() != null) lines++;
                        reader.close();
                        
                        //Upload file to local server directory
                        String localFilePath =
                            uploadFile(this.getUFile(), sftpLocalDir, newFileName,
                                       fctx, pageFlowScope);
                        
                        if (!localFilePath.isEmpty() && localFilePath.equalsIgnoreCase(statusSuccess)) {
                            //Upload file to KSEI through SFTP
    
                            String remoteFilePath =
                                upload(newFileName, sftpHost, sftpPort, sftpUser,
                                       sftpPasw, sftpRemoteDir, sftpLocalDir,
                                       fctx, pageFlowScope);
    
                            if (!remoteFilePath.isEmpty() && remoteFilePath.equalsIgnoreCase(statusSuccess)) {
                                insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, null, statusSuccess, pageFlowScope, dayCtr, lines);
    
                                // Set Into Page Flow Scope
                                pageFlowScope.put("sendStatus", statusSuccess);
                                
                                nextNavigate = "sendkonfirm";
    //                            FacesMessage msg =
    //                                new FacesMessage(FacesMessage.SEVERITY_INFO,
    //                                                 "The send to KSEI upload operation success.",
    //                                                 "The send to KSEI upload operation success.");
    //                            fctx.addMessage(null, msg);
                            } else {
                                insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, ftSendErrorMsg, statusError, pageFlowScope, dayCtr, lines);
    
                                // Set Into Page Flow Scope
                                pageFlowScope.put("sendStatus", statusError);
    //                            pageFlowScope.put("errMsg", "The send to KSEI upload operation failed."); --> message assigned from method
                                
                                nextNavigate = "sendkonfirm";
    //                            FacesMessage msg =
    //                                new FacesMessage(FacesMessage.SEVERITY_ERROR,
    //                                                 "The send to KSEI upload operation failed.",
    //                                                 "The send to KSEI upload operation failed.");
    //                            fctx.addMessage(null, msg);
                            }
                        } else {
                            insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, ftSendErrorMsg, statusError, pageFlowScope, dayCtr, lines);
    
                            // Set Into Page Flow Scope
                            pageFlowScope.put("sendStatus", statusError);
    //                        pageFlowScope.put("errMsg", "The server file upload operation failed."); --> message assigned from module
                            
                            nextNavigate = "sendkonfirm";
    //                        FacesMessage msg =
    //                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
    //                                             "The server file upload operation failed.",
    //                                             "The server file upload operation failed.");
    //                        fctx.addMessage(null, msg);
                        }
                    } else {                    
                        insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, ftSendErrorMsg, statusError, pageFlowScope, dayCtr, lines);

                        // Set Into Page Flow Scope
                        pageFlowScope.put("sendStatus", statusError);
                        pageFlowScope.put("errMsg", "File yang anda upload terdeteksi kosong.");
                        
                        nextNavigate = "sendkonfirm";
                    }    
                } else {
                    insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, ftSendErrorMsg, statusError, pageFlowScope, dayCtr, lines);

                    // Set Into Page Flow Scope
                    pageFlowScope.put("sendStatus", statusError);
                    pageFlowScope.put("errMsg", "Parameter koneksi SFTP masih ada yang kurang.");
                    
                    nextNavigate = "sendkonfirm";
//                    FacesMessage msg =
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                         "Parameter koneksi SFTP masih ada yang kurang.",
//                                         "Parameter koneksi SFTP masih ada yang kurang");
//                    fctx.addMessage(null, msg);
                }

            } catch (SQLException sqle) {
                insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, ftSendErrorMsg, statusError, pageFlowScope, dayCtr, lines);

                // Set Into Page Flow Scope
                pageFlowScope.put("sendStatus", statusError);
                pageFlowScope.put("errMsg", sqle.getLocalizedMessage());
                
                nextNavigate = "sendkonfirm";
//                FacesMessage msg =
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                     sqle.getLocalizedMessage(),
//                                     sqle.getLocalizedMessage());
//                fctx.addMessage(null, msg);
            } catch (NamingException ne) {
                insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, ftSendErrorMsg, statusError, pageFlowScope, dayCtr, lines);

                // Set Into Page Flow Scope
                pageFlowScope.put("sendStatus", statusError);
                pageFlowScope.put("errMsg", ne.getLocalizedMessage());
                
                nextNavigate = "sendkonfirm";
//                FacesMessage msg =
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                     ne.getLocalizedMessage(),
//                                     ne.getLocalizedMessage());
//                fctx.addMessage(null, msg);
            } catch (Exception e) {
                insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, ftSendErrorMsg, statusError, pageFlowScope, dayCtr, lines);

                // Set Into Page Flow Scope
                pageFlowScope.put("sendStatus", statusError);
                pageFlowScope.put("errMsg", e.getLocalizedMessage());
                
                nextNavigate = "sendkonfirm";
//                FacesMessage msg =
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                     e.getLocalizedMessage(),
//                                     e.getLocalizedMessage());
//                fctx.addMessage(null, msg);
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException sqle) {
                    insFileTransmision(conn, fctx, newFileName, ftRecipient, jnsMsg, tanggal, ftSendMethod, ftSendErrorMsg, statusError, pageFlowScope, dayCtr, lines);

                    // Set Into Page Flow Scope
                    pageFlowScope.put("sendStatus", statusError);
                    pageFlowScope.put("errMsg", sqle.getLocalizedMessage());
                    
                    nextNavigate = "sendkonfirm";
//                    FacesMessage msg =
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                         sqle.getLocalizedMessage(),
//                                         sqle.getLocalizedMessage());
//                    fctx.addMessage(null, msg);
                }
            }
        } else {                    
            nextNavigate = "sendkonfirm";

            // Set Into Page Flow Scope
            pageFlowScope.put("sendStatus", statusError);
            pageFlowScope.put("errMsg", "Tanggal tidak sesuai dengan panjang karakter.");
            
            FacesMessage msg =
                new FacesMessage(FacesMessage.SEVERITY_ERROR, "Tanggal tidak sesuai dengan panjang karakter.",
                                 "Tanggal tidak sesuai dengan panjang karakter.");
            fctx.addMessage(null, msg);
        }

        return nextNavigate;
    }

    public void setUFile(UploadedFile uFile) {
        this.uFile = uFile;
    }

    public UploadedFile getUFile() {
        return uFile;
    }

    public void setRiFile(RichInputFile riFile) {
        this.riFile = riFile;
    }

    public RichInputFile getRiFile() {
        return riFile;
    }

    public void setInpTanggal(RichInputDate inpTanggal) {
        this.inpTanggal = inpTanggal;
        if (this.inpTanggal.getValue() == null ||
            this.inpTanggal.getValue().toString().equals("")) {
            this.inpTanggal.setValue(new Date());
        }
    }

    public RichInputDate getInpTanggal() {
        return inpTanggal;
    }

    public void setInpJenisMsg(RichSelectOneRadio inpJenisMsg) {
        this.inpJenisMsg = inpJenisMsg;
    }

    public RichSelectOneRadio getInpJenisMsg() {
        return inpJenisMsg;
    }

    public String upload(String fileName, String sftpHost, String sftpPort,
                         String sftpUser, String sftpPasw,
                         String sftpRemoteDir, String sftpLocalDir,
                         FacesContext fctx, Map pageFlowScope) {

        JSch jsch = new JSch();
        Session sessionJsch = null;
        String uploadStatus = statusSuccess;
        
        try {
            sessionJsch = jsch.getSession(sftpUser, sftpHost);
            sessionJsch.setConfig("StrictHostKeyChecking", "no");
            sessionJsch.setPassword(sftpPasw);
            sessionJsch.connect();
            Channel channel = sessionJsch.openChannel("sftp");
            channel.connect();
            ChannelSftp sftpChannel = (ChannelSftp)channel;
            
            //Check is directory already exists
            SftpATTRS attrs=null;
            try {
                attrs = sftpChannel.stat(sftpRemoteDir);
            } catch (Exception e) {
                uploadStatus = statusError;
                pageFlowScope.put("errMsg", "Remote directory does not exists.");
            }

            if (attrs != null) {
                // Directory exists then upload file through SFTP
                File f1 = new File(sftpLocalDir + fileName);            
                sftpChannel.chmod(Integer.parseInt("777",8), sftpRemoteDir);
                sftpChannel.cd(sftpRemoteDir);
                sftpChannel.put(new FileInputStream(f1), f1.getName());
                
                uploadStatus = statusSuccess;
                pageFlowScope.put("errMsg", "");
            }
            
            // Close connection
            sftpChannel.exit();
            sessionJsch.disconnect();
        } catch (Exception ex) {
            uploadStatus = statusError;
            pageFlowScope.put("errMsg", ex.getLocalizedMessage());
//            FacesMessage msg =
//                new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getLocalizedMessage(),
//                                 ex.getLocalizedMessage());
//            fctx.addMessage(null, msg);
        }

        return uploadStatus;
    }

    private String uploadFile(UploadedFile file, String localDir,
                              String newFileName, FacesContext fctx, Map pageFlowScope) {
        UploadedFile myfile = file;
        String path = null;
        String uploadStatus = statusSuccess;
        
        if (myfile == null) {
            uploadStatus = statusError;
            pageFlowScope.put("errMsg", "No file uploaded.");
        } else {
            // All uploaded files will be stored in below path
            //            path = localDir + myfile.getFilename();
            path = localDir + newFileName;
            
            // Check permission to write         
            File locDir = new File(localDir);   
            
            if (locDir.canWrite()) {            
                // Upload file to server                
                InputStream inputStream = null;
                try {
                    FileOutputStream out = new FileOutputStream(path);
                    inputStream = myfile.getInputStream();
                    byte[] buffer = new byte[8192];
                    int bytesRead = 0;
                    while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    out.flush();
                    out.close();
                    
                    uploadStatus = statusSuccess;
                    pageFlowScope.put("errMsg", "");                    
                } catch (Exception ex) {
                    uploadStatus = statusError;
                    pageFlowScope.put("errMsg", ex.getLocalizedMessage());
                    
//                    FacesMessage msg =
//                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                         ex.getLocalizedMessage(),
//                                         ex.getLocalizedMessage());
//                    fctx.addMessage(null, msg);
                } finally {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        uploadStatus = statusError;
                        pageFlowScope.put("errMsg", e.getLocalizedMessage());
                        
//                        FacesMessage msg =
//                            new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                             e.getLocalizedMessage(),
//                                             e.getLocalizedMessage());
//                        fctx.addMessage(null, msg);
                    }
                }
            } else {
                uploadStatus = statusError;
                pageFlowScope.put("errMsg", "Does not have permission to write to local folder");
//                FacesMessage msg =
//                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Does not have permission to write to local folder",
//                                     "Does not have permission to write to local folder");
//                fctx.addMessage(null, msg);
            }
        }

        //Returns the path where file is stored
        return uploadStatus;
    }
    
    private void insFileTransmision(Connection conn, FacesContext fctx, String fileName, String recipient, String jnsMsg, String valDate, String sendMethod, String errMsg, String sendStatus, Map pageFlowScope, int dayCtr, int recordNumber) {
        //Get Max Id
        int ftId = getMaxId(conn, fctx, pageFlowScope);
        
        //Get Max Retry
        int ftRetry = getMaxRetry(conn, fctx, fileName, recipient, jnsMsg, valDate, pageFlowScope);
        
        //Get SFTP connection parameter
        try {
            PreparedStatement execInsFileTransmision =
                conn.prepareStatement(sqlInsFileTransmision);
            execInsFileTransmision.setInt(1, ftId);
            execInsFileTransmision.setString(2, fileName);
            execInsFileTransmision.setInt(3, dayCtr);
            execInsFileTransmision.setString(4, sendMethod);
            execInsFileTransmision.setString(5, recipient);
            execInsFileTransmision.setString(6, jnsMsg);
            execInsFileTransmision.setString(7, valDate);
            execInsFileTransmision.setString(8, sendStatus);
            if (ftRetry == 1 && sendStatus.equals(statusSuccess)) {
                execInsFileTransmision.setString(9, null); 
            } else {
                execInsFileTransmision.setInt(9, ftRetry); 
            }
            execInsFileTransmision.setString(10, errMsg); //Error Message
            execInsFileTransmision.setDate(11, new java.sql.Date(System.currentTimeMillis()));
            execInsFileTransmision.setInt(12, recordNumber);
            execInsFileTransmision.executeUpdate();            
        } catch (SQLException sqle) {
            pageFlowScope.put("errMsg", sqle.getLocalizedMessage());
//            FacesMessage msg =
//                new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                 sqle.getLocalizedMessage(),
//                                 sqle.getLocalizedMessage());
//            fctx.addMessage(null, msg);
        }
    }
    
    private int getMaxId(Connection conn, FacesContext fctx, Map pageFlowScope) {
        int maxId = 0;
        //Get max id file transmision sequence
        try {
            PreparedStatement execMaxFtId =
                conn.prepareStatement(sqlMaxFtId);
            ResultSet rs = execMaxFtId.executeQuery();
            while (rs.next()) {
                maxId =
                        rs.getInt("FT_ID") == 0 ? 0 : rs.getInt("FT_ID");
            }       
        } catch (SQLException sqle) {
            pageFlowScope.put("errMsg", sqle.getLocalizedMessage());
//            FacesMessage msg =
//                new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                 sqle.getLocalizedMessage(),
//                                 sqle.getLocalizedMessage());
//            fctx.addMessage(null, msg);
        }
        return maxId;
    }

    
    private int getDailyCtr(Connection conn, FacesContext fctx, Map pageFlowScope) {
        int maxDayCtr = 0;
        //Get max daily counter
        try {
            PreparedStatement execMaxFtId =
                conn.prepareStatement(sqlMaxFtId);
            ResultSet rs = execMaxFtId.executeQuery();
            while (rs.next()) {
                maxDayCtr =
                        rs.getInt("FT_ID") == 0 ? 0 : rs.getInt("FT_ID");
            }       
        } catch (SQLException sqle) {
            pageFlowScope.put("errMsg", sqle.getLocalizedMessage());
    //            FacesMessage msg =
    //                new FacesMessage(FacesMessage.SEVERITY_ERROR,
    //                                 sqle.getLocalizedMessage(),
    //                                 sqle.getLocalizedMessage());
    //            fctx.addMessage(null, msg);
        }
        return maxDayCtr;
    }
    
    private int getMaxRetry(Connection conn, FacesContext fctx, String fileName, String recipient, String subModule, String valDate, Map pageFlowScope) {
        int maxRetry = 0;
        //Get max id file transmision sequence
        try {
            PreparedStatement execMaxFtRetry =
                conn.prepareStatement(sqlMaxFtRetry);
            execMaxFtRetry.setString(1, recipient);
            execMaxFtRetry.setString(2, subModule);
            execMaxFtRetry.setString(3, valDate);
            ResultSet rs = execMaxFtRetry.executeQuery();
            while (rs.next()) {
                maxRetry =
                        rs.getInt("FT_RETRY") == 0 ? 1 : rs.getInt("FT_RETRY");
            }       
        } catch (SQLException sqle) {
            pageFlowScope.put("errMsg", sqle.getLocalizedMessage());
//            FacesMessage msg =
//                new FacesMessage(FacesMessage.SEVERITY_ERROR,
//                                 sqle.getLocalizedMessage(),
//                                 sqle.getLocalizedMessage());
//            fctx.addMessage(null, msg);
        }
        return maxRetry;
    }
}
