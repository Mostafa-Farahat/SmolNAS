package util;

import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.*;

public class DirectoryLister {
    private final Path dataRoot;
    private final PrintWriter writer;
    public DirectoryLister(Path dataRoot, HttpServletResponse resp) throws IOException{
            this.dataRoot = dataRoot;
            this.writer = resp.getWriter();
    }

    public void listDirectory(String url){
        //extract path on system form url
        String pathFromRoot = url.replaceAll("/SmolNAS/data/","");
        //Entity is used because the path could refer to a file or directory
        Path entityPath = Paths.get(dataRoot.toString() + "/" + pathFromRoot);

        if(!Files.isDirectory(entityPath)){
            //TO DO:
        }else{//is a directory
            //writing html template to response
            try{
                File file = new File( System.getenv("CATALINA_HOME")+"/webapps/SmolNAS/directoryTemplate.html");
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
                    StringBuilder fileNameBuilder = new StringBuilder(entity.getFileName().toString());
                    String newUrl = null;
                    if(url.charAt(url.length()-1) == '/'){
                        //the url already has a / at the end so don't add it
                        newUrl = url + entity.getFileName();
                    }else{
                        newUrl = url+"/"+entity.getFileName();
                    }

                    //add a / to directory names to distinguish them from files
                    if(Files.isDirectory(entity)){
                        fileNameBuilder.append("/");
                    }

                    //listing files and directories
                    String fileName = fileNameBuilder.toString();
                    String fileNameNoSlash = fileName.replaceFirst("/","");
                    Path fullPath =  entityPath.resolve(entity.getFileName());
                    String html = String.format("<div>\n" +
                            "    <a href=\"%s\">%s</a>\n" +
                            "    <button onclick='download(\"%s\")'>Download</button>\n" +
                            "    <button onclick='del(\"%s\")'>Delete</button>\n" +
                            "</div><br>", newUrl, fileName, fileNameNoSlash, fileNameNoSlash);
                    writer.write(html);
                }
                directorTree.close();
            }catch(IOException ex){
                System.out.println("could not list directory item" + ex.getMessage());
            }
        }
    }
}
