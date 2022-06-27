package app.bmri.scripless.servlet;

import java.io.File;

import java.io.IOException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.sql.DataSource;

import org.apache.commons.io.FileUtils;

import java.util.ArrayList;
import java.util.List;

public class RetrieveFtpFileServlet extends HttpServlet {
    private static final long serialVersionUID = 5124385755277354522L;
    private static String inFile = "in";
    private static String outFile = "out";
    private static String localInDirSql =
        "SELECT PRM.VALUE1 FROM SI_ADMIN.PARAMETER PRM WHERE PRM.KODE='P27'";
    private static String localOutDirSql =
        "SELECT PRM.VALUE1 FROM SI_ADMIN.PARAMETER PRM WHERE PRM.KODE='P25'";

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,
                                                           IOException {
        Connection conn = null;
        List ftpConnParam = new ArrayList();
        String strDirPath = "";
        String strFilenm = request.getParameter("filenm");
        String strType = request.getParameter("type");

        if (strType.equals(inFile) || strType.equals(outFile)) {
            try {
                Context ctx = new InitialContext();
                DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
                conn = ds.getConnection();
                
                String localDirSql = strType.equalsIgnoreCase(inFile) ? localInDirSql : localOutDirSql;
                
                PreparedStatement ftpFileDir =
                    conn.prepareStatement(localDirSql);
                ResultSet rs = ftpFileDir.executeQuery();
                while (rs.next()) {
                    strDirPath = rs.getString("VALUE1");
                }

                String strFile = strDirPath + strFilenm;
                strFile = strFile.trim();

                File file = new File(strFile);

                if (file.exists()) {
                    byte[] fspFile = FileUtils.readFileToByteArray(file);
                    response.setContentType("text/csv");
                    response.setHeader("Content-Disposition",
                                       "attachment; filename=" +
                                       file.getName());
                    response.setContentLength(fspFile.length);

                    ServletOutputStream out = response.getOutputStream();
                    out.write(fspFile);
                    out.flush();
                    out.close();
                } else {
                    response.sendRedirect("../scripless/faces/filenotfound");
//                    response.setContentType("text/html");
//                    PrintWriter out = response.getWriter();
//                    out.write("<html><body>File Not Found</body></html>");
//                    out.flush();
//                    out.close();
                }
            } catch (SQLException sqle) {
                response.sendRedirect("../scripless/faces/error");
//                response.setContentType("text/html");
//                PrintWriter out = response.getWriter();
//                out.write("<html><body>"+ sqle.getLocalizedMessage() +"</body></html>");
//                out.flush();
//                out.close();
            } catch (NamingException ne) {
                response.sendRedirect("../scripless/faces/error");
//                response.setContentType("text/html");
//                PrintWriter out = response.getWriter();
//                out.write("<html><body>"+ ne.getLocalizedMessage() +"</body></html>");
//                out.flush();
//                out.close();
            } catch (Exception e) {
                response.sendRedirect("../scripless/faces/error");
//                response.setContentType("text/html");
//                PrintWriter out = response.getWriter();
//                out.write("<html><body>"+ e.getLocalizedMessage() +"</body></html>");
//                out.flush();
//                out.close();
            } finally {
                try {
                    if (conn != null) {
                        conn.close();
                    }
                } catch (SQLException sqle) {
                    response.sendRedirect("../scripless/faces/error");
//                    response.setContentType("text/html");
//                    PrintWriter out = response.getWriter();
//                    out.write("<html><body>"+ sqle.getLocalizedMessage() +"</body></html>");
//                    out.flush();
//                    out.close();
                }
            }
        } else {
            response.sendRedirect("../scripless/faces/wrongparam");
//            response.setContentType("text/html");
//            PrintWriter out = response.getWriter();
//            out.write("<html><body>Invalid type parameter value.</body></html>");
//            out.flush();
//            out.close();
        }
    }
}
