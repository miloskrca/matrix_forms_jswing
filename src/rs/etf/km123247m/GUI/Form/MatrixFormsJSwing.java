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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
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
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;

    private JPanel rootPanel;
    private JTextArea textMatrixInlineInput;
    private JButton btnStart;
    private JComboBox comboFormSelect;
    private JSlider sliderSteps;
    private JButton btnStepNext;
    private JButton btnStepPrev;
    private JTextPane textStepDescription;
    private JPanel panelMatrixDisplay;

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
        sliderSteps.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                stepSelected();
                System.out.println(sliderSteps.getValue());
            }
        });
        btnStepNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sliderSteps.getValue() < sliderSteps.getMaximum()) {
                    sliderSteps.setValue(sliderSteps.getValue() + 1);
                }
            }
        });
        btnStepPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sliderSteps.getValue() > sliderSteps.getMinimum()) {
                    sliderSteps.setValue(sliderSteps.getValue() - 1);
                }
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
                sliderSteps.setMaximum(stepObjects.size() - 1);
                sliderSteps.setValue(sliderSteps.getMaximum());
                System.out.println("End");
                break;
            case FormEvent.PROCESSING_EXCEPTION:
                System.out.println(event.getMessage());
                break;
        }
    }

    private void stepSelected() {
        if (stepObjects.size() > 0) {
            int selected = sliderSteps.getValue();
            if(currentlySelectedStep == selected) {
                return;
            }
            currentlySelectedStep = selected;
            AbstractStep selectedStep;

            panelMatrixDisplay.removeAll();
            panelMatrixDisplay.setLayout(new BorderLayout());
            panelMatrixDisplay.revalidate();

            if (selected == -1) {
                JPanel panel = getPanel("\\text{No steps selected.}");
                panelMatrixDisplay.add(panel);
            } else {
                try {
                    selectedStep = stepObjects.get(selected);
                    ArrayList<LaTexPanel> panels = selectedStep.getPanels();
                    for (LaTexPanel panel : panels) {
                        panelMatrixDisplay.add(panel, BorderLayout.NORTH);
                    }
                    textStepDescription.setText(selectedStep.getTitle() + "\n" + selectedStep.getDescription());
                } catch (Exception e) {
                    e.printStackTrace();
                    for (StackTraceElement s : e.getStackTrace()) {
                        panelMatrixDisplay.add(new JLabel(s.toString()), BorderLayout.NORTH);
                    }
                }
            }
        }

        panelMatrixDisplay.repaint();
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


    protected LaTexPanel getPanel(String formula) {
        LaTexPanel panel = new LaTexPanel();

        panel.setFormula(formula);
        panel.render();

        return panel;
    }

    protected void addMenu() {
        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("A Menu");
        menu.setMnemonic(KeyEvent.VK_A);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        //a group of JMenuItems
        menuItem = new JMenuItem("A text-only menu item",
                KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, InputEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        menu.add(menuItem);

        menuItem = new JMenuItem("Both text and icon",
                new ImageIcon("images/middle.gif"));
        menuItem.setMnemonic(KeyEvent.VK_B);
        menu.add(menuItem);

        menuItem = new JMenuItem(new ImageIcon("images/middle.gif"));
        menuItem.setMnemonic(KeyEvent.VK_D);
        menu.add(menuItem);

        //a group of radio button menu items
        menu.addSeparator();
        ButtonGroup group = new ButtonGroup();
        rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        rbMenuItem.setSelected(true);
        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Another one");
        rbMenuItem.setMnemonic(KeyEvent.VK_O);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        //a group of check box menu items
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
        cbMenuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(cbMenuItem);

        cbMenuItem = new JCheckBoxMenuItem("Another one");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        menu.add(cbMenuItem);

        //a submenu
        menu.addSeparator();
        submenu = new JMenu("A submenu");
        submenu.setMnemonic(KeyEvent.VK_S);

        menuItem = new JMenuItem("An item in the submenu");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        submenu.add(menuItem);

        menuItem = new JMenuItem("Another item");
        submenu.add(menuItem);
        menu.add(submenu);

        //Build second menu in the menu bar.
        menu = new JMenu("Another Menu");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

        this.setJMenuBar(menuBar);
    }
}
