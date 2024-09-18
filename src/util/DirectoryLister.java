package util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryLister {
    private final Path dataRoot;
    private final PrintWriter writer;
    public DirectoryLister(Path dataRoot, HttpServletResponse resp) throws IOException{
            this.dataRoot = dataRoot;
            this.writer = resp.getWriter();
    }

    public void listDirectory(String url){
        //extract path on system form url
        Pattern pattern = Pattern.compile("/SmolNAS/data/");
        Matcher matcher = pattern.matcher(url);
        String pathFromRoot = matcher.replaceFirst("");
        //Entity is used because the path could refer to a file or directory
        Path entityPath = Paths.get(dataRoot.toString() + "/" + pathFromRoot);

        if(!Files.isDirectory(entityPath)){
            //TO DO:
        }else{//is a directory
            //writing html template to response
            try{
                File file = new File("/home/mostafa/Desktop/SmolNAS/web/directoryTemplate.html");
                FileInputStream fileInput= new FileInputStream(file);
                int r=0;
                while((r=fileInput.read())!=-1){
                    writer.write((char)r);
                }
                fileInput.close();
            }catch(IOException ex){
                System.out.println("could not open html template");
            }

            writer.write(String.format("<div><b>Index Of: %s/</b></div><br>",pathFromRoot));

            try(DirectoryStream<Path> directorTree = Files.newDirectoryStream(entityPath)){
                for(Path entity : directorTree){
                    String fileName = entity.getFileName().toString();
                    String newUrl = null;
                    if(url.charAt(url.length()-1) == '/'){
                        //the url already has a / at the end so don't add it
                        newUrl = url + entity.getFileName();
                    }else{
                        newUrl = url+"/"+entity.getFileName();
                    }
                    //listing files and directories
                    Path fullPath =  entityPath.resolve(entity.getFileName());
                    String html = String.format("<div>\n" +
                            "    <a href=\"%s\">%s</a>\n" +
                            "    <button onclick='download(\"%s\")'>Download</button>\n" +
                            "    <button onclick='del(\"%s\")'>Delete</button>\n" +
                            "</div><br>", newUrl, fileName, fullPath, fullPath);
                    writer.write(html);
                }
                directorTree.close();
            }catch(IOException ex){
                System.out.println("could not list directory item" + ex.getMessage());
            }
        }
    }
}
