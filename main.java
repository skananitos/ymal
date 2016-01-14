/**
 * @author ekoletsou
 *
 * Local Based
 */
package ymal;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) throws SQLException, Exception {

      System.out.println("STEP 1");

      Connection con = null;
      con = getConnection.Connection();

     System.out.println("STEP 2");

     int tmp = myQuery2.Query(con);
       System.out.println(tmp);

     System.out.println("STEP 3");

    myApriori ap = new myApriori();
       ap.aprioriProcess();

       System.out.println("STEP 4");

        ymalQueryPreprocess tmp3 = new ymalQueryPreprocess();
        tmp3.preprocessQuery();

        System.out.println("END");

    }
}
