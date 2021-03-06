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
import java.io.*;
import java.util.ArrayList;
import java.util.Observable;

/**
 * Created by Miloš Krsmanović.
 * Jan 2015
 * <p/>
 * package: rs.etf.km123247m.GUI.Form
 */
public class MatrixFormsJSwing extends JFrame implements FormObserver {

    public static final String SMITH_FORM = "Smitova normalna forma";
    public static final String RATIONAL_FORM = "Racionalna kanonska forma";
    public static final String JORDANS_FORM = "Žordanova kanonska forma";

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
        super("Smitova, racionalna i Žordanova forma");
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
                    System.out.println("--form opcija van granica dozvoljeno. Dozvoljeno od 0 do " + (comboFormSelect.getComponentCount()));
                }
            } else if (param.equals("--run")) {
                paramRun = true;
            } else {
                System.out.println("Parametar nije prepoznat (" + param + ")");
            }
        }
        if (paramRun) {
            if (paramForm && paramMatrix) {
                startTransformation();
            } else {
                if (!paramForm) {
                    System.out.println("--form opcija mora biti ispravno uneta da se moglo pokrenuti izračunavanje.");
                }
                if (!paramMatrix) {
                    System.out.println("--matrix opcija mora biti ispravno uneta da se moglo pokrenuti izračunavanje.");
                }
            }
        }
    }

    private void startTransformation() {
        count = 1;
        stepObjects.clear();
        currentlySelectedStep = -1;

        panelMatrixDisplay.removeAll();
        panelMatrixDisplay.setLayout(new BoxLayout(panelMatrixDisplay, BoxLayout.Y_AXIS));

        JPanel panel = new JPanel();
        panel.add(new JLabel("Računanje..."));
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panelMatrixDisplay.add(panel);

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
                step = createStep(AbstractStep.START, null, event, form);
                stepObjects.add(step);
                break;
            case FormEvent.PROCESSING_STEP:
                ICommand stepCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
                if (stepCommand != null) {
                    step = createStep(count++, stepCommand, event, form);
                    stepObjects.add(step);
                }
                break;
            case FormEvent.PROCESSING_INFO:
                ICommand infoCommand = form.getCommands().size() > 0 ? form.getCommands().getLast() : null;
                step = createStep(getInfoNumber(event), infoCommand, event, form);
                stepObjects.add(step);
                break;
            case FormEvent.PROCESSING_END:
                step = createStep(AbstractStep.END, null, event, form);
                stepObjects.add(step);
                addStepsToList();
                break;
            case FormEvent.PROCESSING_EXCEPTION:
                step = new ExceptionStep(AbstractStep.EXCEPTION, null, event, form);
                stepObjects.add(step);
                addStepsToList();
                break;
        }
    }

    protected void addStepsToList() {
        listSteps.setEnabled(true);
        DefaultListModel<String> listModel = new DefaultListModel<String>();
        for (AbstractStep aStep : stepObjects) {
            listModel.addElement(aStep.getTitle());
        }
        listSteps.setModel(listModel);
        listSteps.setSelectedIndex(stepObjects.size() - 1);
        stepSelected();
        listSteps.grabFocus();
    }

    /**
     * Get Info number
     *
     * @param event FormEvent
     *
     * @return int
     */
    protected int getInfoNumber(FormEvent event) {
        int number = AbstractStep.INFO;
        String message = event.getMessage();
        if (message.equals(FormEvent.INFO_JORDAN_GENERATE_FACTORS)
                || message.equals(FormEvent.INFO_JORDAN_GENERATE_BLOCKS)
                || message.equals(FormEvent.INFO_JORDAN_END_GENERATE_BLOCKS)
//                || message.equals(FormEvent.INFO_RATIONAL_PREPARE_T)
                || message.equals(FormEvent.INFO_RATIONAL_GENERATE_T)
                || message.equals(FormEvent.INFO_RATIONAL_GENERATE_PX)
                || message.equals(FormEvent.INFO_RATIONAL_END_GENERATE_PX)
                ) {
            number = count++;
            // this makes this Info into a Step
        }

        return number;
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

    private AbstractStep createStep(int type, ICommand command, FormEvent event, MatrixForm form) {
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
            handleParserExceptions((Exception) exception);
        } else if (exception instanceof FormEvent) {
            panelMatrixDisplay.add(new JLabel("Exception: " + ((FormEvent) exception).getMessage()));
        }
        panelMatrixDisplay.revalidate();
        panelMatrixDisplay.repaint();
    }

    /**
     * Handle exceptions coming from parser, these are not the same as exceptions coming from forms.
     * @param exception Exception
     */
    public void handleParserExceptions(Exception exception) {
        stepObjects.clear();
        addStepsToList();

        JLabel title = new JLabel("Ulazna matrica je neispravna.");
        title.setFont(new Font("Arial", Font.BOLD, 18));
        JPanel panel = new JPanel();
        panel.add(title);
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panelMatrixDisplay.add(panel);

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        if(exception.getMessage().equals("Matrix format error! m != n")) {
            panel.add(new JLabel("Broj vrsta mora da bude isti kao broj kolona."));
        } else if (exception.getMessage().contains("Matrix too small.")) {
            panel.add(new JLabel("Veličina matrice je ispod konfigurisane minimalne veličine."));
        } else if (exception.getMessage().contains("Matrix too big.")) {
            panel.add(new JLabel("Veličina matrice je iznad konfigurisane maksimalne veličine."));
        } else {
            panel.add(new JLabel("Exception: " + (exception).getMessage()));
        }

        panelMatrixDisplay.add(panel);
    }

    protected void addMenu() {
        //Create the menu bar.
        menuBar = new JMenuBar();

        // File
        menu = new JMenu("Fajl");
        menu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(menu);

        menuItem = new JMenuItem("Učitaj matricu iz fajla");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //Create a file chooser
                final JFileChooser fc = new JFileChooser();

                //In response to a button click:
                int returnVal = fc.showOpenDialog(MatrixFormsJSwing.this);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();

                    try {
                        textMatrixInlineInput.read(new FileReader(file), file.getName());
                    } catch (FileNotFoundException e1) {
                        showException(e1);
                    } catch (IOException e1) {
                        showException(e1);
                    }
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Sačuvaj matricu u fajl");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser();
                int returnVal = chooser.showSaveDialog(null);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    try {
                        String extension = getFileExtension(chooser.getSelectedFile()).equals("txt") ? "" : ".txt";
                        FileWriter fw = new FileWriter(chooser.getSelectedFile() + extension);
                        fw.write(textMatrixInlineInput.getText());
                        fw.close();
                    } catch (Exception ex) {
                        showException(ex);
                    }
                }
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("Izlaz");
        menuItem.setMnemonic(KeyEvent.VK_E);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(menuItem);

        // Edit
        menu = new JMenu("Primeri");
        menu.setMnemonic(KeyEvent.VK_X);

        // Examples
        // Smith
        JPanel labelPanel = new JPanel();
        labelPanel.add(new JLabel("Smitova normalna forma"));
        menu.addSeparator();
        menu.add(labelPanel);
        menu.addSeparator();

        menuItem = new JMenuItem("2x2");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.SMITH_TWOxTWO);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("3x3");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.SMITH_THREExTHREE);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("4x4");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.SMITH_FOURxFOUR);
            }
        });
        menu.add(menuItem);

        // Rational
        labelPanel = new JPanel();
        labelPanel.add(new JLabel("Racionalna kanonska forma"));
        menu.addSeparator();
        menu.add(labelPanel);
        menu.addSeparator();

        menuItem = new JMenuItem("2x2");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.RATIONAL_TWOxTWO);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("3x3");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.RATIONAL_THREExTHREE);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("4x4");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.RATIONAL_FOURxFOUR);
            }
        });
        menu.add(menuItem);

        // Jordans
        labelPanel = new JPanel();
        labelPanel.add(new JLabel("Žordanova kanonska forma"));
        menu.addSeparator();
        menu.add(labelPanel);
        menu.addSeparator();

        menuItem = new JMenuItem("2x2");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.JORDAN_TWOxTWO);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("3x3");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.JORDAN_THREExTHREE);
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("4x4");
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textMatrixInlineInput.setText(MatrixExamples.JORDAN_FOURxFOUR);
            }
        });
        menu.add(menuItem);

        menuBar.add(menu);

        // About
        menu = new JMenu("Pomoć");
        menu.setMnemonic(KeyEvent.VK_H);
        menuBar.add(menu);

        menuItem = new JMenuItem("Generiši MuPad komande za dostupne matrice");
        menuItem.setMnemonic(KeyEvent.VK_M);
        menuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMuPadDialog();
            }
        });
        menu.add(menuItem);

        menuItem = new JMenuItem("O programu");
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
     * @param file File
     * @return String
     */
    private String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
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
                    muPadDialog = new JDialog(MatrixFormsJSwing.this, true);
                    muPadDialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
                    muPadDialog.setMinimumSize(new Dimension(300, 200));
                }

                try {
                    muPadDialog.getContentPane().removeAll();
                    muPadDialog.add(new JLabel("MuPad komande za generisanje dostupnih matrica" +
                            " izabranog koraka (" + selectedStep.getTitle() + "):"), BorderLayout.NORTH);
                    muPadDialog.add(new JTextArea(selectedStep.getMuPadCommands()), BorderLayout.CENTER);
                    muPadDialog.pack();
                    muPadDialog.setLocation(
                            (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                            (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2
                    );
                    muPadDialog.setVisible(true);
                } catch (Exception e) {
                    showException(e);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * About dialog.
     */
    protected void showAboutDialog() {
        if (aboutDialog == null) {
            aboutDialog = new JDialog(MatrixFormsJSwing.this, true);
            aboutDialog.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
            aboutDialog.setResizable(false);
            aboutDialog.setTitle("O programu");
            aboutDialog.setSize(new Dimension(450, 150));

            Box b = Box.createVerticalBox();
            b.setBorder(new EmptyBorder(5, 5, 5, 5));
            b.add(Box.createGlue());
            b.add(new JLabel("Java aplikacija za Smitovu, racionalnu i Žordanovu formu"));
            b.add(new JLabel("Autor: Miloš Krsmanović"));
            b.add(new JLabel("Indeks: 2012/3247"));
            b.add(new JLabel("Za svrhu izrade master rada na Elektrotehničkom fakultetu u Beogradu."));
            b.add(Box.createGlue());
            aboutDialog.getContentPane().add(b, "Center");
            aboutDialog.setLocation(
                    (Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - getWidth() / 2,
                    (Toolkit.getDefaultToolkit().getScreenSize().height) / 2 - getHeight() / 2
            );
        }
        aboutDialog.setVisible(true);
    }
}