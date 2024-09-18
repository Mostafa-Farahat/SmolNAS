package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    static private DataSource dataSource = null;
    //singelton class so the constructor is private
    private ConnectionPool(){

    }
    public static Connection getConnection(){
        Connection con = null;
        try {
            if (dataSource == null) {
                Context context = new InitialContext();
                dataSource = (DataSource) context.lookup("java:comp/env/jdbc/SmolNASdb");
            }
            con = dataSource.getConnection();
        }catch(NamingException ex){
            System.out.println("could not retrieve JNDI resource" + ex.getMessage());
        }catch(SQLException ex){
            System.out.println("could not get a conneciton");
        }

        return con;

    }
}
