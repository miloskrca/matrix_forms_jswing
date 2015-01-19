package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Command.MatrixCommand.*;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Observer.Event.FormEvent;

import javax.swing.*;
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
            saveStepStatusPanel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract void saveStepStatusPanel() throws Exception;

    public static LaTexLabel getLaTexPanel(String formula) {
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
        String f = name +" = \\begin{bmatrix}";
        for (int row = 0; row < matrix.getRowNumber(); row++) {
            for (int column = 0; column < matrix.getColumnNumber(); column++) {
                f += matrix.get(row, column).getElement().toString();
                if(column < matrix.getColumnNumber() - 1) {
                    f += " & ";
                }
            }
            f += " \\\\";
        }
        f += "\\end{bmatrix}";

        return f;
    }
    protected String generateMupadMatrix(String name, IMatrix matrix) throws Exception {
        String f = name + " := matrix([";
        for (int row = 0; row < matrix.getRowNumber(); row++) {
            f += "[";
            for (int column = 0; column < matrix.getColumnNumber(); column++) {
                f += matrix.get(row, column).getElement().toString();
                if(column < matrix.getColumnNumber() - 1) {
                    f += ",";
                }
            }
            f += "]";
            if(row < matrix.getRowNumber() - 1) {
                f += ",";
            }
        }
        f += "])";

        return f;
    }
    public String getMuPadCommands() throws Exception {
        String mupadCommands = "";
        for(Map.Entry<String, IMatrix> entry: matrices) {
            mupadCommands += "\n" + generateMupadMatrix(entry.getKey(), entry.getValue());
        }

        return mupadCommands;
    }

    public String getCommandDescription() {
        String description = "";
        if(command != null) {
            String commandClass = command.getClass().getSimpleName();
            if(commandClass.equals("SwitchColumnsCommand")) {
                SwitchColumnsCommand comm = (SwitchColumnsCommand)command;
                description = "Switching columns " + comm.getColumn1() + " and " + comm.getColumn2() + ".";
            } else if (commandClass.equals("SwitchRowsCommand")) {
                SwitchRowsCommand comm = (SwitchRowsCommand)command;
                description = "Switching rows " + comm.getRow1() + " and " + comm.getRow2() + ".";
            } else if (commandClass.equals("MultiplyRowWithElementAndStoreCommand")) {
                MultiplyRowWithElementAndStoreCommand comm = (MultiplyRowWithElementAndStoreCommand)command;
                description = "Multiplying row "
                        + comm.getRow() + " with element "
                        + comm.getElement().toString() + ".";
            } else if (commandClass.equals("MultiplyRowWithElementAndAddToRowAndStoreCommand")) {
                MultiplyRowWithElementAndAddToRowAndStoreCommand comm = (MultiplyRowWithElementAndAddToRowAndStoreCommand)command;
                description = "Multiplying row "
                        + comm.getRow1() + " with element "
                        + comm.getElement().toString() + " and saving to row "
                        + comm.getRow2() + ".";
            } else if (commandClass.equals("MultiplyColumnWithElementAndStoreCommand")) {
                MultiplyColumnWithElementAndStoreCommand comm = (MultiplyColumnWithElementAndStoreCommand)command;
                description = "Multiplying column "
                        + comm.getColumn() + " with element "
                        + comm.getElement().toString() + ".";
            } else if (commandClass.equals("MultiplyColumnWithElementAndAddToColumnAndStoreCommand")) {
                MultiplyColumnWithElementAndAddToColumnAndStoreCommand comm = (MultiplyColumnWithElementAndAddToColumnAndStoreCommand)command;
                description = "Multiplying column "
                        + comm.getColumn1() + " with element "
                        + comm.getElement().toString() + " and saving to column "
                        + comm.getColumn2() + ".";
            } else if (commandClass.equals("AddRowsAndStoreCommand")) {
                AddRowsAndStoreCommand comm = (AddRowsAndStoreCommand)command;
                description = "Adding rows " + comm.getRow1() + " and " + comm.getRow2() + ".";
            } else if (commandClass.equals("AddColumnsAndStoreCommand")) {
                AddColumnsAndStoreCommand comm = (AddColumnsAndStoreCommand)command;
                description = "Adding columns " + comm.getColumn1() + " and " + comm.getColumn2() + ".";
            } else {
                description = commandClass;
            }
        }

        return description;
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
