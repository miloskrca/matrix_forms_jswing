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
        stepStatusPanel.add(new JLabel("<html><h3>" + getDescription() + "</h3></html>"));
        switch (getNumber()) {
            case START:
                matrix = handler.duplicate(rForm.getStartMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                break;
            case INFO:
                matrix = handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()));
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
                matrix = handler.duplicate(rForm.getStartMatrix());
                stepStatusPanel.add(new JLabel("Starting matrix [A]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(rForm.getFinalMatrix());
                stepStatusPanel.add(new JLabel("Transformed matrix [R]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("R", matrix)));
                matrices.add(new MatrixEntry("R", matrix));
                matrix = handler.duplicate(rForm.getT());
                stepStatusPanel.add(new JLabel("Matrix [T]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("T", matrix)));
                // TODO: T^-1 is not working correctly for LaTex
                stepStatusPanel.add(getLaTexPanel("T^-1*A*T = R(A)"));
                stepStatusPanel.add(new JLabel("R(A): Transformation of matrix A to rational form"));
                matrices.add(new MatrixEntry("T", matrix));
                break;
            default:
                //step
                matrix = handler.duplicate(rForm.getP(rForm.getRound()));
                stepStatusPanel.add(new JLabel("Current state of matrix [P]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("P[" + rForm.getRound() + "]", matrix)));
                matrices.add(new MatrixEntry("P[" + rForm.getRound() + "]", matrix));
                matrix = handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()));
                stepStatusPanel.add(new JLabel("Current state of matrix [A]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", matrix));
                matrix = handler.duplicate(rForm.getQ(rForm.getRound()));
                stepStatusPanel.add(new JLabel("Current state of matrix [Q]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("Q[" + rForm.getRound() + "]", matrix)));
                matrices.add(new MatrixEntry("Q[" + rForm.getRound() + "]", matrix));
                stepStatusPanel.add(new JLabel("P: Reflects operations on rows."));
                stepStatusPanel.add(new JLabel("Q: Reflects operations on columns."));
        }
    }

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
