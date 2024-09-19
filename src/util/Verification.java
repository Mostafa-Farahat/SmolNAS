package util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Verification {
    public static boolean isUserAuthorized(HttpServletRequest req, UrlParser parser){
        //the UrlParser interface provides a method to return the directory owner
        //against which the verification should be done.

        //the interface is passed in order to provide different ways
        //to get the owner name depending on the usage
        // (e.g the owner could be in a url query or the url itself)
        String directoryOwner = parser.getDirectoryOwner(req);
        Connection con = null;
        boolean status = false;
        try{
            con = ConnectionPool.getConnection();
            Cookie[] cookies= req.getCookies();
            if(cookies==null){
                System.out.println("request without a cookie");
                return false;
            }

            String requestSessionID = null;

            for(Cookie cookie : cookies){
                if(cookie.getName().equals("accessToken")){
                    requestSessionID = cookie.getValue();
                    break;
                }
            }

            PreparedStatement getNameFromSessionID= con.prepareStatement("SELECT userName FROM sessions WHERE sessionID = ?");
            getNameFromSessionID.setString(1,requestSessionID);
            ResultSet result = getNameFromSessionID.executeQuery();

            if(!result.isBeforeFirst()){
                //if result set is empty then the token doesn't exist in db
                System.out.println("your sesison ID doesn't exist in db");//for logging
                status = false;
            }else{
                result.next();
                if(result.getString(1).equals(directoryOwner)){//access token corresponds to owner
                    status = true;
                }else{//access token not coresponding to owner
                    status = false;
                }
            }
        }catch(SQLException ex){
            System.out.println("error occured while accessing db" + ex.getMessage());
        }finally {
            //TO DO: close rest of resources
            try{
                con.close();
            }catch(SQLException ex){
                System.out.println("cannot close connection: "+ ex.getMessage());
            }
        }
        return status;
    }
}
