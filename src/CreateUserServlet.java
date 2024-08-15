import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;


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


        if(!pass.equals(passVerify)){
            writer.write("entries in password and verify password do not match please try again");
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }





    }
}
