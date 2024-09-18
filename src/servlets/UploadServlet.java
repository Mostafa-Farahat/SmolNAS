package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import util.OwnerFromUrl;
import util.Verification;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
//        OwnerFromUrl parser = new OwnerFromUrl();
//        if(!Verification.isUserAuthorized(req,parser)){
//            PrintWriter writer = resp.getWriter();
//            writer.write("YOU ARE NOT AUTHORIZED TO MAKE CHANGES TO THIS DIRECTORY");
//            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return;
//        }

//      can't get a part by name so we use stream API to get what we need
        List<Part> parts = req.getParts().stream().filter(part -> "files".equals(part.getName()) && part.getSize()>0).collect(Collectors.toList());

        Path dataRoot = Paths.get("/home/mostafa/Desktop/SmolData/");

        Pattern regex = Pattern.compile("/SmolNAS/data/");
        Matcher matcher = regex.matcher(req.getParameter("path"));
        String relativePath = matcher.replaceFirst("");

        Path directoryAbsloutePath= dataRoot.resolve(relativePath);


        for(Part filePart : parts){
            String fileName = filePart.getSubmittedFileName();
            InputStream uploaded = filePart.getInputStream();
            Path newFile = directoryAbsloutePath.resolve(fileName);

            OutputStream outFile = Files.newOutputStream(newFile);

            byte[] buffer = new byte[4000];
            int bytesRead = 0;
            bytesRead = uploaded.read(buffer);
            while(bytesRead != -1){
                outFile.write(buffer,0,bytesRead); //in order to not copy extra garbage bytes from the buffer
                bytesRead = uploaded.read(buffer);
            }
            uploaded.close();
            outFile.flush();
            outFile.close();
        }
        resp.sendRedirect("http://localhost:8080"+ req.getParameter("path"));

    }
}
