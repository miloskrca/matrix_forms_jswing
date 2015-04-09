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
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
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
    private int currentlySelectedStep = -1;

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
    private JDialog muPadDialog;
    private JDialog aboutDialog;

    public MatrixFormsJSwing(String[] args) {
        super("Smiths, rational i Jordans form");
        addMenu();

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                startTransformation();
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
        checkForParametersAndHandle(args);
    }

    private void checkForParametersAndHandle(String[] parameters) {
        boolean paramMatrix = false;
        boolean paramForm = false;
        boolean paramRun = false;
        for (String param : parameters) {
            if (param.contains("--matrix")) {
                paramMatrix = true;
                textMatrixInlineInput.setText(param.split("--matrix=")[1]);
            } else if (param.contains("--form")) {
                int option = Integer.parseInt(param.split("--form=")[1]);
                if (option >= 0 && option <= comboFormSelect.getComponentCount()) {
                    paramForm = true;
                    comboFormSelect.setSelectedIndex(option);
                } else {
                    System.out.println("--form option out of bounds. Available from 0 to " + (comboFormSelect.getComponentCount()));
                }
            } else if (param.equals("--run")) {
                paramRun = true;
            } else {
                System.out.println("Parameter not recognized (" + param + ")");
            }
        }
        if(paramRun) {
            if(paramForm && paramMatrix) {
                startTransformation();
            } else {
                if (!paramForm) {
                    System.out.println("--form option needs to be set properly to be able to run.");
                }
                if (!paramMatrix) {
                    System.out.println("--matrix option needs to be set properly to be able to run.");
                }
            }
        }
    }

    private void startTransformation() {
        count = 1;
        stepObjects.clear();
        currentlySelectedStep = -1;

        panelMatrixDisplay.removeAll();
        panelMatrixDisplay.add(new JLabel("Running..."));
        panelMatrixDisplay.revalidate();
        panelMatrixDisplay.repaint();

        StringParser parser = new IExprMatrixStringParser(true);
        String inputText = textMatrixInlineInput.getText();
        if (!inputText.equals("")) {
            parser.setInputString(inputText);
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
            } catch (Exception e) {
                e.printStackTrace();
                showException(e);
            }
        }
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
                if (stepCommand != null) {
                    step = getStep(count++, stepCommand, event, form);
                    stepObjects.add(step);
                    System.out.println(stepCommand.getDescription());
                    System.out.println(form.getHandler().getMatrix().toString() + "\n");
                }
                break;
            case FormEvent.PROCESSING_INFO:
                ICommand infoCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
                step = getStep(AbstractStep.INFO, infoCommand, event, form);
                stepObjects.add(step);
                System.out.println("Info");
                System.out.println(form.getHandler().getMatrix().toString() + "\n");
                break;
            case FormEvent.PROCESSING_END:
                step = getStep(AbstractStep.END, null, event, form);
                stepObjects.add(step);
                listSteps.setEnabled(true);
                System.out.println("End");
                DefaultListModel<String> listModel = new DefaultListModel<String>();
                for (AbstractStep aStep : stepObjects) {
                    listModel.addElement(aStep.getTitle());
                }
                listSteps.setModel(listModel);
                listSteps.setSelectedIndex(stepObjects.size() - 1);
                stepSelected();
                listSteps.grabFocus();
                break;
            case FormEvent.PROCESSING_EXCEPTION:
                System.out.println(event.getMessage());
                stepObjects.clear();
                showException(event);
                listSteps.setEnabled(false);
                break;
        }
    }

    private void stepSelected() {
        if (stepObjects.size() > 0) {
            int selected = listSteps.getSelectedIndex();
            if (currentlySelectedStep != selected && selected != -1) {
                currentlySelectedStep = selected;
                AbstractStep selectedStep;

                panelMatrixDisplay.removeAll();
                panelMatrixDisplay.setLayout(new BoxLayout(panelMatrixDisplay, BoxLayout.Y_AXIS));

                try {
                    selectedStep = stepObjects.get(selected);
                    panelMatrixDisplay.add(selectedStep.getStepStatusPanel());
                } catch (Exception e) {
                    e.printStackTrace();
                    showException(e);
                }

                panelMatrixDisplay.revalidate();
                panelMatrixDisplay.repaint();
            }
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

    /**
     * Displays exception message
     *
     * @param exception Occurred exception
     */
    private void showException(Object exception) {
        panelMatrixDisplay.removeAll();
        if (exception instanceof Exception) {
            panelMatrixDisplay.add(new JLabel("Exception: " + ((Exception) exception).getMessage()));
        } else if (exception instanceof FormEvent) {
            panelMatrixDisplay.add(new JLabel("Exception: " + ((FormEvent) exception).getMessage()));
        }
        panelMatrixDisplay.revalidate();
        panelMatrixDisplay.repaint();
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

        menuItem = new JMenuItem("Get MuPad command for visible matrices");
        menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMuPadDialog();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("About");
        menuItem.setMnemonic(KeyEvent.VK_A);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        menu.add(menuItem);

        this.setJMenuBar(menuBar);
    }

    /**
     * MuPad commands dialog showing currently commands for generating
     * currently visible matrices.
     */
    protected void showMuPadDialog() {
        if (stepObjects.size() > 0) {
            int selected = listSteps.getSelectedIndex();
            if (selected != -1) {
                currentlySelectedStep = selected;
                AbstractStep selectedStep = stepObjects.get(selected);

                if (muPadDialog == null) {
                    muPadDialog = new JDialog(MatrixFormsJSwing.this);
                    muPadDialog.setMinimumSize(new Dimension(300, 200));
                }

                try {
                    muPadDialog.getContentPane().removeAll();
                    muPadDialog.add(new JLabel("MuPad commands for generating visible matrices" +
                            " of the selected step (" + selectedStep.getTitle() + "):"), BorderLayout.NORTH);
                    muPadDialog.add(new JTextArea(selectedStep.getMuPadCommands()), BorderLayout.CENTER);
                    muPadDialog.pack();
                    muPadDialog.setVisible(true);
                } catch (Exception e) {
                    showException(e);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * About dialog. TODO: make it better! Use labels! Maybe add logo!
     */
    protected void showAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new JDialog(MatrixFormsJSwing.this);
            aboutDialog.setTitle("About");
            aboutDialog.setMinimumSize(new Dimension(400, 200));

            JPanel panel = new JPanel();
            panel.setBorder(new EmptyBorder(5, 5, 5, 5));

            String textTitle = "Java application for Smiths, rational i Jordans form";
            JLabel labelTitle = new JLabel(textTitle);
            labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
            labelTitle.setVerticalAlignment(SwingConstants.TOP);
            panel.add(labelTitle);

            String textAuthor = "Author: Miloš Krsmanović";
            JLabel labelAuthor = new JLabel(textAuthor);
            labelAuthor.setHorizontalAlignment(SwingConstants.CENTER);
            labelTitle.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(labelAuthor);

            String textIndex = "Index: 2012/3247";
            JLabel labelIndex = new JLabel(textIndex);
            labelIndex.setHorizontalAlignment(SwingConstants.CENTER);
            labelTitle.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(labelIndex);

            String textInfo = "Za svrhu izrade master rada na Elektrotehničkom fakultetu u Beogradu.";
            JLabel labelInfo = new JLabel(textInfo);
            labelIndex.setHorizontalAlignment(SwingConstants.CENTER);
            labelTitle.setVerticalAlignment(SwingConstants.CENTER);
            panel.add(labelInfo);

            aboutDialog.add(panel);
        }
        aboutDialog.setVisible(true);
    }
}