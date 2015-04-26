package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.JordanMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.Handler.MatrixHandler;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Observer.Event.FormEvent;

import javax.swing.*;
import java.util.ArrayList;

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
                if (getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    addFixingDiagonalExplanation();
                } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    addSubtractForSmithExplanation(jForm.getStartMatrix());
                } else if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_BLOCKS)) {
                    addGenerateBlocksForJordanExplanation();
                } else if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_END_GENERATE_BLOCKS)) {
                    addEndGenerateBlocksForJordanExplanation(jForm);
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

    /**
     * Add generate blocks explanation
     */
    private void addGenerateBlocksForJordanExplanation() {
        addToStepStatus(new JLabel("We need to generate Jordan blocks from the current matrix [A]"));
    }

    /**
     * Add explanation after generating blocks
     * @param jForm JordanMatrixForm
     */
    private void addEndGenerateBlocksForJordanExplanation(JordanMatrixForm jForm) {
        addToStepStatus(new JLabel("Roots:"));
        ArrayList<ArrayList<Object>> roots = jForm.getRoots();
        for (int i = 0; i < roots.size(); i++) {
            for (int j = 0; j < roots.get(i).size(); j++) {
                addToStepStatus(getLaTexLabel( "x_" + (i + j) + "=" + getLatexFromMatrixElement(roots.get(i).get(j))));
            }
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
                if (getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Elements on diagonal need fixing.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Finished fixing elements on diagonal.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    title += "Title INFO_SUBTRACT_FOR_SMITH.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_FIX_LEADING_COEFFICIENTS)) {
                    title += "Title INFO_FIX_LEADING_COEFFICIENTS.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_LEADING_COEFFICIENTS)) {
                    title += "Title INFO_END_FIX_LEADING_COEFFICIENTS.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_BLOCKS)) {
                    title += "Title INFO_JORDAN_GENERATE_BLOCKS.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_END_GENERATE_BLOCKS)) {
                    title += "Title INFO_JORDAN_END_GENERATE_BLOCKS.";
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

    /**
     * Return title with custom behaviour for Jordan form
     *
     * @return String
     */
    public String getTitle() {
        switch (getNumber()) {
            case START:
                return "Start";
            case INFO:
                if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_END_GENERATE_BLOCKS)
                        || getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_BLOCKS)
                        ) {
                    return "Info step";
                }
                return "Info";
            case END:
                return "Finish";
            default:
                return "Step " + getNumber();
        }
    }
}
