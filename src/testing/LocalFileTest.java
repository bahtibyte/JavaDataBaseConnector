package testing;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class LocalFileTest {



    public static void main(String[] args) throws Exception {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(new File(new File(System.getProperty("user.home")+"/.jdbc"), "logins.json")))
        {
            //Read JSON file
            JSONObject obj = (JSONObject) jsonParser.parse(reader);
            JSONArray logins = (JSONArray) obj.get("saved");
            //JSONArray employeeList = (JSONArray) obj;
            System.out.println(logins);

            //Iterate over employee array
            //employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
