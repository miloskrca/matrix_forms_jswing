package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Command.MatrixCommand.SwitchColumnsCommand;
import rs.etf.km123247m.Command.MatrixCommand.SwitchRowsCommand;
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
                addToStepStatus(new JLabel("Trenutno stanje matrice:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
                if (getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    addFixingDiagonalExplanation();
                } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    addSubtractForSmithExplanation(rForm.getStartMatrix());
                }
                matrices.add(new MatrixEntry("A_I", matrix));
                break;
            case END:
                matrix = handler.duplicate(rForm.getStartMatrix());
                addToStepStatus(new JLabel("Početna matrica [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(rForm.getFinalMatrix());
                addToStepStatus(new JLabel("Transformisana matrica [R]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("R", matrix)));
                matrices.add(new MatrixEntry("R", matrix));
                matrix = handler.duplicate(rForm.getT());
                addToStepStatus(new JLabel("Matrica [T]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("T", matrix)));
                addToStepStatus(getLaTexLabel("T^{-1}*A*T = R(A)"));
                addToStepStatus(new JLabel("R(A): Transformacija matrice [A] u racionalnu formu"));
                matrices.add(new MatrixEntry("T", matrix));
                break;
            default:
                //step
//                matrix = handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()));
//                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix, getCommand())));
//                matrices.add(new MatrixEntry("A_I", matrix));

                boolean inverse = getCommand() instanceof SwitchRowsCommand
                        || getCommand() instanceof SwitchColumnsCommand;

                matrix = getCommand().getMatrixBefore();
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_1", matrix, getCommand(), false)));
                matrices.add(new MatrixEntry("A_1", matrix));
                matrix = getCommand().getMatrixAfter();
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_2", matrix, getCommand(), inverse)));
                matrices.add(new MatrixEntry("A_2", matrix));

                matrix = handler.duplicate(rForm.getP(rForm.getRound()));
                addToStepStatus(new JLabel("Trenutno stanje matrice [P]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("P[" + rForm.getRound() + "]", matrix)));
                matrices.add(new MatrixEntry("P[" + rForm.getRound() + "]", matrix));
                matrix = handler.duplicate(rForm.getQ(rForm.getRound()));
                addToStepStatus(new JLabel("Trenutno stanje matrice [Q]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("Q[" + rForm.getRound() + "]", matrix)));
                matrices.add(new MatrixEntry("Q[" + rForm.getRound() + "]", matrix));
                addToStepStatus(new JLabel("P: Oslikava operacija nad redovima."));
                addToStepStatus(new JLabel("Q: Oslikava operacija nad kolonama."));
        }
    }

    @Override
    public String getDescription() {
        String title = "";
        switch (getNumber()) {
            case START:
                title += "Početak transformacije matrice [A] u racionalnu kanonsku formu:";
                break;
            case INFO:
                if (getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Elemente na dijagonali treba ispraviti.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title += "Završetak ispravke elemenata na dijagonali.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    title += "Oduzimanje matrice [A] od jedinične, dijagonalne, matrice pomnožene sa X.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_FINISH_RATIONAL_START_T)) {
                    title += "Početak generisanja matrice [T] od rezultujuće matrice [R].";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_FIX_LEADING_COEFFICIENTS)) {
                    title += "Redukcija koeficijenata uz elemente sa najvećim stepenom na 1.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_LEADING_COEFFICIENTS)) {
                    title += "Kraj redukcije koeficijenata uz elemente sa najvećim stepenom na 1.";
                } else {
                    title += getEvent().getMessage() + ".";
                }
                break;
            case END:
                title += "Transformacija je završena.";
                break;
            default:
                //step
                title += getCommandDescription();
        }

        return title;
    }
}
