package app.bmri.scripless.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

public class LaporanDataServlet extends HttpServlet {
    @SuppressWarnings("compatibility:-4845363715853989121")
    private static final long serialVersionUID = 5124385755277354522L;
    private static final int STATIC_INVESTOR_REPORT_URL = 21;
    private static final int STMT_INVESTOR_REPORT_URL = 22;
    private static final int VALID_INVESTOR_REPORT_URL = 23;
    private static final int STMT_BROKER_REPORT_URL = 24;
    private static final String TBL_STATIC = "STATIC_MSG";
    private static final String TBL_STMT = "STATEMENT_MSG";
    private static final String TBL_VALIDATION = "VALIDATION_MSG";

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,
                                                           IOException {

        // Start resend with DB
        try {
            String strName = request.getParameter("name");
            String tanggalAwal = request.getParameter("tanggalawal");
            String tanggalAkhir = request.getParameter("tanggalakhir");
            String type = request.getParameter("type");
            String kodeAb = request.getParameter("kodeab");
            String searchBy = request.getParameter("searchby");
            String searchValue = request.getParameter("searchvalue");

            if ("staticinvestor".equals(strName)) {
                if ("pdf".equals(type)) {
                    String strUrl = jsfConfigs(STATIC_INVESTOR_REPORT_URL);
                    strUrl = strUrl.replace("[TANGGAL_AWAL]", tanggalAwal);
                    strUrl = strUrl.replace("[TANGGAL_AKHIR]", tanggalAkhir);

                    PrintWriter printWriter = response.getWriter();
                    printWriter.write("<html><head><meta http-equiv=\"refresh\" content=\"0;url=" +
                                      strUrl + "\"></head></html>");
                    printWriter.flush();
                    printWriter.close();
                } else if ("csv".equals(type)) {
                    SimpleDateFormat df =
                        new SimpleDateFormat("yyyyMMddhhmmss");
                    String strFileName =
                        "static_investor_" + df.format(new Date());
                    response.setContentType("text/csv");
                    response.setHeader("Content-Disposition",
                                       "attachment; filename=" + strFileName +
                                       ".csv");

                    PrintWriter printWriter = response.getWriter();

                    // Print column name header
                    String tabelColumns =
                        (retrieveHeaderTableColumn(TBL_STATIC).toString().replace("[",
                                                                                  "")).replace("]",
                                                                                               "");
                    List<String> strHeader =
                        retrieveHeaderTableColumn(TBL_STATIC);

                    for (int i = 0; i < strHeader.size(); i++) {
                        printWriter.print(strHeader.get(i));
                        if (i < strHeader.size()) {
                            printWriter.print(",");
                        }
                    }

                    printWriter.println("");

                    // Print row datas
                    List<String> strDatas =
                        retrieveStaticData(tabelColumns, tanggalAwal,
                                           tanggalAkhir);

                    for (int i = 0; i < strDatas.size(); i++) {
                        printWriter.print(strDatas.get(i));
                        if (i < strDatas.size()) {
                            printWriter.println("");
                        }
                    }

                    printWriter.flush();
                    printWriter.close();
                }
            } else if ("statementinvestor".equals(strName)) {
                if ("pdf".equals(type)) {
                    String strUrl = jsfConfigs(STMT_INVESTOR_REPORT_URL);
                    strUrl = strUrl.replace("[TANGGAL_AWAL]", tanggalAwal);
                    strUrl = strUrl.replace("[TANGGAL_AKHIR]", tanggalAkhir);

                    PrintWriter printWriter = response.getWriter();
                    printWriter.write("<html><head><meta http-equiv=\"refresh\" content=\"0;url=" +
                                      strUrl + "\"></head></html>");
                    printWriter.flush();
                    printWriter.close();
                } else if ("csv".equals(type)) {
                    SimpleDateFormat df =
                        new SimpleDateFormat("yyyyMMddhhmmss");
                    String strFileName =
                        "statement_investor_" + df.format(new Date());
                    response.setContentType("text/csv");
                    response.setHeader("Content-Disposition",
                                       "attachment; filename=" + strFileName +
                                       ".csv");

                    PrintWriter printWriter = response.getWriter();

                    // Print column name header
                    String tabelColumns =
                        (retrieveHeaderTableColumn(TBL_STMT).toString().replace("[",
                                                                                "")).replace("]",
                                                                                             "");
                    List<String> strHeader =
                        retrieveHeaderTableColumn(TBL_STMT);

                    for (int i = 0; i < strHeader.size(); i++) {
                        printWriter.print(strHeader.get(i));
                        if (i < strHeader.size()) {
                            printWriter.print(",");
                        }
                    }

                    printWriter.println("");

                    // Print row datas
                    List<String> strDatas =
                        retrieveStatementData(tabelColumns, tanggalAwal,
                                              tanggalAkhir);

                    for (int i = 0; i < strDatas.size(); i++) {
                        printWriter.print(strDatas.get(i));
                        if (i < strDatas.size()) {
                            printWriter.println("");
                        }
                    }

                    printWriter.flush();
                    printWriter.close();
                }
            } else if ("validationinvestor".equals(strName)) {
                if ("pdf".equals(type)) {
                    String strUrl = jsfConfigs(VALID_INVESTOR_REPORT_URL);
                    strUrl = strUrl.replace("[TANGGAL_AWAL]", tanggalAwal);
                    strUrl = strUrl.replace("[TANGGAL_AKHIR]", tanggalAkhir);

                    PrintWriter printWriter = response.getWriter();
                    printWriter.write("<html><head><meta http-equiv=\"refresh\" content=\"0;url=" +
                                      strUrl + "\"></head></html>");
                    printWriter.flush();
                    printWriter.close();
                } else if ("csv".equals(type)) {
                    SimpleDateFormat df =
                        new SimpleDateFormat("yyyyMMddhhmmss");
                    String strFileName =
                        "validation_investor_" + df.format(new Date());
                    response.setContentType("text/csv");
                    response.setHeader("Content-Disposition",
                                       "attachment; filename=" + strFileName +
                                       ".csv");

                    PrintWriter printWriter = response.getWriter();

                    // Print column name header
                    String tabelColumns =
                        (retrieveHeaderTableColumn(TBL_VALIDATION).toString().replace("[",
                                                                                      "")).replace("]",
                                                                                                   "");
                    List<String> strHeader =
                        retrieveHeaderTableColumn(TBL_VALIDATION);

                    for (int i = 0; i < strHeader.size(); i++) {
                        printWriter.print(strHeader.get(i));
                        if (i < strHeader.size()) {
                            printWriter.print(",");
                        }
                    }

                    printWriter.println("");

                    // Print row datas
                    List<String> strDatas =
                        retrieveValidationData(tabelColumns, tanggalAwal,
                                               tanggalAkhir);

                    for (int i = 0; i < strDatas.size(); i++) {
                        printWriter.print(strDatas.get(i));
                        if (i < strDatas.size()) {
                            printWriter.println("");
                        }
                    }

                    printWriter.flush();
                    printWriter.close();
                }
            } else if ("statementbroker".equals(strName)) {
                if ("pdf".equals(type)) {
                    String strUrl = jsfConfigs(STMT_BROKER_REPORT_URL);
                    strUrl = strUrl.replace("[TANGGAL_AWAL]", tanggalAwal);
                    strUrl = strUrl.replace("[TANGGAL_AKHIR]", tanggalAkhir);
                    strUrl = strUrl.replace("[KODE_AB]", kodeAb);

                    PrintWriter printWriter = response.getWriter();
                    printWriter.write("<html><head><meta http-equiv=\"refresh\" content=\"0;url=" +
                                      strUrl + "\"></head></html>");
                } else if ("csv".equals(type)) {
                }
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public String jsfConfigs(int configId) {
        Connection conn = null;
        String strPath = "";

        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            PreparedStatement statement =
                conn.prepareStatement("SELECT JC.CONFIG_VALUE FROM JSF_CONFIGS JC WHERE JC.CONFIG_ID = ?");
            statement.setInt(1, configId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                strPath = rs.getString("CONFIG_VALUE");
                //System.out.println("CONFIG VALUE: " + strPath);
            }
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        } catch (NamingException ne) {
            // TODO: Add catch code
            ne.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return strPath;
    }

    private List<String> retrieveHeaderTableColumn(String tblName) {
        Connection conn = null;
        String columnName = "";
        List<String> strFields = new ArrayList<String>();
        ;

        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            PreparedStatement statement =
                conn.prepareStatement("SELECT COLUMN_NAME FROM USER_TAB_COLUMNS WHERE TABLE_NAME = ? ORDER BY COLUMN_ID");
            statement.setString(1, tblName);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                columnName = rs.getString("COLUMN_NAME");
                strFields.add(columnName);
            }
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        } catch (NamingException ne) {
            // TODO: Add catch code
            ne.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        return strFields;
    }

    private List<String> retrieveStaticData(String tblColumns,
                                            String startDate, String endDate) {
        Connection conn = null;
        String rowData = "";
        List<String> strDatas = new ArrayList<String>();

        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            PreparedStatement statement =
                conn.prepareStatement("SELECT " + tblColumns +
                                      " FROM STATIC_MSG WHERE TRUNC(CREATE_TIME) BETWEEN TO_DATE('" +
                                      startDate +
                                      "','YYYY-MM-DD') AND TO_DATE('" +
                                      endDate + "','YYYY-MM-DD')");
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colNum = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= colNum; i++) {
                    if (i == 1) {
                        rowData = rs.getString(i);
                    } else if (i == colNum) {
                        rowData = rowData + "," + rs.getString(i) + ",";
                    } else {
                        rowData = rowData + "," + rs.getString(i);
                    }
                }
                strDatas.add(rowData);
            }
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        } catch (NamingException ne) {
            // TODO: Add catch code
            ne.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        return strDatas;
    }

