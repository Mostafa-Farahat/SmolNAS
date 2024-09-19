package util;

import jakarta.servlet.http.HttpServletRequest;

public class OwnerFromQuery implements UrlParser{
    @Override
    public String getDirectoryOwner(HttpServletRequest req) {
        String path = req.getParameter("path");
        path = StringProcessing.removeFromString(path, "/home/mostafa/Desktop/SmolData");
        String[] arr = path.split("/");
        return arr[0];
    }
}