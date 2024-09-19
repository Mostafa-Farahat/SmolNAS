package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
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
        Path dataRoot = Paths.get("/home/mostafa/Desktop/SmolData/");
        String relativePath = req.getParameter("path").replaceFirst("/SmolNAS/data/","");

        Path directoryAbsloutePath= dataRoot.resolve(relativePath);

        Files.createDirectory(directoryAbsloutePath.resolve(req.getParameter("dirName")));
        resp.sendRedirect("http://localhost:8080"+ req.getParameter("path"));
    }
}
