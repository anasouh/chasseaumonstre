package fr.univlille.info.J3.chasseaumonstre.model.strategy.hunter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

import SubjectObserver.Subject;
import fr.univlille.info.J3.chasseaumonstre.model.Coordinate;
import fr.univlille.iutinfo.cam.player.hunter.IHunterStrategy;
import fr.univlille.iutinfo.cam.player.perception.ICellEvent;
import fr.univlille.iutinfo.cam.player.perception.ICoordinate;

/*
 * Réprésente le chasseur et sa stratégie
 * 
 * @see IHunterStrategy
 * @author Anas Ouhdda
 * @author Atilla Tas
 * @author Karim Aoulad-Tayab
 * @author Selim Hamza
 * @author Yliess El Atifi
 */
public class Hunter extends Subject implements IHunterStrategy, Serializable {
    private boolean[][] shootLocations;
    private String name;
    private boolean[][] visited;
    private int[][] visitedTurn;
    private boolean ai;

    /*
     * Constructeur de Hunter
     * 
     * @param locations les coordonnées des tirs du chasseur
     */
    public void initialize(boolean[][] locations) {
        this.shootLocations = locations;
        this.visited = locations;
        this.visitedTurn = new int[locations.length][locations[0].length];
        for (int i = 0; i < locations.length; i++) {
            for (int j = 0; j < locations[0].length; j++) {
                visitedTurn[i][j] = -1;
            }
        }
    }

    /*
     * Constructeur de Hunter, qui initialise les coordonnées des tirs du chasseur
     * 
     * @param row la ligne de la cellule
     * @param col la colonne de la cellule
     */
    public void initialize(int row, int col) {
        this.shootLocations = new boolean[row][col];
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasShot(int x, int y) {
        return this.shootLocations[x][y];
    }

    /*
     * Effectue un tir sur une cellule et en informe le modèle principal
     * 
     * @param x la ligne de la cellule
     * @param y la colonne de la cellule
     */
    public void shoot(int x, int y) {
        this.shootLocations[x][y] = true;
        this.notifyObservers(new Coordinate(x, y));
    }

    public boolean isAi() {
        return ai;
    }

    public void setAi(boolean ai) {
        this.ai = ai;
    }

    /*
     * Joue un tour du chasseur
     
    @Override
    public ICoordinate play() {

        //throw new UnsupportedOperationException("Unimplemented method 'play'");
        
            // Parcours des emplacements possibles pour le tir
    for (int i = 0; i < shootLocations.length; i++) 
    {
        for (int j = 0; j < shootLocations[0].length; j++) 
        {
            // Vérifie si la case n'a pas déjà été explorée
            if (!hasShot(i, j)) 
            {
                // Vérifie si la case n'a pas été visitée par le monstre ou ses voisines
                if (!isVisitedByMonsterOrNeighbors(i, j)) 
                {
                    // Si la case est valide, effectue le tir
                    shoot(i, j);
                    // Retourne les coordonnées de la case tirée
                    return new Coordinate(i,j);
                        
                }
            }
        }
    }

    return null;

    }
    */

    @Override
public ICoordinate play() {
    Random random = new Random();
    int row, col;

    // Tant que la case générée aléatoirement a déjà été tirée, générez une nouvelle case
    while (true) 
    {
        row = random.nextInt(shootLocations.length);
        col = random.nextInt(shootLocations[0].length);

        // Vérifie si la case n'a pas encore été tirée
        if (!hasShot(row, col)) {
            // Effectuer le tir
            shoot(row, col);

            // Retourner les coordonnées de la case tirée
            return new Coordinate(row,col);
        }
        // Sinon, répéter le processus pour générer de nouvelles coordonnées
    }
}


    private boolean isVisitedByMonsterOrNeighbors(int x, int y) {
        // Vérifie si la case elle-même ou ses voisines ont été visitées par le monstre
        for (int i = Math.max(0, x - 1); i <= Math.min(shootLocations.length - 1, x + 1); i++) {
            for (int j = Math.max(0, y - 1); j <= Math.min(shootLocations[0].length - 1, y + 1); j++) {
                if (isVisited(i, j)) {
                    return true;
                }
            }
        }
        return false;
    }

    


    /*
     * Met à jour les coordonnées des tirs du chasseur
     * 
     * @param event l'événement qui se produit sur une cellule
     */
    @Override
    public void update(ICellEvent event) {
        ICoordinate coord = event.getCoord();
        this.shoot(coord.getRow(), coord.getCol());
    }

    /*
     * Connaitre si une cellule a été visitée
     * 
     * @param x la ligne de la cellule
     * @param y la colonne de la cellule
     * @return true si la cellule a été visitée, false sinon
     */
    public boolean isVisited(int x, int y) {
        return visited[x][y];
    }

    /*
     * Connaitre à quel tour une cellule a été visitée
     * 
     * @param x la ligne de la cellule
     * @param y la colonne de la cellule
     * @return le tour auquel la cellule a été visitée
     */
    public int getVisitedTurn(int x, int y) {
        return visitedTurn[x][y];
    }

    /*
     * Met à jour la cellule visitée
     * 
     * @param cellX la ligne de la cellule
     * @param cellY la colonne de la cellule
     * @param turn le tour auquel la cellule a été visitée
     */
    public void setVisited(int cellX, int cellY, int turn) {
        this.visited[cellX][cellY] = true;
        this.visitedTurn[cellX][cellY] = turn;
        this.notifyObservers();
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.shootLocations);
        oos.writeObject(this.name);
        oos.writeObject(this.visited);
        oos.writeObject(this.visitedTurn);
    }

    private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
        this.shootLocations = (boolean[][])ois.readObject();
        this.name = (String)ois.readObject();
        this.visited = (boolean[][])ois.readObject();
        this.visitedTurn = (int[][])ois.readObject();
    }
}
