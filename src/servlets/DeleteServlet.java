package servlets;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.OwnerFromQuery;
import util.Verification;

import java.io.PrintWriter;
import java.nio.file.*;
import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;

@WebServlet("/delete")
public class DeleteServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        final OwnerFromQuery parser = new OwnerFromQuery();
        PrintWriter writer = resp.getWriter();

        //verification
        if(!Verification.isUserAuthorized(req,parser)){//if not authorized yeet request
            writer.write("YOU ARE NOT AUTHORIZED TO MAKE CHANGES TO THIS DIRECTORY");
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        //recursivly traverse the directory DFS and delete the folder
        //when all files in it are deleted

        String path = req.getParameter("path");
        StringBuilder pathBuffer = new StringBuilder();
        pathBuffer.append(System.getenv("NAS_DATAROOT"));//root directory
        pathBuffer.append(path.replaceFirst("/SmolNAS/data/", ""));//current entity from root

        Path inputEntity = Paths.get(pathBuffer.toString());//path of file to download

//        String pathString = req.getParameter("path");
//        Path root = Paths.get(pathString);//path of file to be deleted

        Files.walkFileTree(inputEntity, new SimpleFileVisitor<Path>(){
            public FileVisitResult visitFile(Path file, BasicFileAttributes attr){
                try{
                    Files.delete(file);
                }catch(IOException ex){
                    System.out.println("could not delete file recursively");
                    return FileVisitResult.TERMINATE;
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                    throws IOException
            {
                if(exc == null){
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }else{
                    throw exc;
                }
            }
        });

        String fileName = "/"+inputEntity.getFileName().toString();
        String append = path.replaceFirst(fileName, "");
        resp.sendRedirect("http://localhost:8080" + append);
    }
}
