package rs.etf.km123247m.GUI.Step;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public abstract class MatrixExamples {

    public static final String SMITH_TWOxTWO =
            "x, 3;\n" +
                    "3*x+3, 1+x;";
    public static final String SMITH_THREExTHREE =
            "0, 2, 5;\n" +
                    "3, 0, 8;\n" +
                    "1, x, 0;";
    public static final String SMITH_FOURxFOUR =
            "2, 0, 3, x;\n" +
                    "3, 5, x+x^3, 1;\n" +
                    "x+8, 0, 5, 0;\n" +
                    "7, 0, 0, 7x-1;";

    public static final String RATIONAL_TWOxTWO =
            "1, 2;\n" +
                    "3, 4;";
    public static final String RATIONAL_THREExTHREE =
            "2, -2, 14;\n" +
                    "0, 3, -7;\n" +
                    "0, 0, 2;";
    public static final String RATIONAL_FOURxFOUR =
            "1, 2, -4, 4;\n" +
                    "2, -1, 4, -8;\n" +
                    "1, 0, 1, -2;\n" +
                    "0, 1, -2, 3;";

    public static final String JORDAN_TWOxTWO =
            "1, -2;\n" +
                    "3, 4;";
    public static final String JORDAN_THREExTHREE =
            "2, 1, 0;\n" +
                    "0, 2, 0;\n" +
                    "0, 0, 3;";
    public static final String JORDAN_FOURxFOUR =
            "2, 0, 3, 0;\n" +
                    "3, 5, 1, 1;\n" +
                    "0, 0, 5, 0;\n" +
                    "7, 0, 0, 7;";
}
