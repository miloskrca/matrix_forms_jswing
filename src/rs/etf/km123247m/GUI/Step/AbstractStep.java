package rs.etf.km123247m.GUI.Step;

import org.matheclipse.core.form.tex.TeXFormFactory;
import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Command.MatrixCommand.*;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Matrix.Implementation.ArrayMatrix;
import rs.etf.km123247m.Matrix.MatrixCell;
import rs.etf.km123247m.Observer.Event.FormEvent;
import rs.etf.km123247m.Polynomial.Term;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Controller
 */
public abstract class AbstractStep {

    public final static int START = -1;
    public final static int INFO = -2;
    public final static int END = -3;
    private int number;

    private MatrixForm form;
    private FormEvent event;
    private ICommand command;
    protected ArrayList<Map.Entry<String, IMatrix>> matrices = new ArrayList<Map.Entry<String, IMatrix>>();
    protected JPanel stepStatusPanel = new JPanel();

    public AbstractStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        this.number = number;
        this.command = command;
        this.event = event;
        this.form = form;

        try {
            stepStatusPanel.setLayout(new BoxLayout(stepStatusPanel, BoxLayout.Y_AXIS));
            addTitleToStepStatus(getDescription());
            saveStepStatusPanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void saveStepStatusPanel() throws Exception;

    public static LaTexLabel getLaTexLabel(String formula) {
        LaTexLabel label = new LaTexLabel();

        label.setFormula(formula);
        label.render();

        return label;
    }

    public JPanel getStepStatusPanel() {
        return stepStatusPanel;
    }

    public String getTitle() {
        switch (number) {
            case START:
                return "Start";
            case INFO:
                return "Info";
            case END:
                return "Finish";
            default:
                return "Step " + number;
        }
    }

    protected String generateLatexMatrix(String name, IMatrix matrix) throws Exception {
        String f = (name != null ? name + " = " : "") + "\\begin{bmatrix}";
        for (int row = 0; row < matrix.getRowNumber(); row++) {
            for (int column = 0; column < matrix.getColumnNumber(); column++) {
                f += getLatexFromMatrixElement(matrix.get(row, column).getElement());
                if (column < matrix.getColumnNumber() - 1) {
                    f += " & ";
                }
            }
            f += " \\\\";
        }
        f += "\\end{bmatrix}";

        return f;
    }

    /**
     * Generate example matrix for displaying in step preview
     *
     * @return Example matrix string
     */
    protected String getExampleLatexMatrix() {
        String result = "";
        IMatrix example = new ArrayMatrix(4, 4);
        try {
            example.set(new MatrixCell(0, 0, "b1(x)"));
            example.set(new MatrixCell(0, 1, "0"));
            example.set(new MatrixCell(0, 2, "..."));
            example.set(new MatrixCell(0, 3, "0"));

            example.set(new MatrixCell(1, 0, "0"));
            example.set(new MatrixCell(1, 1, "b2(x)"));
            example.set(new MatrixCell(1, 2, "..."));
            example.set(new MatrixCell(1, 3, "0"));

            example.set(new MatrixCell(2, 0, "..."));
            example.set(new MatrixCell(2, 1, "..."));
            example.set(new MatrixCell(2, 2, "..."));
            example.set(new MatrixCell(2, 3, ".."));

            example.set(new MatrixCell(3, 0, "0"));
            example.set(new MatrixCell(3, 1, "0"));
            example.set(new MatrixCell(3, 2, "..."));
            example.set(new MatrixCell(3, 3, "b_m(x)"));
            result = generateLatexMatrix("B", example);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    protected String generateMupadMatrix(String name, IMatrix matrix) throws Exception {
        String f = name + " := matrix([";
        for (int row = 0; row < matrix.getRowNumber(); row++) {
            f += "[";
            for (int column = 0; column < matrix.getColumnNumber(); column++) {
                f += matrix.get(row, column).getElement().toString();
                if (column < matrix.getColumnNumber() - 1) {
                    f += ",";
                }
            }
            f += "]";
            if (row < matrix.getRowNumber() - 1) {
                f += ",";
            }
        }
        f += "])";

        return f;
    }

    /**
     * MuPad Commands
     * @return String containing MuPad commands for getting the matrices in MuPad
     * @throws Exception
     */
    public String getMuPadCommands() throws Exception {
        String mupadCommands = "";
        for (Map.Entry<String, IMatrix> entry : matrices) {
            mupadCommands += "\n" + generateMupadMatrix(entry.getKey(), entry.getValue()) + ";";
        }
        String className = this.getClass().getName();
        mupadCommands += "\n";
        if(className.contains("Jordan")) {
            mupadCommands += "J1 := linalg::jordanForm(A);";
        } else if (className.contains("RationalCanonical")) {
            mupadCommands += "R1 := linalg::frobeniusForm(A);";
            mupadCommands += "\nR2 := T^(-1) * A * T;";
        } else if (className.contains("Smith")) {
            mupadCommands += "S1 := linalg::smithForm(A);";
        }

        return mupadCommands;
    }

    /**
     * Returns a LaTex string from an object
     *
     * @param object Object
     *
     * @return LaTex string
     */
    public String getLatexFromMatrixElement(Object object) {
        TeXFormFactory f = new TeXFormFactory();
        StringBuffer sb = new StringBuffer();
        f.convert(sb, object, 0);
        return sb.toString();
    }

    /**
     * Command description
     *
     * @return String containing description pf the command class
     */
    public String getCommandDescription() {
        String description = "";
        if (command != null) {
            String commandClass = command.getClass().getSimpleName();
            if (commandClass.equals("SwitchColumnsCommand")) {
                SwitchColumnsCommand comm = (SwitchColumnsCommand) command;
                description = "Switching columns " + comm.getColumn1() + " and " + comm.getColumn2() + ".";
            } else if (commandClass.equals("SwitchRowsCommand")) {
                SwitchRowsCommand comm = (SwitchRowsCommand) command;
                description = "Switching rows " + comm.getRow1() + " and " + comm.getRow2() + ".";
            } else if (commandClass.equals("MultiplyRowWithElementAndStoreCommand")) {
                MultiplyRowWithElementAndStoreCommand comm = (MultiplyRowWithElementAndStoreCommand) command;
                description = "Multiplying row "
                        + comm.getRow() + " with element "
                        + comm.getElement().toString() + ".";
            } else if (commandClass.equals("MultiplyRowWithElementAndAddToRowAndStoreCommand")) {
                MultiplyRowWithElementAndAddToRowAndStoreCommand comm = (MultiplyRowWithElementAndAddToRowAndStoreCommand) command;
                description = "Multiplying row "
                        + comm.getRow1() + " with element "
                        + comm.getElement().toString() + " and adding to row "
                        + comm.getRow2() + ".";
            } else if (commandClass.equals("MultiplyColumnWithElementAndStoreCommand")) {
                MultiplyColumnWithElementAndStoreCommand comm = (MultiplyColumnWithElementAndStoreCommand) command;
                description = "Multiplying column "
                        + comm.getColumn() + " with element "
                        + comm.getElement().toString() + ".";
            } else if (commandClass.equals("MultiplyColumnWithElementAndAddToColumnAndStoreCommand")) {
                MultiplyColumnWithElementAndAddToColumnAndStoreCommand comm = (MultiplyColumnWithElementAndAddToColumnAndStoreCommand) command;
                description = "Multiplying column "
                        + comm.getColumn1() + " with element "
                        + comm.getElement().toString() + " and adding to column "
                        + comm.getColumn2() + ".";
            } else if (commandClass.equals("AddRowsAndStoreCommand")) {
                AddRowsAndStoreCommand comm = (AddRowsAndStoreCommand) command;
                description = "Adding rows " + comm.getRow1() + " and " + comm.getRow2() + ".";
            } else if (commandClass.equals("AddColumnsAndStoreCommand")) {
                AddColumnsAndStoreCommand comm = (AddColumnsAndStoreCommand) command;
                description = "Adding columns " + comm.getColumn1() + " and " + comm.getColumn2() + ".";
            } else {
                description = commandClass;
            }
        }

        return description;
    }

    /**
     * Step description
     *
     * @return String containing description of the step
     */
    public abstract String getDescription();

    /**
     * Add explanation why are we fixing the diagonal to display panel.
     */
    protected void addFixingDiagonalExplanation() {
        addToStepStatus(new JLabel("Elements on the diagonal need fixing if the following is not true:"));
        addToStepStatus(getLaTexLabel("b_1(x)|b_2(x)|...|b_m(x)"));
        addToStepStatus(new JLabel("For some matrix B:"));
        addToStepStatus(getLaTexLabel(getExampleLatexMatrix()));
    }

    /**
     * Add explanation how are we preparing the matrix for smith transformation to display panel.
     *
     * @param matrix Matrix that is subtracted from xI
     */
    protected void addSubtractForSmithExplanation(IMatrix matrix) throws Exception {
        addToStepStatus(new JLabel("The matrix first needs to be subtracted by a diagonal matrix"));
        addToStepStatus(new JLabel("before it is transformed to Smith form:"));
        addToStepStatus(getLaTexLabel("A_I = x*I-A"));
        addToStepStatus(new JComponent[]{
                getLaTexLabel(generateLatexMatrix("I", form.getHandler().diagonal(matrix.getRowNumber(), form.getHandler().getObjectFromString(String.valueOf(Term.X))))),
                getLaTexLabel(generateLatexMatrix("A", matrix))
        });
    }

    /**
     * Adds component to the panel
     *
     * @param components Components
     */
    public void addToStepStatus(JComponent[] components) {
        JPanel panel = new JPanel();
        for (JComponent component : components) {
            panel.add(component);
        }
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        stepStatusPanel.add(panel);
    }

    /**
     * Adds component to the panel
     *
     * @param component Component
     */
    public void addToStepStatus(JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(component);
        stepStatusPanel.add(panel);
    }

    /**
     * Add title
     *
     * @param description Title text
     */
    protected void addTitleToStepStatus(String description) {
        JLabel label = new JLabel(description);
        Font font = new Font("Arial", Font.BOLD, 18);
        label.setFont(font);
        addToStepStatus(label);
    }

    public int getNumber() {
        return number;
    }

    public MatrixForm getForm() {
        return form;
    }

    public FormEvent getEvent() {
        return event;
    }
}
