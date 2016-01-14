/**
 * @author ekoletsou
 */

package ymal;

//Database connection -- MySQL Connection


import java.sql.Connection;
import java.sql.DriverManager;


public class getConnection {
    public static Connection Connection() throws Exception {

        String driver = "com.mysql.jdbc.Driver";
        String serverName = "localhost:3306";
        String mydatabase = "ymal";
        String url = "jdbc:mysql://" + serverName + "/" + mydatabase;
        String username = "root";
        String password = "123";
        Connection con = null;

        try {

            Class.forName(driver);
            con = (Connection) DriverManager.getConnection(url, username, password);

        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        System.out.println("OK");
        return con;
    }

}
