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

import java.io.PrintWriter;
import java.nio.file.Files;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@WebServlet("/download")
public class DownloadServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{

        final OwnerFromQuery parser = new OwnerFromQuery();

        String path = req.getParameter("path");
        StringBuilder pathBuffer = new StringBuilder();
        pathBuffer.append(System.getenv("NAS_DATAROOT"));//root directory
        pathBuffer.append(path.replaceFirst("/SmolNAS/data/", ""));//current entity from root

        Path inputEntity = Paths.get(pathBuffer.toString());//path of file to download

        //verification
        if(!Verification.isUserAuthorized(req,parser)){//if not authorized yeet request
            PrintWriter writer = resp.getWriter();
            writer.write("YOU ARE NOT AUTHORIZED TO ACCESS THIS DIRECTORY");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if(Files.isDirectory(inputEntity)){
            resp.setContentType("text/plain");
            resp.setHeader("Content-Disposition", "inline");
            //TO DO: allow directory download of directories as a zip
            PrintWriter writer = resp.getWriter();
            writer.write("downloading directories is currently unavailable");
            return;
        }

        resp.setContentType("application/octet-stream");
        String[] arr = path.split("/");
        resp.setHeader("Content-Disposition", "attachment; filename="+arr[arr.length-1]);
        arr = null;

        ServletOutputStream outStream = resp.getOutputStream();

        try{
            long bytesCopied = Files.copy(inputEntity, outStream);
        }catch(IOException ex){
            PrintWriter writer = resp.getWriter();
            writer.write("and error has occured while downloading your file");
        }finally{
            outStream.flush();
            outStream.close();
        }
    }
}
