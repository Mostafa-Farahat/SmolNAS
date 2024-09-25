package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.OwnerFromQuery;
import util.OwnerFromUrl;
import util.Verification;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/mkdir")
public class mkdirServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //verification
        OwnerFromQuery parser = new OwnerFromQuery();
        if(!Verification.isUserAuthorized(req,parser)){
            PrintWriter writer = resp.getWriter();
            writer.write("YOU ARE NOT AUTHORIZED TO MAKE CHANGES TO THIS DIRECTORY");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Path dataRoot = Paths.get(System.getenv("NAS_DATAROOT"));
        String relativePath = req.getParameter("path").replaceFirst("/SmolNAS/data/","");

        Path directoryAbsloutePath= dataRoot.resolve(relativePath);

        Files.createDirectory(directoryAbsloutePath.resolve(req.getParameter("dirName")));
        resp.sendRedirect("http://localhost:8080"+ req.getParameter("path"));
    }
}
