package ViewModel;

import Model.IModel;
import Model.MovementDirection;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer{
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
        this.model.assignObserver(this); //Observe the Model for it's changes
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    public Maze getMaze(){
        return model.getMaze();
    }

    public int getPlayerRow(){
        return model.getPlayerRow();
    }

    public int getPlayerCol(){
        return model.getPlayerCol();
    }

    public Solution getSolution(){
        return model.getSolution();
    }

    public void generateMaze(int rows, int cols) throws UnknownHostException {
        model.generateMaze(rows, cols);
    }


    public void movePlayer(KeyEvent keyEvent){
        MovementDirection direction;
        switch (keyEvent.getCode()){
            case NUMPAD8:
            case UP:
                direction = MovementDirection.UP;
                break;
            case NUMPAD2:
            case DOWN:
                direction = MovementDirection.DOWN;
                break;
            case NUMPAD4:
            case LEFT:
                direction = MovementDirection.LEFT;
                break;
            case NUMPAD6:
            case RIGHT:
                direction = MovementDirection.RIGHT;
                break;
            case NUMPAD1:
                direction = MovementDirection.DOWNLEFT;
                break;
            case NUMPAD3:
                direction = MovementDirection.DOWNRIGHT;
                break;
            case NUMPAD7:
                direction = MovementDirection.UPLEFT;
                break;
            case NUMPAD9:
                direction = MovementDirection.UPRIGHT;
                break;
            default:
                // no need to move the player...
                return;
        }
        model.updatePlayerLocation(direction);
    }

    public void solveMaze() throws UnknownHostException {
        model.solveMaze();
    }
    public void saveMaze() throws IOException {
        model.saveMaze();
    }

    public void newMaze() throws UnknownHostException {
        model.newMaze();
    }

    public void drag(MouseEvent mouseEvent, double height, double width) {
        model.drag(mouseEvent, height , width);
    }

    public void exit() {
        model.exit();
    }

    public void loadFile(File chosen) {
        model.loadFile(chosen);
    }


    public void scroll(ScrollEvent scrollEvent) {
        model.scroll(scrollEvent);
    }
}
