package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.SmithMatrixForm;
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
public class SmithStep extends AbstractStep {

    public SmithStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        super(number, command, event, form);
    }

    @Override
    protected void saveStepStatusPanel() throws Exception {
        SmithMatrixForm sForm = (SmithMatrixForm) getForm();
        IMatrix matrix;
        MatrixHandler handler = sForm.getHandler();
        switch (getNumber()) {
            case START:
                matrix = handler.duplicate(sForm.getStartMatrix());
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                break;
            case INFO:
                matrix = handler.duplicate(sForm.getFinalMatrix());
                addToStepStatus(new JLabel("Current state of matrix [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
                if(getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    addFixingDiagonalExplanation();
                }
                matrices.add(new MatrixEntry("A_I", matrix));
                break;
            case END:
                matrix = handler.duplicate(sForm.getStartMatrix());
                addToStepStatus(new JLabel("Starting matrix [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(sForm.getFinalMatrix());
                addToStepStatus(new JLabel("Transformed matrix [S]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("S", matrix)));
                matrices.add(new MatrixEntry("S", matrix));
                break;
            default:
                //step
                matrix = handler.duplicate(sForm.getFinalMatrix());
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", sForm.getHandler().duplicate(sForm.getFinalMatrix())));
        }
    }

    @Override
    public String getDescription() {
        String title = "";
        switch (getNumber()) {
            case START:
                title = "Starting transformation to Smith normal form for matrix [A]:";
                break;
            case INFO:
                if(getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Elements on diagonal need fixing.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Finished fixing elements on diagonal.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_FIX_LEADING_COEFFICIENTS)) {
                    title += "Title INFO_FIX_LEADING_COEFFICIENTS.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_LEADING_COEFFICIENTS)) {
                    title += "Title INFO_END_FIX_LEADING_COEFFICIENTS.";
                } else {
                    title += getEvent().getMessage() + ".";
                }
                break;
            case END:
                title = "Transformation ended.";
                break;
            default:
                //step
                title = getCommandDescription();
        }

        return title;
    }
}
