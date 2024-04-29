package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SceneRegisterController implements Initializable {
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	// Navegacion entre pantallas
	public void switchPantallaPrincipal(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaPrincipal.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	public void switchPantallaLogin(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaLogin.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	
	@FXML
    private TextField txtUsuarioRegistro;

    @FXML
    private PasswordField txtContrasenaRegistro;

    @FXML
    private PasswordField txtConfirmContrasena;

    @FXML
    private Label lblMensaje;
    @FXML
    private ImageView imgPasswordWrong;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Inicialización del controlador
        initializeSignUpScreen();

    }

    // Métodos para manejar eventos, validaciones, etc.
    public void registro(ActionEvent event) throws IOException {
	    String usuario = txtUsuarioRegistro.getText();
	    String contrasena = txtContrasenaRegistro.getText();
	    String confirmContrasena = txtConfirmContrasena.getText();

	    if(contrasena.equals(confirmContrasena)) {
		    // Aquí puedes verificar las credenciales, por ejemplo, comparándolas con valores fijos
		    if (usuario.equals("usuario") && contrasena.equals("contrasena")) {
		        // Si las credenciales son válidas, puedes cambiar a la siguiente pantalla
		        switchPantallaPrincipal(event);
		    } else {
		        // Si las credenciales no son válidas, muestra un mensaje de error
		        mostrarAlerta("Nombre de usuario o contraseña incorrectos.", "Error de registro");
		    }
	    }
	    else {
	    	// Si las credenciales no son válidas, muestra un mensaje de error
	        mostrarAlerta("Las contraseñas no coinciden", "Error");
	    }
	}
	


    public void initializeSignUpScreen() {
        // Agregar un ChangeListener al campo de confirmación de contraseña
        txtConfirmContrasena.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                validarContraseñas();
            }
        });

        
    }

    private void validarContraseñas() {
        // Obtener los valores de ambos campos de contraseña
        String contrasena = txtContrasenaRegistro.getText();
        String confirmContrasena = txtConfirmContrasena.getText();

        // Verificar si ambos campos tienen texto
        if (!contrasena.isEmpty() && !confirmContrasena.isEmpty()) {
            // Verificar si las contraseñas son iguales
            if (contrasena.equals(confirmContrasena)) {
                lblMensaje.setText("");
                imgPasswordWrong.setVisible(false);
                // Puedes habilitar un botón de registro aquí si es necesario
            } else {
                lblMensaje.setText("Las contraseñas no coinciden.");
                imgPasswordWrong.setVisible(true);
                // Puedes deshabilitar un botón de registro aquí si es necesario
            }
        } else {
            // Si alguno de los campos está vacío, no realizar ninguna validación
            lblMensaje.setText("");
        }
        
        if(confirmContrasena.isEmpty()) {
        	imgPasswordWrong.setVisible(false);
        }
        
        
    }


    private void mostrarAlerta(String mensaje, String title) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}
}


