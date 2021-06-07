package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;

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
}
