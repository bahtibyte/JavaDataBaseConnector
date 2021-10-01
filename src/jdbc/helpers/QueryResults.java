package jdbc.helpers;

public class QueryResults {

    private Object[][] data;
    private String[] colNames;

    private String sql;

    private int rows;
    private int cols;

    public QueryResults(Object[][] data, String[] colNames, String sql, int rows, int cols) {
        this.data = data;
        this.colNames = colNames;
        this.sql = sql;
        this.rows = rows;
        this.cols = cols;
    }

    public Object[][] getData() {
        return data;
    }

    public String[] getColNames() {
        return colNames;
    }

    public String getSql() {
        return sql;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }
}
