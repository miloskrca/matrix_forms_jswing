package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.RationalCanonicalMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.Handler.MatrixHandler;
import rs.etf.km123247m.Observer.Event.FormEvent;

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
        MatrixHandler handler = rForm.getHandler();
        switch (getNumber()) {
            case START:
                matrices.add(new MatrixEntry("A", handler.duplicate(rForm.getStartMatrix())));
                break;
            case INFO:
                matrices.add(new MatrixEntry("A_I", handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()))));
                break;
            case END:
                matrices.add(new MatrixEntry("A", handler.duplicate(rForm.getStartMatrix())));
                matrices.add(new MatrixEntry("R", handler.duplicate(rForm.getFinalMatrix())));
                matrices.add(new MatrixEntry("T", handler.duplicate(rForm.getT())));
                break;
            default:
                //step
                matrices.add(new MatrixEntry("P[" + rForm.getRound() + "]", handler.duplicate(rForm.getP(rForm.getRound()))));
                matrices.add(new MatrixEntry("A_I", handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()))));
                matrices.add(new MatrixEntry("Q[" + rForm.getRound() + "]", handler.duplicate(rForm.getQ(rForm.getRound()))));
        }
    }

    public String getDescription() {
        String title = "\\begin{array}{l}";
        switch (getNumber()) {
            case START:
                title += "\\text{\\LARGE Start }\\cr \\text{\\Large Starting transformation to Rational canonical form for matrix:}";
                break;
            case INFO:
                title += "\\text{\\LARGE Info }\\cr \\text{\\Large " + getEvent().getMessage() + "}";
                break;
            case END:
                title += "\\text{\\LARGE Finish }\\cr \\text{\\Large Transformation ended. Result:}";
                break;
            default:
                //step
                title += "\\text{\\LARGE Step " + getNumber() + "}\\cr \\text{\\Large " + getCommandDescription() + " }";
        }

        return title + "\\end{array}";
    }
}