    private List<String> retrieveStatementData(String tblColumns,
                                               String startDate,
                                               String endDate) {
        Connection conn = null;
        String rowData = "";
        List<String> strDatas = new ArrayList<String>();

        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            PreparedStatement statement =
                conn.prepareStatement("SELECT " + tblColumns +
                                      " FROM STATEMENT_MSG WHERE TRUNC(CREATE_TIME) BETWEEN TO_DATE('" +
                                      startDate +
                                      "','YYYY-MM-DD') AND TO_DATE('" +
                                      endDate + "','YYYY-MM-DD')");
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colNum = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= colNum; i++) {
                    if (i == 1) {
                        rowData = rs.getString(i);
                    } else if (i == colNum) {
                        rowData = rowData + "," + rs.getString(i) + ",";
                    } else {
                        rowData = rowData + "," + rs.getString(i);
                    }
                }
                strDatas.add(rowData);
            }
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        } catch (NamingException ne) {
            // TODO: Add catch code
            ne.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        return strDatas;
    }

    private List<String> retrieveValidationData(String tblColumns,
                                                String startDate,
                                                String endDate) {
        Connection conn = null;
        String rowData = "";
        List<String> strDatas = new ArrayList<String>();

        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            PreparedStatement statement =
                conn.prepareStatement("SELECT " + tblColumns +
                                      " FROM VALIDATION_MSG WHERE TRUNC(CREATE_TIME) BETWEEN TO_DATE('" +
                                      startDate +
                                      "','YYYY-MM-DD') AND TO_DATE('" +
                                      endDate + "','YYYY-MM-DD')");
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int colNum = rsmd.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= colNum; i++) {
                    if (i == 1) {
                        rowData = rs.getString(i);
                    } else if (i == colNum) {
                        rowData = rowData + "," + rs.getString(i) + ",";
                    } else {
                        rowData = rowData + "," + rs.getString(i);
                    }
                }
                strDatas.add(rowData);
            }
        } catch (SQLException sqle) {
            // TODO: Add catch code
            sqle.printStackTrace();
        } catch (NamingException ne) {
            // TODO: Add catch code
            ne.printStackTrace();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }

        return strDatas;
    }
}
