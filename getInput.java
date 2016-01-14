/**
 * @author ekoletsou
 */

package ymal;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class getInput {
public static String Input()
    {
        String input="";
        //read from System.in
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //try to get users input, if there is an error print the message
        try
        {
            input = reader.readLine();
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        return input;
    }

}
