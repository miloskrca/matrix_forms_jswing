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
    private static final String FIRST_COLOR = "White";
    private static final String SECOND_COLOR = "GreenYellow";
    private int number;

    public final static String START_MARKER = "###LaTexLabelStart###";
    public final static String END_MARKER = "###LaTexLabelEnd###";

    private MatrixForm form;
    private FormEvent event;

    private ICommand command;
    protected ArrayList<Map.Entry<String, IMatrix>> matrices = new ArrayList<Map.Entry<String, IMatrix>>();
    protected JPanel stepStatusPanel = new JPanel();
    private Font font;

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
                return "Početak";
            case INFO:
                return "Info";
            case END:
                return "Kraj";
            default:
                return "Korak " + number;
        }
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
     *
     * @return String containing MuPad commands for getting the matrices in MuPad
     * @throws Exception
     */
    public String getMuPadCommands() throws Exception {
        String mupadCommands = "";
        for (Map.Entry<String, IMatrix> entry : matrices) {
            mupadCommands += "\n" + generateMupadMatrix(entry.getKey(), entry.getValue()) + ";";
        }
        if (getNumber() == END) {
            String className = this.getClass().getName();
            mupadCommands += "\n";
            if (className.contains("Jordan")) {
                mupadCommands += "J1 := linalg::jordanForm(A);";
            } else if (className.contains("RationalCanonical")) {
                mupadCommands += "R1 := linalg::frobeniusForm(A);";
                mupadCommands += "\nR2 := T^(-1) * A * T;";
            } else if (className.contains("Smith")) {
                mupadCommands += "S1 := linalg::smithForm(A);";
            }
        }

        return mupadCommands;
    }

    /**
     *
     * @param name String
     * @param matrix IMatrix
     * @param command ICommand
     * @param inverseColors boolean
     *
     * @return String
     * @throws Exception
     */
    protected String generateLatexMatrix(String name, IMatrix matrix, ICommand command, boolean inverseColors) throws Exception {
        String f = (name != null ? name + " = " : "") + "\\begin{bmatrix}";
        for (int row = 0; row < matrix.getRowNumber(); row++) {
            for (int column = 0; column < matrix.getColumnNumber(); column++) {
                f += getLatexFromMatrixElement(
                        matrix.get(row, column).getElement(),
                        getColorFromCommand(command, row, column, inverseColors)
                );
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
     * @param command ICommand
     * @param row     int
     * @param column  int
     * @param inverse  boolean
     * @return String
     */
    protected String getColorFromCommand(ICommand command, int row, int column, boolean inverse) {
        String color = null;
        String firstColor = inverse ? SECOND_COLOR : FIRST_COLOR;
        String secondColor = inverse ? FIRST_COLOR : SECOND_COLOR;
        if (command != null) {
            if (command instanceof SwitchColumnsCommand) {
                SwitchColumnsCommand comm = (SwitchColumnsCommand) command;
                if (comm.getColumn1() == column) {
                    color = firstColor;
                } else if (comm.getColumn2() == column) {
                    color = secondColor;
                }
            } else if (command instanceof SwitchRowsCommand) {
                SwitchRowsCommand comm = (SwitchRowsCommand) command;
                if (comm.getRow1() == row) {
                    color = firstColor;
                } else if (comm.getRow2() == row) {
                    color = secondColor;
                }
            } else if (command instanceof MultiplyRowWithElementAndStoreCommand) {
                MultiplyRowWithElementAndStoreCommand comm = (MultiplyRowWithElementAndStoreCommand) command;
                if (comm.getRow() == row) {
                    color = firstColor;
                }
            } else if (command instanceof MultiplyRowWithElementAndAddToRowAndStoreCommand) {
                MultiplyRowWithElementAndAddToRowAndStoreCommand comm =
                        (MultiplyRowWithElementAndAddToRowAndStoreCommand) command;
                if (comm.getRow1() == row) {
                    color = firstColor;
                } else if (comm.getRow2() == row) {
                    color = secondColor;
                }
            } else if (command instanceof MultiplyColumnWithElementAndStoreCommand) {
                MultiplyColumnWithElementAndStoreCommand comm = (MultiplyColumnWithElementAndStoreCommand) command;
                if (comm.getColumn() == column) {
                    color = firstColor;
                }
            } else if (command instanceof MultiplyColumnWithElementAndAddToColumnAndStoreCommand) {
                MultiplyColumnWithElementAndAddToColumnAndStoreCommand comm =
                        (MultiplyColumnWithElementAndAddToColumnAndStoreCommand) command;
                if (comm.getColumn1() == column) {
                    color = firstColor;
                } else if (comm.getColumn2() == column) {
                    color = secondColor;
                }
            } else if (command instanceof AddRowsAndStoreCommand) {
                AddRowsAndStoreCommand comm = (AddRowsAndStoreCommand) command;
                if (comm.getRow1() == row) {
                    color = firstColor;
                } else if (comm.getRow2() == row) {
                    color = secondColor;
                }
            } else if (command instanceof AddColumnsAndStoreCommand) {
                AddColumnsAndStoreCommand comm = (AddColumnsAndStoreCommand) command;
                if (comm.getColumn1() == column) {
                    color = firstColor;
                } else if (comm.getColumn2() == column) {
                    color = secondColor;
                }
            }

        }
        return color;
    }

    protected String generateLatexMatrix(String name, IMatrix matrix) throws Exception {
        return generateLatexMatrix(name, matrix, null, false);
    }

    /**
     * Returns a LaTex string from an object
     *
     * @param object Object
     * @return LaTex string
     */
    public String getLatexFromMatrixElement(Object object) {
        return getLatexFromMatrixElement(object, null);
    }

    /**
     * Returns a LaTex string from an object
     *
     * @param object Object
     * @param color  String
     * @return LaTex string
     */
    public String getLatexFromMatrixElement(Object object, String color) {
        TeXFormFactory f = new TeXFormFactory();
        StringBuffer sb = new StringBuffer();
        f.convert(sb, object, 0);
        if (color != null) {
            return colorString(sb.toString(), color);
        } else {
            return sb.toString();
        }
    }

    protected String colorString(String string, String color) {
        return "\\colorbox{" + color + "}{" + string + "}";
    }

    protected String colorInt(int integer, String color) {
        return START_MARKER + colorString(String.valueOf(integer), color) + END_MARKER;
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
                description = "Zamena kolona " + colorInt(comm.getColumn1(), FIRST_COLOR)
                        + " i " + colorInt(comm.getColumn2(), SECOND_COLOR) + ".";
            } else if (commandClass.equals("SwitchRowsCommand")) {
                SwitchRowsCommand comm = (SwitchRowsCommand) command;
                description = "Zamena redova " + colorInt(comm.getRow1(), FIRST_COLOR)
                        + " i " + colorInt(comm.getRow2(), SECOND_COLOR) + ".";
            } else if (commandClass.equals("MultiplyRowWithElementAndStoreCommand")) {
                MultiplyRowWithElementAndStoreCommand comm = (MultiplyRowWithElementAndStoreCommand) command;
                description = "Množenje reda "
                        + colorInt(comm.getRow(), FIRST_COLOR) + " sa "
                        + START_MARKER + comm.getElement().toString() + END_MARKER + ".";
            } else if (commandClass.equals("MultiplyRowWithElementAndAddToRowAndStoreCommand")) {
                MultiplyRowWithElementAndAddToRowAndStoreCommand comm =
                        (MultiplyRowWithElementAndAddToRowAndStoreCommand) command;
                description = "Množenje reda "
                        + colorInt(comm.getRow1(), FIRST_COLOR) + " sa "
                        + START_MARKER + comm.getElement().toString() + END_MARKER + " i dodavanje redu "
                        + colorInt(comm.getRow2(), SECOND_COLOR) + ".";
            } else if (commandClass.equals("MultiplyColumnWithElementAndStoreCommand")) {
                MultiplyColumnWithElementAndStoreCommand comm = (MultiplyColumnWithElementAndStoreCommand) command;
                description = "Množenje kolone "
                        + colorInt(comm.getColumn(), FIRST_COLOR) + " sa "
                        + START_MARKER + comm.getElement().toString() + END_MARKER + ".";
            } else if (commandClass.equals("MultiplyColumnWithElementAndAddToColumnAndStoreCommand")) {
                MultiplyColumnWithElementAndAddToColumnAndStoreCommand comm =
                        (MultiplyColumnWithElementAndAddToColumnAndStoreCommand) command;
                description = "Množenje kolone "
                        + colorInt(comm.getColumn1(), FIRST_COLOR) + " sa "
                        + START_MARKER + comm.getElement().toString() + END_MARKER + " i dodavanje koloni "
                        + colorInt(comm.getColumn2(), SECOND_COLOR) + ".";
            } else if (commandClass.equals("AddRowsAndStoreCommand")) {
                AddRowsAndStoreCommand comm = (AddRowsAndStoreCommand) command;
                description = "Sabiranje redova " + colorInt(comm.getRow1(), FIRST_COLOR)
                        + " i " + colorInt(comm.getRow2(), SECOND_COLOR) + ".";
            } else if (commandClass.equals("AddColumnsAndStoreCommand")) {
                AddColumnsAndStoreCommand comm = (AddColumnsAndStoreCommand) command;
                description = "Sabiranje kolona " + colorInt(comm.getColumn1(), FIRST_COLOR)
                        + " i " + colorInt(comm.getColumn2(), SECOND_COLOR) + ".";
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
        addToStepStatus(new JLabel("Treba popraviti elemente na dijagonali ako sledeće nije istinito:"));
        addToStepStatus(getLaTexLabel("b_1(x)|b_2(x)|...|b_m(x)"));
        addToStepStatus(new JLabel("Za matricu B:"));
        addToStepStatus(getLaTexLabel(getExampleLatexMatrix()));
    }

    /**
     * Add explanation how are we preparing the matrix for smith transformation to display panel.
     *
     * @param matrix Matrix that is subtracted from xI
     */
    protected void addSubtractForSmithExplanation(IMatrix matrix) throws Exception {
        addToStepStatus(new JLabel("Od dijagonalne matrice treba oduzeti matricu"));
        addToStepStatus(new JLabel("pre nego što se transformiše u Smitovu formu:"));
        addToStepStatus(getLaTexLabel("A_I = x*I-A"));
        addToStepStatus(new JComponent[]{
                getLaTexLabel(generateLatexMatrix("I",
                                form.getHandler().diagonal(
                                        matrix.getRowNumber(),
                                        form.getHandler().getObjectFromString(String.valueOf(Term.X))
                                )
                        )
                ),
                getLaTexLabel(generateLatexMatrix("A", matrix))
        });
    }

    /**
     * Adds component to the panel and stack them horizontally
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
        Font font = getTitleFont();
        JLabel stepTitle = new JLabel(getTitle());
        stepTitle.setFont(font);
        addToStepStatus(stepTitle);

        String[] tokens = description.split(START_MARKER + "|" + END_MARKER);
        JComponent[] components = new JComponent[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            if (i % 2 == 0) {
                // even == just text
                JLabel label = new JLabel(tokens[i]);
                label.setFont(font);
                components[i] = label;
            } else {
                // odd == formula
                components[i] = getLaTexLabel(tokens[i]);
            }
        }
        addToStepStatus(components);
    }

    /**
     * Get title font
     *
     * @return Font
     */
    protected Font getTitleFont() {
        if (font == null) {
            font = new Font("Arial", Font.BOLD, 18);
        }

        return font;
    }

    /**
     * Adds before and after matrices to step status and adds the matrices to step matrices array for
     * later usage (e.g. generate MuPad command)
     *
     * @throws Exception
     */
    protected void addBeforeAndAfterMatrices(String matrixLetter) throws Exception {
        boolean inverse = getCommand() instanceof SwitchRowsCommand
                || getCommand() instanceof SwitchColumnsCommand;

        String beforeMatrixName = matrixLetter + "_{Ipred}";
        String afterMatrixName = matrixLetter + "_{I}";

        IMatrix matrix = getCommand().getMatrixBefore();
        addToStepStatus(getLaTexLabel(generateLatexMatrix(beforeMatrixName, matrix, getCommand(), false)));
        matrices.add(new MatrixEntry(beforeMatrixName, matrix));
        matrix = getCommand().getMatrixAfter();
        addToStepStatus(getLaTexLabel(generateLatexMatrix(afterMatrixName, matrix, getCommand(), inverse)));
        matrices.add(new MatrixEntry(afterMatrixName, matrix));
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

    public ICommand getCommand() {
        return command;
    }
}
