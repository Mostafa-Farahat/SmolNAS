package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.OwnerFromQuery;
import util.Verification;

import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final OwnerFromQuery parser = new OwnerFromQuery();

        //verification
        if(!Verification.isUserAuthorized(req,parser)){//if not authorized yeet request
            PrintWriter writer = resp.getWriter();
            writer.write("YOU ARE NOT AUTHORIZED TO ACCESS THIS DIRECTORY");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if(Files.isDirectory(Paths.get(req.getParameter("path")))){
            resp.setContentType("text/plain");
            resp.setHeader("Content-Disposition", "inline");
            PrintWriter writer = resp.getWriter();
            //TO DO: allow directory download of directories as a zip
            writer.write("downloading directories is currently unavailable");
            return;
        }

        resp.setContentType("application/octet-stream");
        String[] arr = req.getParameter("path").split("/");
        String fileName = arr[arr.length -1];
        resp.setHeader("Content-Disposition", "attachment; filename="+fileName);
        arr = null;

        ServletOutputStream outStream = resp.getOutputStream();
        File file = new File(req.getParameter("path"));
        FileInputStream inputFile = new FileInputStream(file);

        byte[] buffer = new byte[4000];
        int bytesRead = 0;
        bytesRead = inputFile.read(buffer);
        while(bytesRead != -1){
            outStream.write(buffer,0,bytesRead); //in order to not copy extra garbage bytes from the buffer
            bytesRead = inputFile.read(buffer);
        }
        inputFile.close();
        outStream.flush();
        outStream.close();

    }
}
