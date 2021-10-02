package jdbc.helpers;

import com.github.vertical_blank.sqlformatter.core.FormatConfig;
import com.github.vertical_blank.sqlformatter.languages.Dialect;

import java.awt.*;
import java.io.File;

public class Settings {

    /* Font size used in the Result Table */
    public static Font tableCellFont = new Font("Consolas", Font.PLAIN, 12);

    public static int tableCellHeight = 20;

    public static Dialect dialects[] = {Dialect.Db2, Dialect.MariaDb, Dialect.MySql, Dialect.PlSql,
            Dialect.N1ql, Dialect.PostgreSql, Dialect.Redshift, Dialect.SparkSql, Dialect.StandardSql, Dialect.TSql};


    public static FormatConfig config = FormatConfig.builder().uppercase(true).build();

    public static File jdbcFolder;

    public static boolean debug = false;
}
