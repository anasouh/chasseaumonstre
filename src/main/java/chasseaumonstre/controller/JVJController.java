package chasseaumonstre.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import chasseaumonstre.model.MonsterHunterModel;
import chasseaumonstre.views.JVJView;
import chasseaumonstre.views.MHHunterView;

public class JVJController {
    
    @FXML
    private Button startGameButton;
    
    @FXML
    private TextField j1; 
    @FXML
    private TextField j2; 

    private Stage stage;
    private MonsterHunterModel model;
    private MHHunterView hunterView;

    public JVJController(Stage stage, MonsterHunterModel model) {
        this.stage = stage;
        this.model = model;
    }

    @FXML
    private void startGame() {
    
        model.setHunterName(j1.getText());
        model.setMonsterName(j2.getText());
        this.hunterView = new MHHunterView(stage, new MHHunterController(stage, model));
        this.hunterView.render();

    }
}
