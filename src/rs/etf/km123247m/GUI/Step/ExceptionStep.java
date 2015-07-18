package rs.etf.km123247m.GUI.Step;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Observer.Event.FormEvent;

import javax.swing.*;

/**
 * Created by Miloš Krsmanović.
 * Jun 2015
 * <p/>
 * package: rs.etf.km123247m.GUI.Step
 */
public class ExceptionStep extends AbstractStep {

    public ExceptionStep(int number, ICommand command, FormEvent event, MatrixForm form) {
        super(number, command, event, form);
    }

    @Override
    protected void saveStepStatusPanel() throws Exception {
        if(getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_IS_SINGULAR)) {
            addMatrixIsSingularExplanation();
        } else if (getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_NOT_NUMERICAL)) {
            addMatrixNotNumericalExplanation();
        } else if (getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_CONTAINS_DECIMAL)) {
            addMatrixContainsDecimalExplanation();
        }
        IMatrix matrix = getForm().getHandler().duplicate(getForm().getHandler().getMatrix());
        addToStepStatus(getLaTexLabel(generateLatexMatrix("A", matrix)));
        matrices.add(new MatrixEntry("A", matrix));

    }

    /**
     * MatrixContainsDecimalExplanation
     */
    private void addMatrixContainsDecimalExplanation() {
        addToStepStatus(new JLabel("Dozvoljeni su celi brojevi i razlomci."));
    }

    /**
     * MatrixIsSingularExplanation
     */
    protected void addMatrixIsSingularExplanation() {
        addToStepStatus(new JLabel("Matrica je singularna ako joj je determinanta jednaka 0."));
    }

    /**
     * MatrixNotNumericalExplanation
     */
    protected void addMatrixNotNumericalExplanation() {
        addToStepStatus(new JLabel("Svi elementni matrice moraju biti numeričkog tipa."));
        addToStepStatus(new JLabel("Simboli nisu dozvoljeni."));
        addToStepStatus(new JLabel("Samo su celi brojevi dozvoljeni."));
    }

    @Override
    public String getDescription() {
        String description;
        if(getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_IS_SINGULAR)) {
            description = "Matrica je singularna!";
        } else if (getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_NOT_NUMERICAL)) {
            description = "Matrica mora biti numerička!";
        } else if (getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_CONTAINS_DECIMAL)) {
            description = "Matrica ne sme sadržati decimalne brojeve!";
        } else {
            description = "Neočekivani izuzetak se dogodio!";
        }

        return description;
    }

        public String getTitle() {
            String title;
            if(getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_IS_SINGULAR)
                    || getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_NOT_NUMERICAL)
                    || getEvent().getMessage().equals(FormEvent.EXCEPTION_MATRIX_CONTAINS_DECIMAL)) {
                title = "Ulazna matrica nije validna.";
            } else {
                title = "Izuzetak!";
            }

            return title;
        }
}
