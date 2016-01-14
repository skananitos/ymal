/**
 * @author ekoletsou
 */
package ymal;

import java.io.*;
import java.util.*;

public class myApriori {

    Vector<String> candidates = new Vector<String>(); //the current candidates
    String configFile = "config.txt"; //configuration file
    String transaFile = "transa.txt"; //transaction file
    String outputFile = "apriori-output.txt";//output file
    String itemSetsFile = "itemSets.txt";//output file
    int numItems; //number of items per transaction
    int itemSets = 0; //number of item sets
    int numTransactions; //number of transactions
    int numColumns; //number of columns
    double minSup; //minimum support for a frequent itemset
    String oneVal[]; //array of value per column that will be treated as a '1'
    String itemSep = "\t"; //the separator value for items in the database

// Generate Apriori itemsets
    public int aprioriProcess() {
        getConfig();
        System.out.println("Apriori algorithm has started.\n");

        BufferedWriter out = null;

        //Check for existed files
        boolean existsOut = (new File("itemSets.txt")).exists();
        if (existsOut) {
            boolean success = (new File("itemSets.txt")).delete();
            if (!success) {
                //Deletion failed
            }
        }
        do {
            itemSets++;
            generateCandidates(itemSets);
            calculateFrequentItemsets(itemSets);
            if (candidates.size() != 0) {
                System.out.println("Frequent " + itemSets + "-itemsets");
                System.out.println("Candidates:" + candidates);
            }
        } while (candidates.size() > 1);

        return candidates.size() ;

    }

