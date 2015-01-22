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
    protected void saveStepStatusPanel() throws Exception {
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

    public String getDescription() {
        String title = "";
        switch (getNumber()) {
            case START:
                title += "Starting transformation to Jordan canonical form for matrix:";
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
