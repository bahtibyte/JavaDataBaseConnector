package testing;

import java.sql.*;

public class QTest {


    public static void main(String args[]) {


        try {

            Connection connection = DriverManager.getConnection("jdbc:sqlserver://192.168.1.156:12001;user=SA;password=PH@123456789");
            Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            String sql = "USE TSQLV4 SELECT * FROM Sales.Orders";

            ResultSet resultSet = stmt.executeQuery(sql);
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            int rows = getRowCount(resultSet);
            int cols = resultSetMetaData.getColumnCount() + 1;

            String colNames[] = new String[cols];
            colNames[0] = "";

            for(int i = 1; i < cols; i++) {
                colNames[i] = resultSetMetaData.getColumnName(i);
            }

            Object data[][] = new Object[rows][];

            for (int row = 0; row < rows; row++) {
                resultSet.next();
                data[row] = new Object[cols];
                data[row][0] = Integer.valueOf(row + 1);
                for(int i = 1; i < cols; i++) {
                    data[row][i] = resultSet.getObject(i);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    private static int getRowCount(ResultSet resultSet) {
        if (resultSet == null) {
            return -1;
        }

        try {
            resultSet.last();
            return resultSet.getRow();
        } catch (SQLException exp) {
            exp.printStackTrace();
        } finally {
            try {
                resultSet.beforeFirst();
            } catch (SQLException exp) {
                exp.printStackTrace();
            }
        }

        return -1;
    }
}
