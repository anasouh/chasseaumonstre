package fr.univlille.info.J3.chasseaumonstre.model;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import SubjectObserver.Observer;
import SubjectObserver.Subject;
import fr.univlille.info.J3.chasseaumonstre.App;
import fr.univlille.info.J3.chasseaumonstre.model.strategy.hunter.Hunter;
import fr.univlille.info.J3.chasseaumonstre.model.strategy.monster.Monster;

/*
 * MonsterHunterModel représente le modèle, qui se charge du déroulement du jeu
 * 
 * @author Anas Ouhdda
 * @author Atilla Tas
 * @author Karim Aoulad-Tayab
 * @author Selim Hamza
 * @author Yliess El Atifi
 */
public class MonsterHunterModel extends Subject implements Serializable, Observer {
    public static final int DEFAULT_WIDTH = 21;
    public static final int DEFAULT_HEIGHT = 17;
    private boolean[][] maze;
    private Integer turn;
    private String monsterName, hunterName;
    private Monster monster;
    private Hunter hunter;
    private Coordinate entrance, exit;

    /*
     * Constructeur de MonsterHunterModel
     */
    public MonsterHunterModel() {
        this.turn = 1;
        this.monster = new Monster();
        this.hunter = new Hunter();
    }

    public void initialize() {
        this.initializeMaze();
        this.initializePlayers();
        this.monster.attach(this);
        this.hunter.attach(this);
    }

    public String getMonsterName() {
        return monsterName;
    }

    public void setMonsterName(String monsterName) {
        this.monsterName = monsterName;
    }

    public String getHunterName() {
        return hunterName;
    }

    public void setHunterName(String hunterName) {
        this.hunterName = hunterName;
    }

    public int getWidth() {
        return App.PREFERENCES.getInt("mazeWidth", DEFAULT_WIDTH);
    }

    public int getHeight() {
        return App.PREFERENCES.getInt("mazeHeight", DEFAULT_HEIGHT);
    }

    public int getObstacles() {
        return App.PREFERENCES.getInt("obstacles", 1);
    }
    
    public void setWidth(int width) throws IllegalArgumentException {
        if (width >= 7 && width % 2 > 0) {
            App.PREFERENCES.putInt("mazeWidth", width);
        } else {
            throw new IllegalArgumentException("La largeur doit être impaire et supérieure ou égale à 7");
        }
    }

    public void setHeight(int height) throws IllegalArgumentException {
        if (height >= 5) {
            App.PREFERENCES.putInt("mazeHeight", height);
        } else {
            throw new IllegalArgumentException("La hauteur doit être supérieure ou égale à 5");
        }
    }

    public void setObstacles(int obstacles) throws IllegalArgumentException {
        if (obstacles >= 1 && obstacles <= 80) {
            App.PREFERENCES.putInt("obstacles", obstacles);
        } else {
            throw new IllegalArgumentException("Le pourcentage doit être compris entre 0 et 80 inclus.");
        }
    }

    public Monster getMonster() {
        return monster;
    }

    public Hunter getHunter() {
        return hunter;
    }

    public void initializePlayers() {
        this.monster.initialize(this.maze);
        monster.setExit(exit.getRow(), exit.getCol());
        monster.setEntry(entrance.getRow(), entrance.getCol());
        monster.setCoord(entrance.getRow(), entrance.getCol(), 0);
        this.hunter.initialize(new boolean[getWidth()][getHeight()]);
    }

    /*
     * Génère un labyrinthe aléatoirement validé par MazeValidator
     * 
     * @see MazeValidator
     */
    public void initializeMaze() {
        if(this.maze == null) {
            MazeGenerator mazeGenerator = new MazeGenerator(getWidth(), getHeight());
            mazeGenerator.generatePlateau(getObstacles());

            MazeValidator mazeValidator = new MazeValidator(mazeGenerator);

            while (!mazeValidator.isValid()) {
                mazeGenerator.toString();
                mazeGenerator.generate();
                mazeValidator.setMaze(mazeGenerator.getMaze());
            }
            entrance = mazeGenerator.getEntranceCoordinate();
            exit = mazeGenerator.getExitCoordinate();
            this.maze = mazeGenerator.toBoolean();
        }
    }

    public boolean[][] getMaze() {
        return this.maze;
    }

    public Integer getTurn() {
        return this.turn;
    }

    public Coordinate getEntrance() {
        return entrance;
    }

    public Coordinate getExit() {
        return exit;
    }

    public void nextTurn() {
        this.turn++;
    }

    /*
     * Importe un labyrinthe depuis un fichier
     */
    public void importMaze(File file) throws NumberFormatException, IOException {
        Path p = Paths.get(file.toString());
        List<String> lines = Files.readAllLines(p);
        this.setHeight(lines.size());
        this.setWidth(lines.get(0).split(",").length);
        boolean[][] labyrinth = new boolean[getHeight()][getWidth()];
        int entranceX = 0;
        int entranceY = 0;
        String[] line;
        for(int i = 0; i < this.getHeight(); i++) {
            line = lines.get(i).split(",");
            for(int j = 0; j < this.getWidth(); j++) {
                int value = Integer.parseInt(line[j]);
                labyrinth[i][j] = MazeGenerator.toBoolean(value);
                if(i == 0 && value == 4) {
                    entranceX = j;
                    entranceY = i;
                }

            }
        }
        monster.setCoord(entranceX, entranceY, 0);
        this.maze = labyrinth;
    }

    @Override
    public void update(Subject subj) {
        
    }
    
    /*
     * Notification reçue par Monster ou Hunter, qui notifie les vues
     * avec les coordonnées du joueur qui a joué, ou "WIN" si le joueur a gagné
     * 
     * @param subj le sujet qui a notifié
     * @param data les données envoyées par le sujet
     * @see Coordinate
     * @see Monster
     * @see Hunter
     */
    @Override
    public void update(Subject subj, Object data) {
        Coordinate coordinates = (Coordinate)data;
        if (subj instanceof Monster) {
            if (coordinates.equals(exit)) {
                this.notifyObservers("WIN");
            } else {
                this.notifyObservers(coordinates);
                if (monster.isAi()){
                    System.out.println("AI");
                    nextTurn();
                }
            }
        } else {
            if (coordinates.equals(getMonster().getCoord())) {
                this.notifyObservers("WIN");
            } else {
                this.notifyObservers(coordinates);
            }
        }
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.maze);
        oos.writeObject(this.turn);
        oos.writeObject(this.getWidth());
        oos.writeObject(this.getHeight());
        oos.writeObject(this.monsterName);
        oos.writeObject(this.hunterName);
        oos.writeObject(this.monster);
        oos.writeObject(this.hunter);
        oos.writeObject(this.entrance);
        oos.writeObject(this.exit);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        this.maze = (boolean[][])ois.readObject();
        this.turn = (Integer)ois.readObject();
        this.monsterName = (String)ois.readObject();
        this.hunterName = (String)ois.readObject();
        this.monster = (Monster)ois.readObject();
        this.hunter = (Hunter)ois.readObject();
        this.entrance = (Coordinate)ois.readObject();
        this.exit = (Coordinate)ois.readObject();
    }
}