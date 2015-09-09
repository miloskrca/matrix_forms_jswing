package rs.etf.km123247m.GUI.Main;

import rs.etf.km123247m.GUI.Form.CmdLineRunner;
import rs.etf.km123247m.GUI.Form.MatrixFormsJSwing;

/**
 * Created by Miloš Krsmanović.
 * Jan 2015
 * <p/>
 * package: rs.etf.km123247m.GUI.Main
 */
public class Main {

    /**
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        if(parameterProvided(args, "--no-gui")) {
            new CmdLineRunner(args);
        } else {
            new MatrixFormsJSwing(args);
        }
    }

    protected static boolean parameterProvided(String[] args, String param) {
        for (String arg : args) {
            if (arg.equals(param)) {
                return true;
            }
        }
        return false;
    }
}
