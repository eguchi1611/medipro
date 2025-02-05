package net.keitaito.medipro.input;

import java.beans.PropertyChangeSupport;

import net.keitaito.medipro.App;
import net.keitaito.medipro.save.SaveData;
import net.keitaito.medipro.save.SaveManager;

public class InputModel {

    private String text;

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, java.beans.PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String old = this.text;
        this.text = text;
        pcs.firePropertyChange("text", old, text);
    }

    public void loadInputText() {
        String inputText = "";
        int stageLevel = App.getLevelModel().getSelectedLevel();
        SaveData saveData = SaveManager.load();
        System.out.println("stageLevel: " + stageLevel);
        if (stageLevel == 1) {
            inputText = saveData.getWorldSaveData1().getInput();
        } else if (stageLevel == 2) {
            inputText = saveData.getWorldSaveData2().getInput();
        } else if (stageLevel == 3) {
            inputText = saveData.getWorldSaveData3().getInput();
        } else if (stageLevel == 4) {
            inputText = saveData.getWorldSaveData4().getInput();
        } else if (stageLevel == 5) {
            inputText = saveData.getWorldSaveData5().getInput();
        } else if (stageLevel == 6) {
            inputText = saveData.getWorldSaveData6().getInput();
        } else if (stageLevel == 7) {
            inputText = saveData.getWorldSaveData7().getInput();
        } else if (stageLevel == 8) {
            inputText = saveData.getWorldSaveData8().getInput();
        }
        setText(inputText);
    }

}
