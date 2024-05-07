package application;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneConfigController {
    private Stage stage;
    private Scene scene;
    private Parent root;

    private static String usuario;

    @FXML
    private ToggleButton fullScreenToggle;

    // Método estático para establecer el usuario
    public static void setUsuario(String usuario) {
        SceneConfigController.usuario = usuario;
    }

    // Navegacion entre pantallas
    public void switchPantallaPrincipal(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("PantallaPrincipal.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Método para manejar el evento de cambio de pantalla completa
    @FXML
    private void handleFullScreenToggle(ActionEvent event) {
        if (fullScreenToggle.isSelected()) {
            // Cambiar a pantalla completa
            stage.setFullScreen(true);
        } else {
            // Cambiar a ventana normal
            stage.setFullScreen(false);
        }
    }

}
