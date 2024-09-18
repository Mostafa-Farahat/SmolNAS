package util;

import jakarta.servlet.http.HttpServletRequest;

public class OwnerFromUrl implements UrlParser{
    @Override
    public String getDirectoryOwner(HttpServletRequest req) {
        return req.getRequestURI().split("/")[3];
    }
}
