package rs.etf.km123247m.GUI.Form;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.Matrix.Forms.Implementation.JordanMatrixForm;
import rs.etf.km123247m.Matrix.Forms.Implementation.PolynomialRationalCanonicalMatrixForm;
import rs.etf.km123247m.Matrix.Forms.Implementation.SmithMatrixForm;
import rs.etf.km123247m.Matrix.Forms.MatrixForm;
import rs.etf.km123247m.Matrix.Handler.Implementation.SymJaMatrixHandler;
import rs.etf.km123247m.Matrix.Handler.MatrixHandler;
import rs.etf.km123247m.Matrix.IMatrix;
import rs.etf.km123247m.Observer.Event.FormEvent;
import rs.etf.km123247m.Observer.FormObserver;
import rs.etf.km123247m.Parser.MatrixParser.SymJa.IExprMatrixStringParser;
import rs.etf.km123247m.Parser.ParserTypes.StringParser;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;

/**
 * Created by Miloš Krsmanović.
 * Jan 2015
 * <p/>
 * package: rs.etf.km123247m.GUI.Form
 */
public class MatrixFormsJSwing extends JFrame implements FormObserver {

    public static final String SMITH_FORM = "Smith normal form";
    public static final String RATIONAL_FORM = "Rational canonical form";
    public static final String JORDANS_FORM = "Jordans canonical form";

    private JPanel rootPanel;
    private JTextArea textArea1;
    private JButton button1;
    private JComboBox comboBox1;
    private JTextArea textArea2;

    public MatrixFormsJSwing() {
        super("Matrix Forms JSwing");

        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setVisible(true);

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                StringParser parser = new IExprMatrixStringParser(true);
                parser.setInputString(textArea1.getText());
                try {
                    String selectedItem = comboBox1.getSelectedItem().toString();
                    if (selectedItem != null) {
                        IMatrix matrix = (IMatrix) parser.parseInput();
                        MatrixHandler handler = new SymJaMatrixHandler(matrix);
                        MatrixForm matrixForm = null;
                        if (selectedItem.equals(SMITH_FORM)) {
                            matrixForm = new SmithMatrixForm(handler);
                        } else if (selectedItem.equals(RATIONAL_FORM)) {
                            matrixForm = new PolynomialRationalCanonicalMatrixForm(handler);
                        } else if (selectedItem.equals(JORDANS_FORM)) {
                            matrixForm = new JordanMatrixForm(handler);
                        }
                        if (matrixForm != null) {
                            matrixForm.addObserver(MatrixFormsJSwing.this);
                            matrixForm.start();
                        }
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public void update(Observable o, Object arg) {
        FormEvent event = (FormEvent) arg;
        MatrixForm form = (MatrixForm) o;
//        AbstractStep step;
        switch (event.getType()) {
            case FormEvent.PROCESSING_START:
//                step = getStep(AbstractStep.START, null, event, form);
//                stepList.getItems().add(step.getTitle());
//                stepObjects.add(step);
                System.out.println("Start");
                textArea2.append("Start\n");
                break;
            case FormEvent.PROCESSING_STEP:
                ICommand stepCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
//                step = getStep(count++, stepCommand, event, form);
//                stepList.getItems().add(step.getTitle());
//                stepObjects.add(step);
                assert stepCommand != null;
                System.out.println(stepCommand.getDescription());
                textArea2.append(form.getHandler().getMatrix().toString() + "\n");
                break;
            case FormEvent.PROCESSING_INFO:
                ICommand infoCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
//                step = getStep(AbstractStep.INFO, infoCommand, event, form);
//                stepList.getItems().add(step.getTitle());
//                stepObjects.add(step);
                assert infoCommand != null;
                System.out.println("Info");
                textArea2.append(form.getHandler().getMatrix().toString() + "\n");
                break;
            case FormEvent.PROCESSING_END:
//                step = getStep(AbstractStep.END, null, event, form);
//                stepList.getItems().add(step.getTitle());
//                stepObjects.add(step);
//                stepList.getSelectionModel().select("Finish");
//                stepSelected();
//                statusLabel.setText("Done.");
                System.out.println("End");
                textArea2.append("End\n");
                break;
            case FormEvent.PROCESSING_EXCEPTION:
//                statusLabel.setText(event.getMessage());
                System.out.println(event.getMessage());
                break;
        }
    }
}
