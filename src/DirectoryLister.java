import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DirectoryLister {
    private final Path dataRoot;
    private final PrintWriter writer;
    DirectoryLister(Path dataRoot, HttpServletResponse resp) throws IOException{
            this.dataRoot = dataRoot;
            this.writer = resp.getWriter();
    }

    void listDirectory(String url){
        //extract path on system form url
        Pattern pattern = Pattern.compile("/SmolNAS/data/");
        Matcher matcher = pattern.matcher(url);
        String pathFromRoot = matcher.replaceFirst("");
        //Entity is used because the path could refer to a file or directory
        Path entityPath = Paths.get(dataRoot.toString() + "/" + pathFromRoot);

        if(!Files.isDirectory(entityPath)){
            //TO DO:
        }else{
            //is a directory
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

                    String html = String.format("<div>\n" +
                            "    <a href=\"%s\">%s</a>\n" +
                            "    <button>Download File</button>\n" +
                            "    <button>Delete File</button>\n" +
                            "</div><br>", newUrl, fileName);
                    writer.write(html);
                }
                directorTree.close();
            }catch(IOException ex){
                System.out.println("could not list directory item" + ex.getMessage());
            }
        }

    }
}