    private void getConfig() {
        FileWriter fw;
        BufferedWriter file_out;
        FileWriter fw_itemSets;
        BufferedWriter file_out_itemSets;

        try {
            FileInputStream file_in = new FileInputStream(configFile);
            BufferedReader data_in = new BufferedReader(new InputStreamReader(file_in));

            numItems = Integer.valueOf(data_in.readLine()).intValue();
            numTransactions = Integer.valueOf(data_in.readLine()).intValue();
            minSup = (Double.valueOf(data_in.readLine()).doubleValue());
            numColumns = Integer.valueOf(data_in.readLine()).intValue();

            System.out.print("\nInput configuration: " + numItems + " items, " + numTransactions + " transactions, ");
            System.out.println("minsup = " + minSup + "," + " number of columns: " + numColumns);
            System.out.println();
            //minSup /= 100.0;
            oneVal = new String[numItems];

            //create the output file
            fw = new FileWriter(outputFile);
            file_out = new BufferedWriter(fw);

            //create the output itemSets file
            fw_itemSets = new FileWriter(itemSetsFile);
            file_out_itemSets = new BufferedWriter(fw_itemSets);

            //put the number of transactions into the output file
            file_out.write("Number of transactions: " + numTransactions + "\n");
            file_out.write("Number of items: " + numItems + "\n");
            file_out.close();
            file_out_itemSets.close();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void generateCandidates(int n) {
        Vector<String> tempCandidates = new Vector<String>(); //temporary candidate string vector
        String str1, str2; //strings that will be used for comparisons
        StringTokenizer st1, st2; //string tokenizers for the two itemsets being compared

        //if its the first set, candidates are just the numbers
        if (n == 1) {
            for (int i = 1; i <= numItems; i++) {
                tempCandidates.add(Integer.toString(i));
            }
        } else if (n == 2) //second itemset is just all combinations of itemset 1
        {
            //add each itemset from the previous frequent itemsets together
            for (int i = 0; i < candidates.size(); i++) {
                st1 = new StringTokenizer(candidates.get(i));
                str1 = st1.nextToken();
                for (int j = i + 1; j < candidates.size(); j++) {
                    st2 = new StringTokenizer(candidates.elementAt(j));
                    str2 = st2.nextToken();
                    tempCandidates.add(str1 + " " + str2);
                }
            }
        } else {
            //for each itemset
            for (int i = 0; i < candidates.size(); i++) {
                //compare to the next itemset
                for (int j = i + 1; j < candidates.size(); j++) {
                    //create the strigns
                    str1 = new String();
                    str2 = new String();
                    //create the tokenizers
                    st1 = new StringTokenizer(candidates.get(i));
                    st2 = new StringTokenizer(candidates.get(j));

                    //make a string of the first n-2 tokens of the strings
                    for (int s = 0; s < n - 2; s++) {
                        str1 = str1 + " " + st1.nextToken();
                        str2 = str2 + " " + st2.nextToken();
                    }
                    //if they have the same n-2 tokens, add them together
                    if (str2.compareToIgnoreCase(str1) == 0) {
                        tempCandidates.add((str1 + " " + st1.nextToken() + " " + st2.nextToken()).trim());
                    }
                }
            }
        }
        candidates.clear(); //clear the old candidates
        candidates = new Vector<String>(tempCandidates); //set the new ones
        tempCandidates.clear();
    }

    private void calculateFrequentItemsets(int n) {
        Vector<String> frequentCandidates = new Vector<String>(); //the frequent candidates for the current itemset
        FileInputStream file_in; //file input stream
        BufferedReader data_in; //data input stream
        FileWriter fw;
        BufferedWriter file_out;
        FileWriter fw_itemSets;
        BufferedWriter file_out_itemSets;
        int count[] = new int[candidates.size()]; //the number of successful matches

        try {
            //output file
            fw = new FileWriter(outputFile, true);
            file_out = new BufferedWriter(fw);
            //output itemSets file
            fw_itemSets = new FileWriter(itemSetsFile, true);
            file_out_itemSets = new BufferedWriter(fw_itemSets);
            //load the transaction file
            file_in = new FileInputStream(transaFile);
            data_in = new BufferedReader(new InputStreamReader(file_in));

            //Reading the file
            String attribute[][] = null;
            attribute = new String[numTransactions][numColumns];

            for (int i = 0; i < numTransactions; i++) {
                String tmpStr = data_in.readLine();
                //  System.out.println("line:" + tmpStr);
                String parts[] = tmpStr.split(itemSep);
                for (int j = 0; j < parts.length; j++) {
                    if (parts[j].equals("") || parts[j].equals(" ") || parts[j].contains("NULL")) {
                        parts[j] = "NULL";
                    }
                }
                for (int j = 0; j < parts.length; j++) {
                    attribute[i][j] = parts[j];
                }
                for (int j = parts.length; j < numColumns; j++) {
                    attribute[i][j] = "NULL";
                }
            }


            for (int i = 0; i < numTransactions; i++) {
                for (int j = 0; j < numColumns; j++) {
                //    System.err.print(attribute[i][j] + " +++ ");
                }
             //   System.err.println("");
            }

            LinkedList<String> monoSets[] = new LinkedList[numColumns];

            for (int j = 0; j < monoSets.length; j++) {
                monoSets[j] = new LinkedList<String>();
            }



            for (int j = 0; j < monoSets.length; j++) {
                for (int i = 0; i < numTransactions; i++) {
                    if (!monoSets[j].contains(attribute[i][j]) && !attribute[i][j].equals("NULL")) {
                        monoSets[j].add(attribute[i][j]);
                    }
                }
            }

            for (int j = 0; j < monoSets.length; j++) {
                LinkedList<String> tmpArray = monoSets[j];
                for (String s : tmpArray) {
                    for (int i = 0; i < j; i++) {
            //            System.err.print("\t");
                    }
          //          System.err.println(s);
                }
            }

            LinkedList<String> syxna = new LinkedList<String>();

            for (itemSets = 1; itemSets <= numColumns; itemSets++) {
                int colCandidate[] = new int[itemSets];
                for (int j = 0; j < itemSets; j++) {
                    colCandidate[j] = j;
                }
                outer1:
                while (true) {
                    int j = 0;
                    //Appearing the compination
                    for (j = 0; j < itemSets; j++) {
            //            System.err.print(colCandidate[j]);
                        if (j == itemSets - 1) {
           //                 System.err.print("\n");
                        } else {
           //                 System.err.print("-");
                        }
                    }

                    //Appearance frequence of each itemset
                    ListIterator<String> iter[] = new ListIterator[colCandidate.length];
                    for (int k = 0; k < iter.length; k++) {
                        iter[k] = monoSets[colCandidate[k]].listIterator();
                    }
                    String syxnoSetSthlon = "";
                    int syxnoSetSthlonCount = -1;
                    outer2:
                    while (true) {
                        String tmpItemString[] = new String[iter.length];
                        String tmpSyxno = "";
                        for (int k = 0; k < iter.length; k++) {
                            try {
                                tmpItemString[k] = iter[k].next();
                                //   tmpSyxno += tmpItemString[k] + "(" + colCandidate[k] + ") ";
                                tmpSyxno += tmpItemString[k];
                                if (k < iter.length - 1) {
                                    tmpSyxno += "\t";
                                }
                                iter[k].previous();
                //                System.err.print(tmpItemString[k] + "(" + colCandidate[k] + ") ");
                            } catch (NoSuchElementException ex) {
                                break outer2;
                            }
                        }
             //           System.err.println("");

                        //Check all subsets of the currend candidate set that exist
                        //In fact, we just check if there are exist all the subsets that are for 1 item smaller
                        //Every time we leave one item of this subset out.
                        String tmpYposynolo = "";
                        boolean isAllSubSetsCommon = true;
                        if (tmpItemString.length != 1) {
                            for (int i = tmpItemString.length - 1; i >= 0; i--) {
                                tmpYposynolo = "";
                                for (int k = 0; k < tmpItemString.length; k++) {
                                    if (i != k) {
                                        //  tmpYposynolo += tmpItemString[k] + "(" + colCandidate[k] + ") ";
                                        tmpYposynolo += tmpItemString[k];
                                    }
                                }
              //                  System.err.println("yposynolo: " + tmpYposynolo);
              //                  System.err.println("SYXNA:");
                                for (String s : syxna) {
               //                     System.err.println("syxno: " + s);
                                }
                                if (syxna.contains(tmpYposynolo) == false) {
                                    isAllSubSetsCommon = false;
                                    break;
                                }
                            }
                        }

                        if (isAllSubSetsCommon == true) {   //Checking if all of its subsets are frequent
                            //We have a specific set with strings in tmpItemString
                            //and we must search in attribute to find how many times it exists
                            int appearenceCount = 0;
                            for (int m = 0; m < numTransactions; m++) {
                                boolean isItemsEqual = true;
                                for (int mm = 0; mm < tmpItemString.length; mm++) {
                                    if (!tmpItemString[mm].equals(attribute[m][colCandidate[mm]])) {
                                        isItemsEqual = false;
                                        break;
                                    }
                                }
                                if (isItemsEqual) {
                                    appearenceCount++;
                                }
                            }
              //              System.err.println("To " + tmpSyxno + " elegxthike kai brethike count=" + appearenceCount);

                            //The selected set is frequent.
                            if (appearenceCount >= minSup && appearenceCount > syxnoSetSthlonCount) {
                                
                               // System.err.println("To " + tmpSyxno + "einai to neo pio syxno set me count:" + appearenceCount);
                                syxnoSetSthlon = tmpSyxno;
                                syxnoSetSthlonCount = appearenceCount;
                            }
                        } else {    //If any of its subsets are not frequent
                      //      System.err.println("To " + tmpSyxno + " den elegxthike.");
                        }

                        int k = iter.length - 1;
                        while (true) {
                            iter[k].next();
                            if (iter[k].hasNext()) {
                                break;
                            } else {
                                iter[k] = monoSets[colCandidate[k]].listIterator();
                                k--;
                            }
                            if (k < 0) {
                                break outer2;
                            }
                        }
                    }
                    if (syxnoSetSthlonCount > 0) {
                        //Add the most frequent set of the column to the syxna list
                    //    System.err.println("To " + syxnoSetSthlon + "Prostethike sta syxna");
                        syxna.add(syxnoSetSthlon);

                        file_out_itemSets.write(syxnoSetSthlon + "\n");
                        //file_out_itemSets.write(syxnoSetSthlonCount + "\n");
                    }

                    //Increasing numbers from back to frond
                    j = itemSets - 1;
                    colCandidate[j]++;
                    while (colCandidate[j] >= numColumns) {
                        colCandidate[j] = 0;
                        if (j == 0) {
                            break outer1;
                        }
                        colCandidate[j - 1]++;
                        j--;
                    }

                    for (j = 1; j < itemSets; j++) {
                        if (colCandidate[j] < colCandidate[j - 1]) {
                            colCandidate[j] = colCandidate[j - 1] + 1;
                        }
                    }
                    for (j = 0; j < itemSets; j++) {
                        if (colCandidate[j] >= numColumns) {
                            break outer1;
                        }
                    }
                }

            }

            System.out.println("SYXNA:");
            for (String s : syxna) {
                System.out.println("syxno: " + s );
            }

            for (int i = 0; i < candidates.size(); i++) {
                if ((count[i] / (double) numTransactions) >= minSup) {
                    frequentCandidates.add(candidates.get(i));
                    //put the frequent itemset into the output file
                    file_out.write(candidates.get(i) + "," + count[i] / (double) numTransactions + "\n");
                }
            }
            file_out.write("-\n");
            file_out.close();
            file_out_itemSets.close();

        } //if error at all in this process, catch it and print the error messate
        catch (IOException e) {
            System.err.println(e);
        }
        candidates.clear(); //clear old candidates
        //new candidates are the old frequent candidates
        candidates = new Vector<String>(frequentCandidates);
        frequentCandidates.clear();
    }


}

