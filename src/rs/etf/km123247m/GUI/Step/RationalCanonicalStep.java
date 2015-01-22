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
        stepStatusPanel.add(new JLabel("<html>" + getDescription() + "</html>"));
        switch (getNumber()) {
            case START:
                matrix = handler.duplicate(rForm.getStartMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                break;
            case INFO:
                matrix = handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", matrix));
                break;
            case END:
                matrix = handler.duplicate(rForm.getStartMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(rForm.getFinalMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("R", matrix)));
                matrices.add(new MatrixEntry("R", matrix));
                matrix = handler.duplicate(rForm.getT());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("T", matrix)));
                matrices.add(new MatrixEntry("T", matrix));
                break;
            default:
                //step
                matrix = handler.duplicate(rForm.getP(rForm.getRound()));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("P[" + rForm.getRound() + "]", matrix)));
                matrices.add(new MatrixEntry("P[" + rForm.getRound() + "]", matrix));
                matrix = handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", matrix));
                matrix = handler.duplicate(rForm.getQ(rForm.getRound()));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("Q[" + rForm.getRound() + "]", matrix)));
                matrices.add(new MatrixEntry("Q[" + rForm.getRound() + "]", matrix));
        }
    }

    public String getDescription() {
        String title = "";
        switch (getNumber()) {
            case START:
                title += "Starting transformation to Rational canonical form for matrix:";
                break;
            case INFO:
                title += getEvent().getMessage() + ".";
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
