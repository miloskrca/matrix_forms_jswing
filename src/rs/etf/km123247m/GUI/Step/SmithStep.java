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
        stepStatusPanel.add(new JLabel("<html>" + getDescription() + "</html>"));
        switch (getNumber()) {
            case START:
                matrix = handler.duplicate(sForm.getStartMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                break;
            case INFO:
                matrix = handler.duplicate(sForm.getFinalMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", matrix));
                break;
            case END:
                matrix = handler.duplicate(sForm.getStartMatrix());
                stepStatusPanel.add(new JLabel("Initial matrix [A]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(sForm.getFinalMatrix());
                stepStatusPanel.add(new JLabel("Transformed matrix [S]:"));
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("S", matrix)));
                matrices.add(new MatrixEntry("S", matrix));
                break;
            default:
                //step
                matrix = handler.duplicate(sForm.getFinalMatrix());
                stepStatusPanel.add(getLaTexPanel(generateLatexMatrix("A_I", matrix)));
                matrices.add(new MatrixEntry("A_I", sForm.getHandler().duplicate(sForm.getFinalMatrix())));
        }
    }

    public String getDescription() {
        String title;
        switch (getNumber()) {
            case START:
                title = "Starting transformation to Smith normal form for matrix [A]:";
                break;
            case INFO:
                title = getEvent().getMessage() + ".";
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
