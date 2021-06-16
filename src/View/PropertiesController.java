package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesController extends AView implements Initializable, Observer, IView{
    public Configurations conf;
    public Properties prop;
    public TextField textField_threadsNum;
    public TextField textField_generatingAlgo;
    public TextField textField_solvingAlgo;


    public void saveChanges(MouseEvent mouseEvent) {
        /**
        try {
            conf = Configurations.getInstance();
            prop = conf.getProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.prop.setProperty("threadPoolSize", textField_threadsNum.getText());
        this.prop.setProperty("mazeGeneratingAlgorithm", textField_generatingAlgo.getText());
        this.prop.setProperty("mazeSearchingAlgorithm", textField_solvingAlgo.getText());
         **/
        moveScene("MyView.fxml", 1000,700, "Welcome to the maze");
    }

    public void aboutTheAlgo(MouseEvent mouseEvent) {
        moveScene("About.fxml", 400, 400, "about us");
    }

    @Override
    public void setViewModel(MyViewModel viewModel) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void backToGame(MouseEvent mouseEvent) {
        moveScene("MyView.fxml", 1000,700, "Welcome to the maze");
    }
}
