package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Command.MatrixCommand.SwitchColumnsCommand;
import rs.etf.km123247m.Command.MatrixCommand.SwitchRowsCommand;
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
                addToStepStatus(new JLabel("Trenutno stanje matrice:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
                if (getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    addFixingDiagonalExplanation();
                } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    addSubtractForSmithExplanation(jForm.getStartMatrix());
                }
                matrices.add(new MatrixEntry("A_I", matrix));
                break;
            case END:
                matrix = handler.duplicate(jForm.getStartMatrix());
                addToStepStatus(new JLabel("Početna matrica [A]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
                matrices.add(new MatrixEntry("A", matrix));
                matrix = handler.duplicate(jForm.getFinalMatrix());
                addToStepStatus(new JLabel("Transformisana matrica [J]:"));
                addToStepStatus(getLaTexLabel(generateLatexMatrix("J", matrix)));
                matrices.add(new MatrixEntry("J", matrix));
                break;
            default:
                //step
                if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_FACTORS)) {
                    addGenerateFactorsForJordanExplanation(jForm);
                } else if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_BLOCKS)) {
                    addGenerateBlocksForJordanExplanation(jForm);
                } else if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_END_GENERATE_BLOCKS)) {
                    addEndGenerateBlocksForJordanExplanation(jForm);
                } else {
                    addBeforeAndAfterMatrices("A");
                }
        }
    }

    /**
     * Add generate blocks explanation
     * @param jForm JordanMatrixForm
     */
    private void addGenerateFactorsForJordanExplanation(JordanMatrixForm jForm) throws Exception {
        IMatrix matrix = jForm.getHandler().duplicate(jForm.getTransitionalMatrix());
        addToStepStatus(new JLabel("Trenutno stanje matrice:"));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
        addToStepStatus(new JLabel("U matrici [A] sada se generišu faktori za sve polinoma na dijagonali."));
    }

    /**
     * Add explanation after generating blocks
     *
     * @param jForm JordanMatrixForm
     */
    private void addGenerateBlocksForJordanExplanation(JordanMatrixForm jForm) throws Exception {
        IMatrix matrix = jForm.getHandler().duplicate(jForm.getTransitionalMatrix());
        addToStepStatus(new JLabel("Trenutno stanje matrice:"));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
        addToStepStatus(new JLabel("Od korena polinoma na dijagonali se generisu žordanovi blokovi."));
        addToStepStatus(new JLabel("Koreni:"));
        ArrayList<ArrayList<Object>> roots = jForm.getRoots();
        for (int i = 0; i < roots.size(); i++) {
            for (int j = 0; j < roots.get(i).size(); j++) {
                addToStepStatus(getLaTexLabel("x_" + (i + j) + "=" + getLatexFromMatrixElement(roots.get(i).get(j))));
            }
        }
    }

    /**
     * Add explanation after generating blocks
     *
     * @param jForm JordanMatrixForm
     */
    private void addEndGenerateBlocksForJordanExplanation(JordanMatrixForm jForm) throws Exception {
        IMatrix matrix = jForm.getHandler().duplicate(jForm.getTransitionalMatrix());
        addToStepStatus(new JLabel("Trenutno stanje matrice:"));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("A_I", matrix)));
        addToStepStatus(new JLabel("Žordanovi blokovi:"));
        ArrayList<IMatrix> jordansBlocks = jForm.getJordanBlocks();
        for (int i = 0; i < jordansBlocks.size(); i++) {
            addToStepStatus(getLaTexLabel(generateLatexMatrix("blok_" + i, jordansBlocks.get(i))));
        }
    }

    @Override
    public String getDescription() {
        String title;
        switch (getNumber()) {
            case START:
                title = "Početak transformacije matrice [A] u Žordanovu formu:";
                break;
            case INFO:
                if (getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title = "Elemente na dijagonali treba ispraviti.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_ELEMENTS_ON_DIAGONAL)) {
                    title = "Završetak ispravke elemenata na dijagonali.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                    title = "Oduzimanje matrice [A] od jedinične, dijagonalne, matrice pomnožene sa X.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_FIX_LEADING_COEFFICIENTS)) {
                    title = "Redukcija koeficijenata uz elemente sa najvećim stepenom na 1.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_LEADING_COEFFICIENTS)) {
                    title = "Kraj redukcije koeficijenata uz elemente sa najvećim stepenom na 1.";
                } else {
                    title = getEvent().getMessage() + ".";
                }
                break;
            case END:
                title = "Transformacija je završena.";
                break;
            default:
                //step
                if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_FACTORS)) {
                    title = "Generisanje faktora elementata na dijagonali.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_BLOCKS)) {
                    title = "Završetak generisanja faktora elementata na dijagonali.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_END_GENERATE_BLOCKS)) {
                    title = "Završetak generisanja žordanovih blokova.";
                } else {
                    title = getCommandDescription();
                }
        }

        return title;
    }

//    /**
//     * Return title with custom behaviour for Jordan form
//     *
//     * @return String
//     */
//    public String getTitle() {
//        switch (getNumber()) {
//            case START:
//                return "Početak";
//            case INFO:
//                if (getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_FACTORS)
//                        || getEvent().getMessage().equals(FormEvent.INFO_JORDAN_GENERATE_BLOCKS)
//                        || getEvent().getMessage().equals(FormEvent.INFO_JORDAN_END_GENERATE_BLOCKS)
//                        ) {
//                    return "Info korak";
//                }
//                return "Info";
//            case END:
//                return "Kraj";
//            default:
//                return "Korak " + getNumber();
//        }
//    }
}
