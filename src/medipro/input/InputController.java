package medipro.input;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import javax.swing.JTextArea;

import medipro.App;
import medipro.IKeyAction;
import medipro.commands.Command;
import medipro.commands.CommandStore;
import medipro.worlds.World;

public class InputController {

    private final InputModel model;

    public InputController(InputModel inputModel) {
        this.model = inputModel;

        App.getStageModel().addPropertyChangeListener("world", this::handleChangeWorld);
    }

    private void handleChangeWorld(PropertyChangeEvent evt) {
        World world = (World) evt.getNewValue();
        model.setText(world.getExampleCommand());
    }

    public InputModel getModel() {
        return model;
    }

    public TextChangeListener getTextChangeListener(JTextArea textArea) {
        return new TextChangeListener(model, this, textArea);
    }

    public void handleSubmit(ActionEvent event) {
        App.getStageModel().getEntity().reset();
        start();
    }

    public void start() {
        IKeyAction keyAction = App.getStageModel();
        CommandStore commandStore = App.getCommandStore();
        Thread thread = new Thread(() -> {
            try {
                String[] lines = model.getText().split("\n");
                for (String line : lines) {
                    Command command = commandStore.getCommand(line);
                    if (command != null) {
                        command.execute(keyAction, line);
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

}
