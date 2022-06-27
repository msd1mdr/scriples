package app.bmri.scripless.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

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


public class StatementFileServlet extends HttpServlet {
    private static final long serialVersionUID = 5124385755277354522L;

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException,
                                                           IOException {
        Connection conn = null;
        String strPath = "";
        // Start resend with DB
        try {
            Context ctx = new InitialContext();
            DataSource ds = (DataSource)ctx.lookup("jdbc/ScriplessDS");
            conn = ds.getConnection();
            PreparedStatement statement =
                conn.prepareStatement("SELECT directory_path FROM dba_directories WHERE directory_name = 'STATEMENT_DIR'");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                strPath = rs.getString("directory_path");
                //System.out.println("DIR PATH: " + strPath);
            }

            String strFileName = request.getParameter("filename");
            String strFile = strPath + File.separator + strFileName;
            strFile = strFile.trim();

            File file = new File(strFile);

            if (file.exists()) {
                byte[] csv = FileUtils.readFileToByteArray(file);
                response.setContentType("text/csv");
                response.setHeader("Content-Disposition",
                                   "attachment; filename=" + strFileName);
                response.setContentLength(csv.length);

                ServletOutputStream out = response.getOutputStream();
                out.write(csv);
                out.flush();
                out.close();
            } else {
                response.setContentType("text/html");
                PrintWriter out = response.getWriter();
                out.write("<html><body>File Not Found</body></html>");
                out.flush();
                out.close();
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
    }
}
