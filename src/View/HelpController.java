package View;

import ViewModel.MyViewModel;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

public class HelpController extends AView implements Initializable, Observer, IView {
    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        this.viewModel.addObserver(this);
    }
    public void back(MouseEvent mouseEvent) {
        moveScene("MyView.fxml", 1000,700, "Welcome to the maze");
    }



    @Override
    public void update(Observable o, Object arg) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
