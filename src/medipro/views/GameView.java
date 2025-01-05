package medipro.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import medipro.controllers.GameController;
import medipro.models.GameModel;

public class GameView extends JPanel {

    private final GameModel model;
    private final GameController controller;

    public GameView(GameModel model, GameController controller) {
        this.model = model;
        this.controller = controller;

        BorderLayout layout = new BorderLayout();
        setLayout(layout);
    }

    public void setView(JPanel view) {
        add(view, BorderLayout.CENTER);
    }

}
