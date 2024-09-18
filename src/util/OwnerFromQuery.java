package util;

import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OwnerFromQuery implements UrlParser{
    @Override
    public String getDirectoryOwner(HttpServletRequest req) {
        String path = req.getParameter("path");
        Pattern regex = Pattern.compile("/home/mostafa/Desktop/SmolData/");
        Matcher matcher = regex.matcher(path);
        path = matcher.replaceFirst("");
        String[] arr = path.split("/");
        return arr[0];
    }
}
