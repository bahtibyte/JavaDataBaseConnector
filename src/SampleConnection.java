import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SampleConnection {

    public static void main(String args[]) {


        String connectionUrl = "";

        try {

            Connection con = DriverManager.getConnection(connectionUrl);
            Statement stmt = con.createStatement();

            String SQL = "SELECT TOP 20 * FROM Person.Person";

            ResultSet rs = stmt.executeQuery(SQL);
            int i = 1;
            while (rs.next()) {
                System.out.println(i++ +": "+rs.getString("FirstName") + " " + rs.getString("LastName"));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }


    }

}
