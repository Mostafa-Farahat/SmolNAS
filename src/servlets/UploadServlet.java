package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import util.OwnerFromQuery;
import util.OwnerFromUrl;
import util.Verification;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/upload")
@MultipartConfig
public class UploadServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TO DO: Implement verification for uploads
        OwnerFromQuery parser = new OwnerFromQuery();
        if(!Verification.isUserAuthorized(req,parser)){
            PrintWriter writer = resp.getWriter();
            writer.write("YOU ARE NOT AUTHORIZED TO MAKE CHANGES TO THIS DIRECTORY");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

//      can't get a part by name so we use stream API to get what we need
        List<Part> parts = req.getParts().stream().filter(part -> "files".equals(part.getName()) && part.getSize()>0).collect(Collectors.toList());

        Path dataRoot = Paths.get("/home/mostafa/Desktop/SmolData/");
        String relativePath = req.getParameter("path").replaceFirst("/SmolNAS/data/", "");
        Path directoryAbsloutePath= dataRoot.resolve(relativePath);

        for(Part filePart : parts){
            String fileName = filePart.getSubmittedFileName();
            InputStream uploaded = filePart.getInputStream();
            Path newFile = directoryAbsloutePath.resolve(fileName);

            try{
                long bytesCopied = Files.copy(uploaded, newFile, StandardCopyOption.REPLACE_EXISTING);
            }catch(IOException ex){
                PrintWriter writer = resp.getWriter();
                writer.write("An Error Occured While Uploading Your Selection");
            }finally{
                uploaded.close();
            }
        }
        resp.sendRedirect("http://localhost:8080"+ req.getParameter("path"));

    }
}
