package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas {
    private Maze maze;
    private Solution solution;
    // player position:
    private int playerRow = 0;
    private int playerCol = 0;
    // wall and player images:
    StringProperty imageFileNameWall = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNamePaw = new SimpleStringProperty();
    StringProperty imageFileNameBone = new SimpleStringProperty();

    public int getPlayerRow() {
        return playerRow;
    }

    public int getPlayerCol() {
        return playerCol;
    }

    public void setPlayerPosition(int row, int col) {
        this.playerRow = row;
        this.playerCol = col;
        draw();
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
        if (solution!=null)
            draw();
    }

    public String getImageFileNameWall() {
        return imageFileNameWall.get();
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public String getImageFileNamePaw() { return imageFileNamePaw.get(); }

    public String getImageFileNameBone() { return imageFileNameBone.get(); }

    public String imageFileNameWallProperty() {
        return imageFileNameWall.get();
    }

    public String imageFileNamePlayerProperty() {
        return imageFileNamePlayer.get();
    }

    public String imageFileNamePawProperty() {
        return imageFileNamePaw.get();
    }

    public String imageFileNameBoneProperty() {
        return imageFileNameBone.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.imageFileNameWall.set(imageFileNameWall);
    }

    public void setImageFileNamePaw(String imageFileNamePaw) {
        this.imageFileNamePaw.set(imageFileNamePaw);
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) { this.imageFileNamePlayer.set(imageFileNamePlayer); }

    public void setImageFileNameBone(String imageFileNameBone) { this.imageFileNameBone.set(imageFileNameBone); }

    public void drawMaze(Maze maze) {
        this.maze = maze;
        draw();
    }

    private void draw() {
        if(maze != null){
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int rows = maze.getMatrix().length;
            int cols = maze.getMatrix()[0].length;

            double cellHeight = canvasHeight / rows;
            double cellWidth = canvasWidth / cols;

            GraphicsContext graphicsContext = getGraphicsContext2D();
            GraphicsContext floor = getGraphicsContext2D();

            //clear the canvas:
            graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);

            drawMazeWalls(graphicsContext,floor, cellHeight, cellWidth, rows, cols);
            if(solution != null) {
                drawSolution(graphicsContext, cellHeight, cellWidth);
            }
            drawPlayer(graphicsContext, cellHeight, cellWidth);
            drawBone(graphicsContext,cellHeight, cellWidth, rows, cols);
        }
    }

    private void drawSolution(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        graphicsContext.setFill(Color.BURLYWOOD);

        Image pawImage = null;
        try{
            pawImage = new Image(new FileInputStream(getImageFileNamePaw()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no paw image file");
        }
        ArrayList<AState> solutionPath = solution.getSolutionPath();
        for (int i = 0; i < solutionPath.size(); i++){
            int col = ((MazeState)solutionPath.get(i)).getPosition().getColumnIndex();
            int row = ((MazeState)solutionPath.get(i)).getPosition().getRowIndex();
            double x = col * cellWidth;
            double y = row * cellHeight;
            if (pawImage == null)
                graphicsContext.fillRect(x,y,cellHeight,cellWidth);
            else
                graphicsContext.drawImage(pawImage, x, y, cellWidth, cellHeight);
        }
    }

    private  void drawBone(GraphicsContext bone,  double cellHeight, double cellWidth, int rows, int cols){
        Image boneImage = null;
        bone.setFill(Color.ALICEBLUE);
        try{
            boneImage = new Image(new FileInputStream(getImageFileNameBone()));
        }catch (FileNotFoundException e){
            System.out.println("There is no bone image file");
        }
        double x = (cols - 1) * cellWidth;
        double y = (rows - 1) * cellHeight;
        if (boneImage == null)
            bone.fillRect(x,y,cellWidth, cellHeight);
        else
            bone.drawImage(boneImage, x, y, cellWidth, cellHeight);
    }



    private void drawMazeWalls(GraphicsContext graphicsContext,GraphicsContext floor , double cellHeight, double cellWidth, int rows, int cols) {
        graphicsContext.setFill(Color.RED);
        floor.setFill(Color.BLANCHEDALMOND);

        Image wallImage = null;
        try{
            wallImage = new Image(new FileInputStream(getImageFileNameWall()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no wall image file");
        }
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double x = j * cellWidth;
                double y = i * cellHeight;
                if (maze.getMatrix()[i][j] == 0){
                    floor.fillRect(x, y, cellWidth, cellHeight);
                }
                if(maze.getMatrix()[i][j] == 1){
                    //if it is a wall:
                    if(wallImage == null)
                        graphicsContext.fillRect(x, y, cellWidth, cellHeight);
                    else
                        graphicsContext.drawImage(wallImage, x, y, cellWidth, cellHeight);
                }
            }
        }
    }

    private void drawPlayer(GraphicsContext graphicsContext, double cellHeight, double cellWidth) {
        double x = getPlayerCol() * cellWidth;
        double y = getPlayerRow() * cellHeight;
        graphicsContext.setFill(Color.GREEN);

        Image playerImage = null;
        try {
            playerImage = new Image(new FileInputStream(getImageFileNamePlayer()));
        } catch (FileNotFoundException e) {
            System.out.println("There is no player image file");
        }
        if(playerImage == null)
            graphicsContext.fillRect(x, y, cellWidth, cellHeight);
        else
            graphicsContext.drawImage(playerImage, x, y, cellWidth, cellHeight);
    }
}