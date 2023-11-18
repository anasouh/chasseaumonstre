package chasseaumonstre.views;

import java.io.IOException;
import java.net.URL;

import SubjectObserver.Observer;
import SubjectObserver.Subject;
import chasseaumonstre.controller.MHMonsterController;
import chasseaumonstre.controller.utils.UtilsController;
import javafx.fxml.FXMLLoader;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/*
 * Classe représentant la vue du Monstre
 * 
 * @param stage : la fenêtre principale
 * @param controller : le contrôleur
 * @see MHMonsterController
 * @autor Anas Ouhdda
 * @autor Atilla Tas
 * @autor Karim Aoulad-Tayab
 * @autor Selim Hamza
 * @autor Yliess El Atifi
 */
public class MHMonsterView implements Observer {
    private static final String MONSTER_CROSSHAIR_PATH = "https://cdn.discordapp.com/attachments/1159749679353974806/1169753687917346846/467900-200.png?ex=65568d04&is=65441804&hm=0d0bc1a4e1045921d9a369b4f808a348ea7473c35ba11ecf52174806f03f3f9f&";
    private Stage stage;
    private MHMonsterController controller;
    private GridPane maze;
    private Image otherImage = new Image("https://media.tenor.com/dPsOXgYjb30AAAAi/pixel-pixelart.gif");
    private ImagePattern patternInRectangle = new ImagePattern(otherImage);
    private Image mur = new Image("https://cdn.discordapp.com/attachments/1159749679353974806/1172539649072304219/image.png?ex=6560afa5&is=654e3aa5&hm=d18c0f8879f791c7bad3e76b97cd6ba430b24b9c24f0dfa9961c83bce50174b6&");
    private ImagePattern wall = new ImagePattern(mur);

    public MHMonsterView(Stage stage, MHMonsterController controller) {
        // Fenêtre
        this.stage = stage;
        this.controller = controller;
        this.maze = new GridPane();
        // Connecter la Vue au Controller
        this.controller.setVue(this);
    }

    /*
     * Afficher la vue du Monstre dans le stage
     */
    public void render() {
        try {
            FXMLLoader loader = new FXMLLoader(new URL("file", "", UtilsController.FXML_LOCATION + "gameView.fxml"));
            loader.setController(this.controller);
            Parent root = loader.load();

            this.draw();
            this.controller.getContentV().getChildren().add(maze);
            this.controller.showHistory();

            Scene scene = new Scene(root, 1300, 900);
            Image image = new Image(MONSTER_CROSSHAIR_PATH);
            scene.setCursor(new ImageCursor(image));

            stage.setTitle("Tour du Monstre");
            stage.setScene(scene);
            stage.show();

            controller.keyPressedOnScene(stage.getScene());
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void draw() {
        int width = this.controller.getModel().getWidth();
        int heigth = this.controller.getModel().getHeight();
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < heigth; y++) {
                StackPane stack = new StackPane();
                Rectangle cell = new Rectangle(1050/width, 850/heigth);
                cell.setOpacity(0);
                Text text = new Text();
                cell.setStroke(Color.BLACK);

                cell.setOnMouseClicked(e -> {
                    if (this.controller.hasMoved()) {
                        return;
                    }
                    int cellX = GridPane.getColumnIndex(stack);
                    int cellY = GridPane.getRowIndex(stack);
                    controller.handleMove(cellX, cellY);
                });

                if(this.controller.getModel().getMonster().estVisible(x,y)){
                    cell.setOpacity(1);
                    switch (this.controller.getModel().getMaze()[x][y]) {
                        case WALL:
                            cell.setFill(wall);
                            break;
                        case EMPTY:
                            if (this.controller.getModel().getMonster().isVisited(x, y)) {
                                cell.setFill(Color.web("#1bde243c"));
                                int turn = this.controller.getModel().getMonster().getVisitedTurn(x, y);
                                if (turn > 0)
                                    text.setText("" + turn);
                            } else
                                cell.setFill(Color.WHITE);
                            break;
                        case EXIT:
                            cell.setFill(Color.GREEN);
                            break;
                        case MONSTER:
                            System.out.println("MONSTER " + x + " " + y);
                            cell.setFill(Color.web("#1bde243c"));
                            if (!(cell.getFill().equals(patternInRectangle))) { 
                                cell.setFill(patternInRectangle
                                );
                            }
                            break;
                        default:
                            break;
                    }
                }
                

                stack.getChildren().addAll(cell, text);
                this.maze.add(stack, x, y);
            }
        }
    }

    /*
     * Mettre à jour la vue du Monstre
     */
    public void update() {
        this.maze.getChildren().clear();
        this.draw();
    }

    @Override
    public void update(Subject subj) {
        this.update();
    }

    @Override
    public void update(Subject subj, Object obj) {
        this.update(subj);
    }
}
