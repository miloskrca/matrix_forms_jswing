package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.JordanMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.Handler.MatrixHandler;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Observer.Event.FormEvent;

import javax.swing.*;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public class JordanStep extends AbstractStep {
    public JordanStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        super(number, command, event, form);
    }

    @Override
    protected void saveStepStatusPanel() throws Exception {
        JordanMatrixForm jForm = (JordanMatrixForm) getForm();
        IMatrix matrix;
        MatrixHandler handler = jForm.getHandler();
        stepStatusPanel.add(new JLabel("<html><h3>" + getDescription() + "</h3></html>"));
        switch (getNumber()) {
            case START:
                matrix = handler.duplicate(jForm.getStartMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                break;
            case INFO:
                matrix = handler.duplicate(jForm.getTransitionalMatrix());
                stepStatusPanel.add(new JLabel("Current state of matrix [A]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A_I", matrix)));
                if(getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    addFixingDiagonalExplanation();
                } else if(getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    // TODO: matrices are empty for some reason
//                    addSubtractForSmithExplanation(matrices.get(matrices.size() - 1).getValue());
                }
                matrices.add(new MatrixEntry("A_I", matrix));
                break;
            case END:
                matrix = handler.duplicate(jForm.getStartMatrix());
                stepStatusPanel.add(new JLabel("Starting matrix [A]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(jForm.getFinalMatrix());
                stepStatusPanel.add(new JLabel("Transformed matrix [J]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("J", matrix)));
                matrices.add(new MatrixEntry("J", matrix));
                break;
            default:
                //step
                matrix = handler.duplicate(jForm.getTransitionalMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", matrix));
        }
    }

    public String getDescription() {
        String title = "";
        switch (getNumber()) {
            case START:
                title += "Starting transformation to Jordan canonical form for matrix:";
                break;
            case INFO:
                if(getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Elements on diagonal need fixing.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Finished fixing elements on diagonal.";
                } else {
                    title += getEvent().getMessage() + ".";
                }
                break;
            case END:
                title += "Transformation ended.";
                break;
            default:
                //step
                title += getCommandDescription();
        }

        return title;
    }
}
