import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;


@WebServlet("/createUser")
public class CreateUserServlet extends HttpServlet {
@Override
public void init(ServletConfig config) throws ServletException {
    super.init(config);
}

@Override
public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    String userName = req.getParameter("userName");
    String  pass= req.getParameter("password");
    String  passVerify= req.getParameter("passwordVerify");
    PrintWriter writer = resp.getWriter();

    Connection con = null;
    try{
        con = ConnectionPool.getConnection();
        PreparedStatement checkDuplicate = con.prepareStatement("SELECT userName FROM userData WHERE userName= ?");
        checkDuplicate.setString(1, userName);

        ResultSet result = checkDuplicate.executeQuery();

        if(!pass.equals(passVerify)){
            writer.write("entries in password and verify password do not match please try again");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }else if(result.isBeforeFirst()){//the checkDuplicate returned a non-empty set
            writer.write("The user name specified exists please try a different user name");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }else {
            try{
                PreparedStatement insertUser = con.prepareStatement("INSERT INTO userData VALUES (?,?)");
                insertUser.setString(1,userName);
                insertUser.setString(2,HashGenerator.generateSHA256(pass));
                insertUser.executeUpdate();

                //TO DO: turn the data directory into a env var to be read
                // (nice for containerization)
                try{
                    Files.createDirectory(Paths.get("/home/mostafa/Desktop/SmolData/"+userName));
                }catch(IOException ex){
                    System.out.println("could not create user directory" + ex.getMessage());
                }

                writer.write("<div>User Added Successfully</div><a href=\"/SmolNAS/logIn.html\">Log In</a>");
                resp.setStatus(HttpServletResponse.SC_OK);
            }catch(SQLException ex){
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                writer.write("USER COULD NOT BE ADDED");
                System.out.println("could not add user:" + ex.getMessage());
            }
        }

    }catch(SQLException ex){
       System.out.println("shit an error occured" +"    "+ ex.getMessage());
    }finally {
        //TO DO: close other resources
        try{
            con.close();
        }catch(Exception ex){
            System.out.println("unable to close connection" + ex.getMessage());
        }
    }
    }
}
