package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.RationalCanonicalMatrixForm;
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
public class RationalCanonicalStep extends AbstractStep {

    public RationalCanonicalStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        super(number, command, event, form);
    }

    @Override
    protected void saveStepStatusPanel() throws Exception {
        RationalCanonicalMatrixForm rForm = (RationalCanonicalMatrixForm) getForm();
        IMatrix matrix;
        MatrixHandler handler = rForm.getHandler();
        switch (getNumber()) {
            case START:
                matrix = handler.duplicate(rForm.getStartMatrix());
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                break;
            case INFO:
                matrix = handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()));
                addToStepStatus(new JLabel("Current state of matrix [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
                if(getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    addFixingDiagonalExplanation();
                } else if(getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    addSubtractForSmithExplanation(rForm.getStartMatrix());
                }
                matrices.add(new MatrixEntry("A_I", matrix));
                break;
            case END:
                matrix = handler.duplicate(rForm.getStartMatrix());
                addToStepStatus(new JLabel("Starting matrix [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(rForm.getFinalMatrix());
                addToStepStatus(new JLabel("Transformed matrix [R]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("R", matrix)));
                matrices.add(new MatrixEntry("R", matrix));
                matrix = handler.duplicate(rForm.getT());
                addToStepStatus(new JLabel("Matrix [T]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("T", matrix)));
                addToStepStatus(getLaTexLabel("T^{-1}*A*T = R(A)"));
                addToStepStatus(new JLabel("R(A): Transformation of matrix A to rational form"));
                matrices.add(new MatrixEntry("T", matrix));
                break;
            default:
                //step
                matrix = handler.duplicate(rForm.getP(rForm.getRound()));
                addToStepStatus(new JLabel("Current state of matrix [P]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("P[" + rForm.getRound() + "]", matrix)));
                matrices.add(new MatrixEntry("P[" + rForm.getRound() + "]", matrix));
                matrix = handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()));
                addToStepStatus(new JLabel("Current state of matrix [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", matrix));
                matrix = handler.duplicate(rForm.getQ(rForm.getRound()));
                addToStepStatus(new JLabel("Current state of matrix [Q]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("Q[" + rForm.getRound() + "]", matrix)));
                matrices.add(new MatrixEntry("Q[" + rForm.getRound() + "]", matrix));
                addToStepStatus(new JLabel("P: Reflects operations on rows."));
                addToStepStatus(new JLabel("Q: Reflects operations on columns."));
        }
    }

    @Override
    public String getDescription() {
        String title = "";
        switch (getNumber()) {
            case START:
                title += "Starting transformation to Rational canonical form for matrix:";
                break;
            case INFO:
                if(getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Elements on diagonal need fixing.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Finished fixing elements on diagonal.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    title += "Title INFO_SUBTRACT_FOR_SMITH.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_FINISH_RATIONAL_START_T)) {
                    title += "Title INFO_RATIONAL_FINISH_RATIONAL_START_T.";
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
