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
        switch (getNumber()) {
            case START:
                matrix = handler.duplicate(jForm.getStartMatrix());
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                break;
            case INFO:
                matrix = handler.duplicate(jForm.getTransitionalMatrix());
                addToStepStatus(new JLabel("Current state of matrix [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
                if(getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    addFixingDiagonalExplanation();
                } else if(getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    addSubtractForSmithExplanation(jForm.getStartMatrix());
                }
                matrices.add(new MatrixEntry("A_I", matrix));
                break;
            case END:
                matrix = handler.duplicate(jForm.getStartMatrix());
                addToStepStatus(new JLabel("Starting matrix [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(jForm.getFinalMatrix());
                addToStepStatus(new JLabel("Transformed matrix [J]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("J", matrix)));
                matrices.add(new MatrixEntry("J", matrix));
                break;
            default:
                //step
                matrix = handler.duplicate(jForm.getTransitionalMatrix());
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", matrix));
        }
    }

    @Override
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
                } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    title += "Title INFO_SUBTRACT_FOR_SMITH.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_FIX_LEADING_COEFFICIENTS)) {
                    title += "Title INFO_FIX_LEADING_COEFFICIENTS.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_LEADING_COEFFICIENTS)) {
                    title += "Title INFO_END_FIX_LEADING_COEFFICIENTS.";
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
