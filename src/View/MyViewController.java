package View;


import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;


import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class MyViewController extends AView implements Initializable, Observer, IView {
    public MyViewModel viewModel;


    public TextField textField_mazeRows;
    public TextField textField_mazeColumns;
    public MazeDisplayer mazeDisplayer;
    public Label playerRow;
    public Label playerCol;
    public CheckBox music;

    StringProperty updatePlayerRow = new SimpleStringProperty();
    StringProperty updatePlayerCol = new SimpleStringProperty();

    public String getUpdatePlayerRow() {
        return updatePlayerRow.get();
    }

    public void setUpdatePlayerRow(int updatePlayerRow) {
        this.updatePlayerRow.set(updatePlayerRow + "");
    }

    public String getUpdatePlayerCol() {
        return updatePlayerCol.get();
    }

    public void setUpdatePlayerCol(int updatePlayerCol) {
        this.updatePlayerCol.set(updatePlayerCol + "");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playerRow.textProperty().bind(updatePlayerRow);
        playerCol.textProperty().bind(updatePlayerCol);
    }

    public void generateMaze(ActionEvent actionEvent) throws UnknownHostException {
        int rows = Integer.valueOf(textField_mazeRows.getText());
        int cols = Integer.valueOf(textField_mazeColumns.getText());

        viewModel.generateMaze(rows, cols);
    }

    public void solveMaze(ActionEvent actionEvent) throws UnknownHostException {
        viewModel.solveMaze();
    }



    public void loadFile(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open maze");
       // fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Maze files (.maze)", ".maze"));
        fc.setInitialDirectory(new File("./Resources/MazeFiles"));
        File chosen = fc.showOpenDialog(null);
        viewModel.loadFile(chosen);
    }

    public void keyPressed(KeyEvent keyEvent) {
        viewModel.movePlayer(keyEvent);
        keyEvent.consume();
    }

    public void setPlayerPosition(int row, int col){
        mazeDisplayer.setPlayerPosition(row, col);
        setUpdatePlayerRow(row);
        setUpdatePlayerCol(col);
    }

    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }

    @Override
    public void update(Observable o, Object arg) {
        String change = (String) arg;
        switch (change){
            case "maze generated":
                mazeGenerated();
                break;
            case "player moved":
                playerMoved();
                break;
            case "maze solved":
                mazeSolved();
                break;
            default:
                System.out.println("Not implemented change: " + change);
                break;
        }
    }

    private void mazeSolved() {
        mazeDisplayer.setSolution(viewModel.getSolution());
    }

    private void playerMoved() {
        setPlayerPosition(viewModel.getPlayerRow(), viewModel.getPlayerCol());
    }

    private void mazeGenerated() {
        mazeDisplayer.setSolution(null);
        mazeDisplayer.drawMaze(viewModel.getMaze());
    }

    public void about(MouseEvent mouseEvent) throws IOException {
        moveScene("About.fxml", 400, 400, "about us");


    }


    public void backgroundMusic(MouseEvent mouseEvent) throws IOException, UnsupportedAudioFileException {
        Media sound = new Media(new File("Resources/sounds/backgroundMusic.mp3").toURI().toString());
        MediaPlayer player = new MediaPlayer(sound);
        player.setVolume(0.05);
        player.setOnEndOfMedia(() -> player.seek(Duration.ZERO));
        player.play();
    }

    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }
    public void saveMaze(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("the maze is in mazeFiles folder");
        alert.show();
        viewModel.saveMaze();
    }

    public void newMaze(ActionEvent actionEvent) throws UnknownHostException {
        viewModel.newMaze();
    }

    public void drag(MouseEvent mouseEvent) {
        viewModel.drag(mouseEvent, this.mazeDisplayer.getHeight(),this.mazeDisplayer.getWidth());
        mouseEvent.consume();
    }

    public void exit(Event event) {
        viewModel.exit();
    }

    public void help(MouseEvent mouseEvent) {
        moveScene("Help.fxml", 500, 500, "How can we help you?");
    }

    public void scroll(ScrollEvent scrollEvent) {
        double zoom=1;
        if(scrollEvent.isControlDown()){
            if(scrollEvent.getDeltaY()<0) {
                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX()*0.5);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY()*0.5);

            }
           else if(scrollEvent.getDeltaY()>0) {
                mazeDisplayer.setScaleX(mazeDisplayer.getScaleX()*1.5);
                mazeDisplayer.setScaleY(mazeDisplayer.getScaleY()*1.5);
            }
            if(scrollEvent.getDeltaY()<0.9) {
                mazeDisplayer.setScaleX(1);
                mazeDisplayer.setScaleY(1);

            }
        }



    }

    public void properties(ActionEvent actionEvent) {
        moveScene("Properties.fxml", 400, 400, "properties");
    }
}