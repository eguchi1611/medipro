package medipro.how_to_play;

import java.awt.event.ActionEvent;

import medipro.App;

public class HowToPlayController {
    private final HowToPlayModel model;

    public HowToPlayController(HowToPlayModel model) {
        this.model = model;
    }

    public HowToPlayModel getModel() {
        return model;
    }

    public void handleClickBackButton(ActionEvent e) {
        model.previousPage();
    }

    public void handleClickNextButton(ActionEvent e) {
        model.nextPage();
    }

    public void handleClickExitButton(ActionEvent e) {
        App.getCardSubject().setCardNumber(App.TOP_VIEW);
    }

}
