package rs.etf.km123247m.GUI.Step;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * Created by Miloš Krsmanović.
 * Sep 2014
 * <p/>
 * package: rs.etf.km123247m.Model
 */
public class LaTexPanel extends JPanel {
    private TeXIcon icon;

    public LaTexPanel() {

    }

    public void setFormula(String formula) {
        TeXFormula teXFormula = new TeXFormula(formula);
        this.icon = teXFormula.createTeXIcon(
                TeXConstants.STYLE_DISPLAY, 20);
    }

    public void render() {
        draw();
    }

    private void draw() {
        if (this.icon == null) {
            return;
        }

        BufferedImage b = new BufferedImage(this.icon.getIconWidth(), this.icon
                .getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        this.icon.paintIcon(new JLabel(), b.getGraphics(), 0, 0);
        this.add(new JLabel(new ImageIcon(b)));
    }
}
