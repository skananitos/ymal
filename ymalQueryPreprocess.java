/**
 * @author ekoletsou
 */
package ymal;

import com.mysql.jdbc.Statement;
import java.io.*;
import java.sql.ResultSet;

public class ymalQueryPreprocess {

    String itemSetsFile = "itemSets.txt";//output file
    String configFile = "config.txt";//output file
    String select = "";
    String from = "";
    String where = "";
    String ymalR = "ymalR.txt";//new output file

    public int preprocessQuery() {
        FileInputStream fstream;
        BufferedReader br;
        FileInputStream fstream_cnf;
        BufferedReader br_cnf;
        DataInputStream in;
        DataInputStream in_cnf;
        int row = 1;

        String input1, input2, input3;
        //the separator value for and,AND,And
        String itemSep2 = "and ";
        String itemSep3 = "And ";
        String itemSep4 = "AND ";

        //create file to write the results of new queries
        FileWriter fw;//ymalR.txt
        BufferedWriter file_out = null;
        try {
            fw = new FileWriter(ymalR);
            file_out = new BufferedWriter(fw);
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());

        }


        //Query Preprocess
        try {
            //open CONFIG file in order to read user's query
            fstream_cnf = new FileInputStream(configFile);
            in_cnf = new DataInputStream(fstream_cnf);
            br_cnf = new BufferedReader(new InputStreamReader(in_cnf));

            //Read File Line By Line
            int numItems = Integer.valueOf(br_cnf.readLine()).intValue();
            int numTransactions = Integer.valueOf(br_cnf.readLine()).intValue();
            double minSup = (Double.valueOf(br_cnf.readLine()).doubleValue());
            int numColumns = Integer.valueOf(br_cnf.readLine()).intValue();
            input1 = String.valueOf(br_cnf.readLine());
            input2 = String.valueOf(br_cnf.readLine());
            input3 = String.valueOf(br_cnf.readLine());

            //FROM clause
            if (input2.contains(",")) {
                String parts01[] = input2.split(",");
                for (int j = 0; j < parts01.length; j++) {
                    //     System.out.print(parts01[j] + ", ");
                    from += parts01[j] + ",";
                }
            } else {
                from = input2 + " ";
            }
            from = from.substring(0, from.length() - 1);

            file_out.write("Related results with ");


            //WHERE clause
            String[] parts3 = null;
            if (input3.contains(itemSep2) || input3.contains(itemSep3) || input3.contains(itemSep4)) {
                if (input3.contains(itemSep2)) {
                    parts3 = input3.split(itemSep2);
                } else if (input3.contains(itemSep3)) {
                    parts3 = input3.split(itemSep3);
                } else if (input3.contains(itemSep4)) {
                    parts3 = input3.split(itemSep4);
                }

                for (int z = 0; z < parts3.length; z++) {
                    if (parts3[z].contains(".mid") || parts3[z].contains(".pid")) {
                        String keys = parts3[z];
                        where += keys + " AND ";
                    }
                    if (!parts3[z].contains(".mid") && !parts3[z].contains(".pid")) {
                        String condition = parts3[z];
                        if (condition.contains("=")&& !condition.contains(">=")&&!condition.contains("<=")) {
                            String oldChar = "=";
                            String newChar = "<>";
                            String newCondition = condition.replace(oldChar, newChar);
                            where += newCondition + " AND ";
                            int k = condition.indexOf(oldChar);
                            CharSequence attr = condition.substring(k + 1);
                            file_out.write(attr + "\t");
                            int a = condition.indexOf(".");
                            int b = condition.indexOf(oldChar);
                            CharSequence attr1 = condition.subSequence(a + 1, b);
                            select += attr1 + ", ";

                        }
                        if (condition.contains("<")||condition.contains(">")||condition.contains("<>")
                                ||condition.contains(">=")||condition.contains("<=")) {
                            //den peirazw th synthiki stis YMAL
                            where += condition + " AND ";
                        }
                       
                    } 
                }

               where = where.substring(0, where.length() - 4);
            } else {
                String eq = "=";
                int e = input3.indexOf(eq);
                CharSequence attr = input3.substring(0, e + 2);
                select += attr;
                String dif = "<>";
                String newCondition = input3.replace(eq, dif);
                where += newCondition;
            }

select = select.substring(0, select.length() - 2);

            //open ITEMSETS file in order to read the data
            fstream = new FileInputStream(itemSetsFile);
            // Get the object of DataInputStream
            in = new DataInputStream(fstream);
            br = new BufferedReader(new InputStreamReader(in));


            //Read File Line By Line
            String strLine = "";
            Object valueObj = null;
            String q = "SELECT " + select + " FROM " + from + "WHERE " + where;
            java.sql.Connection conn = getConnection.Connection();
            Statement stmtnt = (Statement) conn.createStatement();
            String columnName = "", tableName = "", newAttr = "";
            int count = 0;

            while ((strLine = br.readLine()) != null) {

                if (strLine.contains("\t")) {
                    String newStr = strLine.replace("\t", " AND ");
                   
 q = q + " AND " + newStr + " GROUP BY " + select + " ORDER BY count(*) desc LIMIT 10";
                    System.out.println(q + "\n");
                    newAttr = newAttr.substring(0, newAttr.length() - 5);
                    file_out.write("# " + "Because of similar " + newAttr + "\n");
                }

                if (strLine.contains("=") && !strLine.contains("\t")) {
               
q = q + " AND " + strLine + " GROUP BY " + select + " ORDER BY count(*) desc LIMIT 10";
                    System.out.println(q + "\n");
                    int n = strLine.indexOf(".");
                    int m = strLine.indexOf("=");
                    CharSequence attr = strLine.subSequence(n + 1, m);
                    CharSequence attr2 = strLine.subSequence(0, m);
                    file_out.write("# " + "Because of similar " + attr + "\n");
                    newAttr += attr + " and ";
                }

                ResultSet rslt = stmtnt.executeQuery(q);
                java.sql.ResultSetMetaData rsltMetaData = rslt.getMetaData();
                int numberOfColumns = rsltMetaData.getColumnCount();

                while (rslt.next()) {
                    String a = "";
                    count++;
                    for (int j = 1; j <= numberOfColumns; j++) {

                        valueObj = rslt.getObject(j);

                        columnName = rsltMetaData.getColumnName(j);
                        tableName = rsltMetaData.getTableName(j);

                        if (valueObj == null) {
                            a += (" \t");
                        } else {
                            if (valueObj.equals("") || valueObj.equals(" ")) {
                                valueObj = "NULL";
                                a += (valueObj + "\t");
                            } else {
                                a += (valueObj + "\t");
                            }
                        }
                    }
                    file_out.write(a + "\n");
                    System.out.println(a + "\n");
                }

                //     if (!rslt.next()) {
                //         file_out.write("...no results" + "\n");
                //     }

                q = "SELECT " + select + " FROM " + from + "WHERE " + where;

                row++;
                file_out.write("\n");

            }

            //Closing connections
            try {
                in.close();
                in_cnf.close();
                file_out.close();

            } catch (IOException e) {
                System.err.println("NOT CLOSED");
            }

        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        System.out.println("");
        return row;
    }
}
