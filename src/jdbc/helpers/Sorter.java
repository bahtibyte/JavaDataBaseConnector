package jdbc.helpers;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Comparator;

public class Sorter implements Comparator<Object[]> {

    private final int col;

    public Sorter(int col) {
        this.col = col;
    }

    @Override
    public int compare(Object[] o1, Object[] o2) {

        if (o1.length == 0 || o2.length == 0)
            return 0;

        Object x = o1[col];
        Object y = o2[col];

        if (x == null && y == null)
            return 0;
        if (x == null)
            return -1;
        if (y == null)
            return 1;

        if (x instanceof Integer)
            return ((Integer)x).intValue() -  ((Integer)y).intValue();

        if (x instanceof Double)
            return ((Double)x).compareTo((Double)y);

        if (x instanceof BigDecimal)
            return ((BigDecimal)x).compareTo((BigDecimal)y);

        if (x instanceof String)
            return ((String)x).compareTo((String)y);

        if (x instanceof Date)
            return ((Date)x).compareTo((Date)y);

        if (x instanceof Time)
            return ((Time)x).compareTo((Time)y);

        if (x instanceof Timestamp)
            return ((Timestamp)x).compareTo((Timestamp)y);

        if (x instanceof Boolean)
            return ((Boolean)x).compareTo((Boolean)y);

        if (x instanceof Byte)
            return ((Byte)x).compareTo((Byte)y);

        if (x instanceof Short)
            return ((Short)x).compareTo((Short)y);

        if (x instanceof Float)
            return ((Float)x).compareTo((Float)y);

        return x.toString().compareTo(y.toString());
    }
}
