/**
 * @author ekoletsou
 */
package ymal;

import java.io.*;
import java.sql.*;
import java.util.*;

public class myQuery {

    public static int Query(Connection con, String input) throws SQLException, IOException {
        ResultSet rslt = null;
        Statement stmtnt = null;
        int rowCount = 0;
        String a = null, b = null, z = null;

        BufferedWriter out1 = null;
        BufferedWriter out2 = null;
        BufferedWriter out3 = null;
        BufferedWriter out4 = null;

        //Check for existed files
        boolean existsTransa = (new File("transa.txt")).exists();
        boolean existsSet = (new File("set.txt")).exists();
        boolean existsConfig = (new File("config.txt")).exists();
        boolean existsReal = (new File("realR.txt")).exists();
        if (existsTransa || existsSet || existsConfig) {
            boolean success1 = (new File("transa.txt")).delete();
            boolean success2 = (new File("set.txt")).delete();
            boolean success3 = (new File("config.txt")).delete();
            boolean success4 = (new File("realR.txt")).delete();
            if (!success1 || success2 || success3 || success4) {
                //Deletion failed
            }
        }

        // Create files
        try {
            FileWriter fstream1 = new FileWriter("transa.txt", true);
            FileWriter fstream2 = new FileWriter("set.txt", true);
            FileWriter fstream3 = new FileWriter("config.txt", true);
            FileWriter fstream4 = new FileWriter("realR.txt", true);
            fstream1.flush();
            fstream2.flush();
            fstream3.flush();
            fstream4.flush();
            out1 = new BufferedWriter(fstream1);
            out2 = new BufferedWriter(fstream2);
            out3 = new BufferedWriter(fstream3);
            out4 = new BufferedWriter(fstream4);
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }

        // connection, query results, metadata info
        stmtnt = (Statement) con.createStatement();
        rslt = stmtnt.executeQuery(input);


        ResultSetMetaData rsltMetaData = rslt.getMetaData();
        // Get the number of the columns
        int numberOfColumns = rsltMetaData.getColumnCount();
        System.out.println("\nresultSet MetaData column Count=" + numberOfColumns);

        String[] queryAttr = new String[numberOfColumns + 1];
        String columnName = "", tableName = "";



        Object valueObj = null;
        HashSet<Object> tmpSet = null;
        HashSet<Object>[] ls = new HashSet[numberOfColumns];
        ArrayList array = new ArrayList();

        for (int j = 1; j <= numberOfColumns; j++) {

            columnName = rsltMetaData.getColumnName(j);
            tableName = rsltMetaData.getTableName(j);
            out4.write(tableName + "." + columnName + "\t");
        }
        out4.write("\n=================================================\n");

        // iterate the result set and get one row at a time
        while (rslt.next()) {
            a = "";
            z = "";
            for (int j = 1; j <= numberOfColumns; j++) {

                valueObj = rslt.getObject(j);

                columnName = rsltMetaData.getColumnName(j);
                tableName = rsltMetaData.getTableName(j);

                if (valueObj == null) {
                    a += (" \t");
                    z += (" \t");
                } else {
                    if (valueObj.equals("") || valueObj.equals(" ")) {
                        valueObj = "NULL";
                        a += (tableName + "." + columnName + "= '" + valueObj + "'\t");
                        z += (valueObj + "\t");
                    } else {
                        a += (tableName + "." + columnName + "= '" + valueObj + "'\t");
                        z += (valueObj + "\t");
                    }
                }
                array.add(valueObj);
                tmpSet = ls[j - 1];
                if (tmpSet == null) {
                    tmpSet = new HashSet<Object>();
                    ls[j - 1] = tmpSet;
                }
                tmpSet.add(valueObj);

                rowCount = rslt.getRow();

            }

            out1.write(a + "\n");
            out4.write(z + "\n");
            // out4.newLine();
            //    System.out.println(a + "\n");
        }

        String itemSepFrom1 = "from ", itemSepFrom2 = "From ", itemSepFrom3 = "FROM ";
        String itemSepWhere1 = "where ", itemSepWhere2 = "Where ", itemSepWhere3 = "WHERE ";
        int k = 0, l = 0;

        CharSequence attr = "", attr2 = "";

        if (input.contains(itemSepFrom1) || input.contains(itemSepFrom2) || input.contains(itemSepFrom3)) {
            if (input.contains(itemSepWhere1)) {
                k = input.indexOf(itemSepFrom1);
                l = input.indexOf(itemSepWhere1);
                attr = input.subSequence(k + 4, l);
            } else if (input.contains(itemSepWhere2)) {
                k = input.indexOf(itemSepFrom2);
                l = input.indexOf(itemSepWhere2);
                attr = input.subSequence(k + 4, l);
            } else if (input.contains(itemSepWhere3)) {
                k = input.indexOf(itemSepFrom3);
                l = input.indexOf(itemSepWhere3);
                attr = input.subSequence(k + 4, l);
            }
        }

        if (input.contains(itemSepWhere1) || input.contains(itemSepWhere2) || input.contains(itemSepWhere3)) {
            String endQuery = ";";
            if (input.contains(itemSepWhere1)) {
                if (input.contains(endQuery)) {
                    int v = input.indexOf(endQuery);
                    attr2 = input.subSequence(l + 6, v);
                } else {
                    attr2 = input.substring(l + 6);
                }


            } else if (input.contains(itemSepWhere2)) {
                if (input.contains(endQuery)) {
                    int v = input.indexOf(endQuery);
                    attr2 = input.subSequence(l + 6, v);
                } else {
                    attr2 = input.substring(l + 6);
                }


            } else if (input.contains(itemSepWhere3)) {
                if (input.contains(endQuery)) {
                    int v = input.indexOf(endQuery);
                    attr2 = input.subSequence(l + 6, v);
                } else {
                    attr2 = input.substring(l + 6);
                }
            }
        }

        int itemX = rowCount * numberOfColumns;
        //config file= items, transactions, minSup
        out3.write(itemX + "\n" + rowCount + "\n" + 6 + "\n" + numberOfColumns + "\n");

        // Get the column names; column indices start from 1
        for (int j = 1; j < numberOfColumns + 1; j++) {
            columnName = rsltMetaData.getColumnName(j);
            // Get the name of the column's table name
            tableName = rsltMetaData.getTableName(j);
            queryAttr[j] = tableName + "." + columnName;
            out3.write(queryAttr[j] + "\t");
            System.out.println("resultSet MetaData column Name= " + tableName + "." + columnName);
        }

        out3.write("\n" + attr + "\n" + attr2);


        for (int i = 0; i < ls.length; ++i) {
            b = "";
            tmpSet = ls[i];

            b = tmpSet.toString() + "\n";
            out2.write(b);
        }

        //Closing connections
        try {
            out1.close();
            out2.close();
            out3.close();
            out4.close();
            rslt.close();
            stmtnt.close();
            con.close();
        } catch (Exception e) {
            System.err.println("NOT CLOSED");
        }
        System.out.println("\nTotal results:");
        return (rowCount);
    }
    
}
