import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.sql.*;
import java.util.UUID;

@WebServlet("/login")
public class LogInServlet extends HttpServlet {

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userName = req.getParameter("username");
        String pass = HashGenerator.generateSHA256(req.getParameter("password"));
        Writer writer = resp.getWriter();

        Connection connection = null;
        try{
            connection = ConnectionPool.getConnection();
            PreparedStatement getUser = connection.prepareStatement("SELECT userName,password FROM userData WHERE userName = ?");
            getUser.setString(1,userName);
            ResultSet result = getUser.executeQuery();



            if(!result.next() || !pass.equals(result.getString(2))){//the result set is empty (non existing user)|| password not correct
               writer.write("the user name or password is incorrect");
            }else{//create session token using UUID, add it to db, return it as cookie to client
                PreparedStatement getSessionData = connection.prepareStatement("SELECT sessionID FROM sessions WHERE userName = ?");
                getSessionData.setString(1,userName);
                ResultSet sessionsResult = getSessionData.executeQuery();
                UUID accessToken = null;

                if(sessionsResult.isBeforeFirst()){//there is already a sessionId for user
                    sessionsResult.next();
                    accessToken = UUID.fromString(sessionsResult.getString(1));
                }else{//no sessionID found
                    accessToken = UUID.randomUUID();
                    PreparedStatement createSession = connection.prepareStatement("INSERT INTO sessions VALUES (?,?)");
                    createSession.setString(1, accessToken.toString());
                    createSession.setString(2, userName);
                    createSession.executeUpdate();
                }

                resp.addCookie(new Cookie("accessToken", accessToken.toString()));
                resp.sendRedirect("/SmolNAS/data/"+userName);

                //TO DO: exception handling
            }


        }catch(SQLException ex){
            System.out.println("an error has occured: " + ex.getMessage());
        }finally {
            //TO DO: close rest of resources
            try{
                connection.close();
            }catch(SQLException ex){
                System.out.println("cannot close connection: "+ ex.getMessage());
            }
        }
    }
}
