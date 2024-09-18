package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;

import util.OwnerFromUrl;
import util.Verification;
import util.DirectoryLister;
@WebServlet("/data/*")
public class NavigatorServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final PrintWriter writer = resp.getWriter();
        final OwnerFromUrl parser = new OwnerFromUrl();
        if(Verification.isUserAuthorized(req,parser)){
            DirectoryLister lister = new DirectoryLister(Paths.get("/home/mostafa/Desktop/SmolData"), resp);
            lister.listDirectory(req.getRequestURI());
        }else{
            writer.write("YOU ARE NOT AUTHORIZED TO ACCESS THIS DIRECTORY");
        }
    }
}
