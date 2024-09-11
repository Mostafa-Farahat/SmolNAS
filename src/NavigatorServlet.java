import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.*;

@WebServlet("/data/*")
public class NavigatorServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //verification
        String directoryOwner = req.getRequestURI().split("/")[3];
        final PrintWriter writer = resp.getWriter();
        Connection con = null;
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/SmolNAS","root", "root");
            Cookie[] cookies= req.getCookies();
            String requestSessionID = null;

            for(Cookie cookie : cookies){
                if(cookie.getName().equals("accessToken")){
                    requestSessionID = cookie.getValue();
                    break;
                }
            }

            PreparedStatement getNameFromSessionID= con.prepareStatement("SELECT userName FROM sessions WHERE sessionID = ?");
            getNameFromSessionID.setString(1,requestSessionID);
            ResultSet result = getNameFromSessionID.executeQuery();

            if(!result.isBeforeFirst()){
                //if result set is empty then the token doesn't exist in db
                writer.write("you access token is invalid");
                System.out.println("your sesison ID is invalid");
            }else{
                result.next();
                if(result.getString(1).equals(directoryOwner)){
                    DirectoryLister lister = new DirectoryLister(Paths.get("/home/mostafa/Desktop/SmolData"), resp);
                    lister.listDirectory(req.getRequestURI());
                }else{
                    writer.write("YOU ARE NOT AUTHORIZED TO ACCESS THIS DIRECTORY");
                }
            }
        }catch(SQLException ex){
            System.out.println("error occured while accessing db" + ex.getMessage());
        }
    }
}
