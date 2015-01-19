package rs.etf.km123247m.GUI.Form;

import rs.etf.km123247m.Command.ICommand;
import rs.etf.km123247m.GUI.Step.*;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
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

    ArrayList<AbstractStep> stepObjects = new ArrayList<AbstractStep>();
    private int count = 0;
    private int currentlySelectedStep = 0;

    //menu
    JMenuBar menuBar;
    JMenu menu;
    JMenuItem menuItem;

    private JPanel rootPanel;
    private JTextArea textMatrixInlineInput;
    private JButton btnStart;
    private JComboBox comboFormSelect;
    private JPanel panelMatrixDisplay;
    private JList<String> listSteps;

    public MatrixFormsJSwing() {
        super("Matrix Forms JSwing");
        addMenu();

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                count = 1;
                stepObjects.clear();
                StringParser parser = new IExprMatrixStringParser(true);
                parser.setInputString(textMatrixInlineInput.getText());
                try {
                    String selectedItem = comboFormSelect.getSelectedItem().toString();
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
        listSteps.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                stepSelected();
            }
        });

        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        FormEvent event = (FormEvent) arg;
        MatrixForm form = (MatrixForm) o;
        AbstractStep step;
        switch (event.getType()) {
            case FormEvent.PROCESSING_START:
                step = getStep(AbstractStep.START, null, event, form);
                stepObjects.add(step);
                System.out.println("Start");
                break;
            case FormEvent.PROCESSING_STEP:
                ICommand stepCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
                step = getStep(count++, stepCommand, event, form);
                stepObjects.add(step);
                assert stepCommand != null;
                System.out.println(stepCommand.getDescription());
                System.out.println(form.getHandler().getMatrix().toString() + "\n");
                break;
            case FormEvent.PROCESSING_INFO:
                ICommand infoCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
                step = getStep(AbstractStep.INFO, infoCommand, event, form);
                stepObjects.add(step);
                assert infoCommand != null;
                System.out.println("Info");
                System.out.println(form.getHandler().getMatrix().toString() + "\n");
                break;
            case FormEvent.PROCESSING_END:
                step = getStep(AbstractStep.END, null, event, form);
                stepObjects.add(step);
                listSteps.setSelectedIndex(stepObjects.size() - 1);
                listSteps.setEnabled(true);
                System.out.println("End");
                DefaultListModel<String> listModel = new DefaultListModel<String>();
                for(AbstractStep aStep : stepObjects) {
                    listModel.addElement(aStep.getTitle());
                }
                listSteps.setModel(listModel);
                break;
            case FormEvent.PROCESSING_EXCEPTION:
                System.out.println(event.getMessage());
                stepObjects.clear();
                panelMatrixDisplay.removeAll();
                panelMatrixDisplay.setLayout(new BoxLayout(panelMatrixDisplay, BoxLayout.Y_AXIS));
                panelMatrixDisplay.add(new JLabel("Exception: " + event.getMessage()));
                panelMatrixDisplay.repaint();
                listSteps.setEnabled(false);
                break;
        }
    }

    private void stepSelected() {
        if (stepObjects.size() > 0) {
            int selected = listSteps.getSelectedIndex();
            if(currentlySelectedStep == selected || currentlySelectedStep == -1) {
                return;
            }
            currentlySelectedStep = selected;
            AbstractStep selectedStep;

            panelMatrixDisplay.removeAll();
            panelMatrixDisplay.setLayout(new BoxLayout(panelMatrixDisplay, BoxLayout.Y_AXIS));

            try {
                selectedStep = stepObjects.get(selected);
                panelMatrixDisplay.add(selectedStep.getStepStatusPanel());
            } catch (Exception e) {
                e.printStackTrace();
                for (StackTraceElement s : e.getStackTrace()) {
                    panelMatrixDisplay.add(new JLabel(s.toString()), BorderLayout.NORTH);
                }
            }

            panelMatrixDisplay.revalidate();
            panelMatrixDisplay.repaint();
        }
    }

    private AbstractStep getStep(int type, ICommand command, FormEvent event, MatrixForm form) {
        String selectedItem = (String) comboFormSelect.getSelectedItem();
        if (selectedItem.equals(SMITH_FORM)) {
            return new SmithStep(type, command, event, form);
        } else if (selectedItem.equals(RATIONAL_FORM)) {
            return new RationalCanonicalStep(type, command, event, form);
        } else if (selectedItem.equals(JORDANS_FORM)) {
            return new JordanStep(type, command, event, form);
        }

        return null;
    }

    protected void addMenu() {
        //Create the menu bar.
        menuBar = new JMenuBar();

        // File
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        menuItem = new JMenuItem("Exit");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItem);

        // Edit
        menu = new JMenu("Examples");
        menu.setMnemonic(KeyEvent.VK_X);

        // Examples
        menuItem = new JMenuItem("2x2");
        menuItem.setMnemonic(KeyEvent.VK_2);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.TWOxTWO);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("3x3");
        menuItem.setMnemonic(KeyEvent.VK_3);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.THREExTHREE);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("4x4");
        menuItem.setMnemonic(KeyEvent.VK_4);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.FOURxFOUR);
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        // About
        menu = new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menu);

        menuItem = new JMenuItem("About");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MatrixFormsJSwing.this, "Eggs are not supposed to be green.");
            }
        });
        menu.add(menuItem);

        this.setJMenuBar(menuBar);
    }
}