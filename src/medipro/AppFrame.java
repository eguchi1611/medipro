package medipro;

import javax.swing.JFrame;

public class AppFrame extends JFrame {

    public AppFrame() {
        super("medipro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        pack();
        setLocationRelativeTo(null);
    }

}
