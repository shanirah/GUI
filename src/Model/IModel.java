package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observer;


public interface IModel {
    void generateMaze(int rows, int cols) throws UnknownHostException;
    Maze getMaze();
    void updatePlayerLocation(MovementDirection direction);
    int getPlayerRow();
    int getPlayerCol();
    void assignObserver(Observer o);
    void solveMaze() throws UnknownHostException;
    Solution getSolution();

    void saveMaze() throws IOException;

    void newMaze() throws UnknownHostException;
    void exit();
    void loadFile(File chosen);
    void drag(MouseEvent mouseEvent, double canvasWidth, double canvasHeight);

    void help();

    void scroll(ScrollEvent scrollEvent);
}
