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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String pathString = req.getParameter("path");
        Path root = Paths.get(pathString);//path of file to be deleted

        Files.walkFileTree(root, new SimpleFileVisitor<Path>(){
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

        //overWriting the path string to make it equal to the path of the directory
        Pattern regex = Pattern.compile("/home/mostafa/Desktop/SmolData");
        Matcher matcher = regex.matcher(pathString);
        pathString = matcher.replaceFirst("");

        String[] arr = pathString.split("/");
        arr[0]="";
        arr[arr.length-1]="";
        StringBuilder pathBuffer = new StringBuilder();
        for(int i=0; i<arr.length-1; i++){
            //perventing extra / for aesthetic purposes
            if(i == arr.length-2){
                pathBuffer.append(arr[i]);
            }else{
                pathBuffer.append(arr[i]).append("/");
            }
        }

        pathString = pathBuffer.toString();
        resp.sendRedirect("http://localhost:8080/SmolNAS/data" + pathString);
    }
}
