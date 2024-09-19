package util;

import jakarta.servlet.http.HttpServletRequest;

public class OwnerFromQuery implements UrlParser{
    @Override
    public String getDirectoryOwner(HttpServletRequest req) {
        String path = req.getParameter("path");
        path = path.replaceFirst("/home/mostafa/Desktop/SmolData/|/SmolNAS/data/", "");
        String[] arr = path.split("/");
        return arr[0];
    }
}
