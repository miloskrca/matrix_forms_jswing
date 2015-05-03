package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.RationalCanonicalMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.Handler.MatrixHandler;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Observer.Event.FormEvent;
import rs.etf.km123247m.Polynomial.Term;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

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
                if(getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_FINISH)) {
                    matrix = handler.duplicate(rForm.getFinalMatrix());
                    addToStepStatus(new JLabel("Matrica u racionalnoj kanonskoj formi:"));
                    addToStepStatus(getLaTexLabel(generateLatexMatrix("R", matrix)));
                    matrices.add(new MatrixEntry("R", matrix));

                    ArrayList<IMatrix> rationalBlocks = rForm.getRationalBlocks();
                    for (int i = 0; i < rationalBlocks.size(); i++) {
                        addToStepStatus(getLaTexLabel(generateLatexMatrix("blok_" + i, rationalBlocks.get(i))));
                        matrices.add(new MatrixEntry("blok_" + i, rationalBlocks.get(i)));
                    }
                } else {
                    matrix = handler.duplicate(rForm.getTransitionalMatrix(rForm.getRound()));
                    addToStepStatus(new JLabel("Trenutno stanje matrice:"));
                    String matrixName = rForm.getRound() == 0 ? "A_I" : "B_I";
                    addToStepStatus(getLaTexLabel(generateLatexMatrix(matrixName, matrix)));
                    matrices.add(new MatrixEntry(matrixName, matrix));
                    if (getEvent().getMessage().equals(FormEvent.INFO_FIX_ELEMENTS_ON_DIAGONAL)) {
                        addFixingDiagonalExplanation();
                    } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_LEADING_COEFFICIENTS)) {
                        if(rForm.getRound() == 0) {
                            addToStepStatus(new JLabel("Od ove matrice generišemo matricu transformacije [R]"));
                            addToStepStatus(new JLabel("Generišemo blokove od polinoma na dijagonali " +
                                    "rezultujuće matrice [R]"));
                        }
                    } else if (getEvent().getMessage().equals(FormEvent.INFO_SUBTRACT_FOR_SMITH)) {
                        addSubtractForSmithExplanation(rForm.getStartMatrix());
                    } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_PREPARE_T)) {
                        addSubtractForTExplanation(rForm.getFinalMatrix());
                    }
                }
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
                if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_GENERATE_T)) {
                    addGenerateTExplanation(rForm);
                } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_GENERATE_PX)) {
                    addGeneratePxExplanation(rForm);
                } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_END_GENERATE_PX)) {
                    addEndGeneratePxExplanation(rForm);
                } else {
                    addBeforeAndAfterMatrices(rForm.getRound() == 0 ? "A" : "B");

                    matrix = handler.duplicate(rForm.getP(rForm.getRound()));
                    addToStepStatus(new JLabel("Trenutno stanje matrice [P]:"));
                    addToStepStatus(getLaTexLabel(generateLatexMatrix("P_" + rForm.getRound(), matrix)));
                    matrices.add(new MatrixEntry("P_" + rForm.getRound(), matrix));
                    matrix = handler.duplicate(rForm.getQ(rForm.getRound()));
                    addToStepStatus(new JLabel("Trenutno stanje matrice [Q]:"));
                    addToStepStatus(getLaTexLabel(generateLatexMatrix("Q_" + rForm.getRound(), matrix)));
                    matrices.add(new MatrixEntry("Q_" + rForm.getRound(), matrix));
                    addToStepStatus(new JLabel("P: Oslikava operacija nad redovima."));
                    addToStepStatus(new JLabel("Q: Oslikava operacija nad kolonama."));
                }
        }
    }

    /**
     * Add explanation how are we preparing the matrix for smith transformation to display panel.
     *
     * @param matrix Matrix that is subtracted from xI
     */
    protected void addSubtractForTExplanation(IMatrix matrix) throws Exception {
        addToStepStatus(new JLabel("Od dijagonalne matrice treba oduzeti matricu [R]"));
        addToStepStatus(new JLabel("pre nego što se transformiše u Smitovu formu:"));
        addToStepStatus(getLaTexLabel("B_I = x*I-R"));
        addToStepStatus(new JComponent[]{
                getLaTexLabel(generateLatexMatrix("I",
                                getForm().getHandler().diagonal(
                                        matrix.getRowNumber(),
                                        getForm().getHandler().getObjectFromString(String.valueOf(Term.X))
                                )
                        )
                ),
                getLaTexLabel(generateLatexMatrix("R", matrix))
        });
    }

    /**
     * @param rForm RationalCanonicalMatrixForm
     * @throws Exception
     */
    protected void addGenerateTExplanation(RationalCanonicalMatrixForm rForm) throws Exception {
        addToStepStatus(getLaTexLabel("P(x) = P_0^{-1}*P_1"));
        addToStepStatus(getLaTexLabel("T = A^nP(x)_n + ... + A^3P(x)_3 + A^2P(x)_2 + A^1P(x)_1 + P(x)_0"));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("P_0", rForm.getP(0))));
        matrices.add(new MatrixEntry("P_0", rForm.getP(0)));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("P_1", rForm.getP(1))));
        matrices.add(new MatrixEntry("P_1", rForm.getP(1)));
    }

    /**
     * @param rForm RationalCanonicalMatrixForm
     * @throws Exception
     */
    protected void addGeneratePxExplanation(RationalCanonicalMatrixForm rForm) throws Exception {
        MatrixHandler handler = rForm.getHandler();
        addToStepStatus(getLaTexLabel("P(x) = P_0^{-1}*P_1"));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("P_0", rForm.getP(0))));
        matrices.add(new MatrixEntry("P_0", rForm.getP(0)));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("P_0^{-1}", handler.invertMatrix(rForm.getP(0)))));
        matrices.add(new MatrixEntry("P_0^(-1)", handler.invertMatrix(rForm.getP(0))));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("P_1", rForm.getP(1))));
        matrices.add(new MatrixEntry("P_1", rForm.getP(1)));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("P(x)", getEvent().getMatrix())));
        matrices.add(new MatrixEntry("Px", getEvent().getMatrix()));
    }

    /**
     * @param form RationalCanonicalMatrixForm
     * @throws Exception
     */
    protected void addEndGeneratePxExplanation(RationalCanonicalMatrixForm form) throws Exception {
        addToStepStatus(getLaTexLabel(generateLatexMatrix("P(x)", getEvent().getMatrix())));
        matrices.add(new MatrixEntry("Px", getEvent().getMatrix()));
        String tString = "";
        HashMap<Integer, IMatrix> pMatrices = form.getpMatrices();
        for (Integer degree : pMatrices.keySet()) {
            addToStepStatus(getLaTexLabel(generateLatexMatrix("P(x)_" + degree, pMatrices.get(degree))));
            matrices.add(new MatrixEntry("Px_" + degree, pMatrices.get(degree)));
            tString = (" + " + (degree > 0 ? "A^" + degree : "") + "P(x)_" + degree) + tString;
        }
        tString = tString.substring(3);
        addToStepStatus(getLaTexLabel("T = " + tString));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("A", form.getStartMatrix())));
        matrices.add(new MatrixEntry("A", form.getStartMatrix()));
        addToStepStatus(getLaTexLabel(generateLatexMatrix("T", form.getT())));
        matrices.add(new MatrixEntry("T", form.getT()));
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
                } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_FINISH)) {
                    title += "Kraj generisanja rezultujuće matrice [R].";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_FIX_LEADING_COEFFICIENTS)) {
                    title += "Redukcija koeficijenata uz elemente sa najvećim stepenom na 1.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_END_FIX_LEADING_COEFFICIENTS)) {
                    title += "Kraj redukcije koeficijenata uz elemente sa najvećim stepenom na 1.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_PREPARE_T)) {
                    title += "Priprema za generisanje matrice [T].";
                } else {
                    title += getEvent().getMessage() + ".";
                }
                break;
            case END:
                title += "Transformacija je završena.";
                break;
            default:
                //step
                if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_GENERATE_T)) {
                    title += "Početak generisanje matrice [T].";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_GENERATE_PX)) {
                    title += "Generisanje P(x) matrice.";
                } else if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_END_GENERATE_PX)) {
                    title += "Generisanje svih P(x) pod matrica.";
                } else {
                    title += getCommandDescription();
                }
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
                return "Početak";
            case INFO:
                if (getEvent().getMessage().equals(FormEvent.INFO_RATIONAL_FINISH)) {
                    return "Kraj [Racionalna]";
                }
                return "Info";
            case END:
                return "Kraj";
            default:
                return "Korak " + getNumber();
        }
    }
}
