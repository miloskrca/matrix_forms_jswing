package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.JordanMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Observer.Event.FormEvent;

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
    protected void saveMatricesForTheCurrentState() throws Exception {
        JordanMatrixForm jForm = (JordanMatrixForm) getForm();
        switch (getNumber()) {
            case START:
                matrices.add(new MatrixEntry("A", jForm.getHandler().duplicate(jForm.getStartMatrix())));
                break;
            case INFO:
                matrices.add(new MatrixEntry("A_I", jForm.getHandler().duplicate(jForm.getTransitionalMatrix())));
                break;
            case END:
                matrices.add(new MatrixEntry("A", jForm.getHandler().duplicate(jForm.getStartMatrix())));
                matrices.add(new MatrixEntry("J", jForm.getHandler().duplicate(jForm.getFinalMatrix())));
                break;
            default:
                //step
                matrices.add(new MatrixEntry("A_I", jForm.getHandler().duplicate(jForm.getTransitionalMatrix())));
        }
    }

    @Override
    public String getDescription() {
        String title = "\\begin{array}{l}";
        switch (getNumber()) {
            case START:
                title += "\\text{\\LARGE Start }\\cr \\text{\\Large Starting transformation to Jordan canonical form for matrix:}";
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
