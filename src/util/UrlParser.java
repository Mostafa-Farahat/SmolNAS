package util;

import jakarta.servlet.http.HttpServletRequest;

public interface UrlParser {
    String getDirectoryOwner(HttpServletRequest req);
}
