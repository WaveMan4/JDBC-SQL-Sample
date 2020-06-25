/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DbAccessBHC;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Gilles
 */
public class QueryServlet extends HttpServlet {
   
    private final static String driver = "com.mysql.jdbc.Driver";
    private final static String user = "johncolter";
    private final static String password = "LetMeIn!";
    //private final static String url="jdbc:mysql://localhost:3306/";
    private final static String url="jdbc:mysql://web7.jhuep.com:3306/";
    private final static String db="class";
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String dateOfRequest = request.getParameter("dateInfo");
            String [] dateSections = dateOfRequest.split("-");

            String sqlDate;
            if (dateSections.length > 0) {
                int month = Integer.parseInt(dateSections[0]);
                int day = Integer.parseInt(dateSections[1]);
                int year = Integer.parseInt(dateSections[2]);
                if (month >= 10 && day >= 10  ) {
                    sqlDate = String.format("%d-%d-%d", year, month, day);        
                }
                else if (month < 10 && day < 10) { 
                    sqlDate = String.format("%d-%02d-%02d", year, month, day);   
                }
                else if (month < 10) { 
                    sqlDate = String.format("%d-%02d-%d", year, month, day); 
                }
                else if (day < 10) {
                    sqlDate = String.format("%d-%d-%02d", year, month, day); 
                }
                else {
                    sqlDate = String.format("%d-%d-%d", year, month, day);      
                }
                System.out.println(sqlDate);
            }
            else {
                return;
            }
            
            //Display opening HTML elements
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet QueryServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            
            try (Connection conn = DriverManager.getConnection(url + db +"?serverTimezone=US/Eastern", user, password);
                PreparedStatement testQuery = 
                    conn.prepareStatement
                        ("SELECT reservation.StartDay, class.locations.location, "
                                + "class.guides.First, class.guides.Last, "
                                + "reservation.First, reservation.Last \n" +
                        "FROM class.reservation, "
                                + "class.guides, "
                                + "class.locations\n" +
                        "WHERE reservation.guide = guides.idguides and "
                            + "reservation.location = locations.idlocations and "
                            + "StartDay >= " + "\"" + sqlDate + "\"")) {
                System.out.println("finished setting up try...");
                ResultSet rs = testQuery.executeQuery();
                System.out.println("finished ResultSet...");
                
                out.println(" <table border=\"1\">"
                            + "<tr> "
                                + "<th>StartDay</th>"
                                + "<th>Location</th>"
                                + "<th>Guide-FN</th>"
                                + "<th>Guide-LN</th>"
                                + "<th>Rsvn-FN</th>"
                                + "<th>Rsvn-LN</th>"
                            + "</tr>");
                

                //Write Table to html page
                System.out.println("StartDay" + "\t"
                                    + "Location" + "\t"
                                    + "Guide-First" + "\t"
                                    + "Guide-Last" + "\t"
                                    + "Reservation-First" + "\t"
                                    + "Reservation-Last");

                while (rs.next()) {
                    out.println
                    ("<tr>" 
                        + "<td>" + rs.getString("StartDay") + "</td>"
                        + "<td>"+ rs.getString("Location") + "</td>"
                        + "<td>"+ rs.getString("First") + "</td>"
                        + "<td>"+ rs.getString("Last") + "</td>"
                        + "<td>"+ rs.getString("guides.First") + "</td>" 
                        + "<td>"+ rs.getString("guides.Last") + "</td>"
                    + "</tr>");
                }
                out.println("</table>");
            } catch (SQLException sqle) {
                System.out.println(sqle.getMessage());
                out.println("Error: " + sqle.getMessage());
            }

            //Display closing HTML elements
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
