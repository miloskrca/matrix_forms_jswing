package rs.etf.km123247m.GUI.Form;

import rs.etf.km123247m.Matrix.Forms.Implementation.JordanMatrixForm;
import rs.etf.km123247m.Matrix.Forms.Implementation.PolynomialRationalCanonicalMatrixForm;
import rs.etf.km123247m.Matrix.Forms.Implementation.SmithMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.Handler.Implementation.SymJaMatrixHandler;
import rs.etf.km123247m.Matrix.Handler.MatrixHandler;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Observer.Event.FormEvent;
import rs.etf.km123247m.Parser.MatrixParser.SymJa.IExprMatrixStringParser;
import rs.etf.km123247m.Parser.ParserTypes.StringParser;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by Miloš Krsmanović.
 * Sep 2015
 * <p/>
 * package: rs.etf.km123247m.GUI.Form
 */
public class CmdLineRunner implements Observer {

    private String result;
    private String inputText;
    private String form;
    private HashMap<Integer, String> forms = new HashMap<Integer, String>();

    public CmdLineRunner(String[] parameters) {
        loadFormsHashMap();

        boolean paramMatrix = false;
        boolean paramForm = false;
        boolean paramRun = false;
        for (String param : parameters) {
            if (param.contains("--matrix")) {
                paramMatrix = true;
                inputText = param.split("--matrix=")[1];
            } else if (param.contains("--form")) {
                int option = Integer.parseInt(param.split("--form=")[1]);
                if (option >= 0 && option < forms.size()) {
                    paramForm = true;
                    form = forms.get(option);
                } else {
                    System.out.println("--form opcija van granica dozvoljenog. Dozvoljeno je:");

                    for (Integer key : forms.keySet()) {
                        System.out.println(key + ": " + forms.get(key));
                    }
                }
            } else if (param.equals("--run")) {
                paramRun = true;
            } else if (!param.equals("--no-gui")) {
                System.out.println("Parametar nije prepoznat (" + param + ")");
            }
        }
        if(paramRun) {
            if (paramForm && paramMatrix) {
                calculate();
            } else {
                if (!paramForm) {
                    System.out.println("--form opcija mora biti ispravno uneta da se moglo pokrenuti izračunavanje.");
                }
                if (!paramMatrix) {
                    System.out.println("--matrix opcija mora biti ispravno uneta da se moglo pokrenuti izračunavanje.");
                }
            }
        } else {
            System.out.println("--run opcija mora biti uneta da bi izračunavanje bilo pokrenuto.");
        }
    }

    private void loadFormsHashMap() {
        forms.put(0, "Smitova normalna forma");
        forms.put(1, "Racionalna kanonska forma");
        forms.put(2, "Žordanova kanonska forma");
    }

    public void calculate() {
        StringParser parser = new IExprMatrixStringParser(true);
        if (!inputText.equals("")) {
            parser.setInputString(inputText);
            try {
                if (form != null) {
                    IMatrix matrix = (IMatrix) parser.parseInput();
                    MatrixHandler handler = new SymJaMatrixHandler(matrix);
                    MatrixForm matrixForm = null;
                    if (form.equals(MatrixFormsJSwing.SMITH_FORM)) {
                        matrixForm = new SmithMatrixForm(handler);
                    } else if (form.equals(MatrixFormsJSwing.RATIONAL_FORM)) {
                        matrixForm = new PolynomialRationalCanonicalMatrixForm(handler);
                    } else if (form.equals(MatrixFormsJSwing.JORDANS_FORM)) {
                        matrixForm = new JordanMatrixForm(handler);
                    }
                    if (matrixForm != null) {
                        matrixForm.addObserver(CmdLineRunner.this);
                        matrixForm.start();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = e.getMessage();
            }
        }

        System.out.println();
        System.out.println("------------------------------------------------");
        System.out.println("Forma:          " + form);
        System.out.println("Ulazna matrica: " + inputText);
        System.out.println("------------------------------------------------");
        System.out.println("Rezultat: ");
        System.out.println(result);
    }

    @Override
    public void update(Observable o, Object arg) {
        FormEvent event = (FormEvent) arg;
        MatrixForm form = (MatrixForm) o;
        switch (event.getType()) {
            case FormEvent.PROCESSING_START:
            case FormEvent.PROCESSING_STEP:
            case FormEvent.PROCESSING_INFO:
                break;
            case FormEvent.PROCESSING_END:
                result = event.getMatrix().toString();
                break;
            case FormEvent.PROCESSING_EXCEPTION:
                result = event.getMessage();
                break;
        }
    }

}
