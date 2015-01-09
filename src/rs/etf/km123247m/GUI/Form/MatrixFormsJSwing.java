package rs.etf.km123247m.GUI.Form;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Miloš Krsmanović.
 * Jan 2015
 * <p/>
 * package: rs.etf.km123247m.GUI.Form
 */
public class MatrixFormsJSwing extends JFrame {
    private JButton button;
    private JPanel rootPanel;

    public MatrixFormsJSwing() {
        super("Matrix Forms JSwing");

        setContentPane(rootPanel);
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showConfirmDialog(MatrixFormsJSwing.this, "Hi");
            }
        });

        setVisible(true);
    }
}
