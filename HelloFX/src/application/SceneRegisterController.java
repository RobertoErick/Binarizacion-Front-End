package application;

import javafx.beans.value.ChangeListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import javafx.scene.control.Control;
import javafx.stage.Stage;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;

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
	
	@FXML
	private Label lblMensaje1;
	@FXML
	private ImageView imgPasswordWrong1;
	
	@FXML
	private Label lblMensaje2;
	@FXML
	private ImageView imgPasswordWrong2;

	 @FXML
	private ImageView infoIcon;
	 @FXML
	 private StackPane infoIconContainer;

	 
	 
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
		// Crear un tooltip para el campo de contraseña
        Tooltip tooltip = new Tooltip("La contraseña debe contener al menos una letra minúscula, una letra mayúscula, un número y tener una longitud mínima de 8 caracteres.");
     // Establecer el ancho máximo del Tooltip
        tooltip.setPrefWidth(300); // Establecer el ancho máximo a 300 
        tooltip.setPrefHeight(70);
        tooltip.setWrapText(true); // Habilitar el ajuste automático de texto

        // Asignar el tooltip al campo de contraseña
        Tooltip.install(txtContrasenaRegistro, tooltip);
        
     
        infoIconContainer.setOnMouseEntered((MouseEvent event) -> {
            if (!tooltip.isShowing()) {
                tooltip.show(infoIconContainer, event.getScreenX(), event.getScreenY());
            }
        });

        infoIconContainer.setOnMouseExited((MouseEvent event) -> {
            tooltip.hide(); // Oculta el Tooltip cuando el mouse sale del ícono
        });



		// Inicialización del controlador
		initializeSignUpScreen();
		

        // Establecer el ancho máximo para el wrapping del texto
        lblMensaje2.setWrapText(true);
        lblMensaje2.setMaxWidth(270);

	}
	
	 // Método para validar correo electrónico
    private boolean validarCorreoElectronico(String correo) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

	// Métodos para manejar eventos, validaciones, etc.
	public void registro(ActionEvent event) throws IOException {
		String usuario = txtUsuarioRegistro.getText();
		String contrasena = txtContrasenaRegistro.getText();
		String confirmContrasena = txtConfirmContrasena.getText();

		if (contrasena.equals(confirmContrasena)) {
			try {
				// Construir el cuerpo de la solicitud
				JsonObject requestBody = new JsonObject();
				requestBody.addProperty("email", usuario);
				requestBody.addProperty("password", contrasena);
				String requestBodyString = requestBody.toString();

				// Establecer la URL de la solicitud
				URL url = new URL(
						"https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=AIzaSyCF1YvvKuz-i5XhYs3HVtOQFYUFMityWWk");

				// Abrir una conexión HTTP
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setDoOutput(true);

				// Escribir el cuerpo de la solicitud en la conexión
				try (OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())) {
					writer.write(requestBodyString);
				}

				// Leer la respuesta
				int responseCode = connection.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_OK) {
					// La solicitud fue exitosa
					mostrarAlerta("Usuario registrado correctamente.", Alert.AlertType.INFORMATION);
					txtUsuarioRegistro.setText("");
					txtContrasenaRegistro.setText("");
					txtConfirmContrasena.setText("");
					switchPantallaLogin(event);
				} else {
					// La solicitud falló
					String errorMessage = connection.getResponseMessage();
					mostrarAlerta("Error al registrar usuario: " + errorMessage, Alert.AlertType.ERROR);
				}
			} catch (IOException e) {
				mostrarAlerta("Error de conexión: " + e.getMessage(), Alert.AlertType.ERROR);
			}
		} else {
			// Si las credenciales no son válidas, muestra un mensaje de error
			mostrarAlerta("Las contraseñas no coinciden", Alert.AlertType.ERROR);
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

		txtContrasenaRegistro.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (validarContrasena(txtContrasenaRegistro.getText())) {
					lblMensaje2.setText("");
					imgPasswordWrong2.setVisible(false);
				}
				else {
					
				}
				
				validarContraseñas();
				
				if(txtContrasenaRegistro.getText().isEmpty()) {
					lblMensaje2.setText("");
					imgPasswordWrong2.setVisible(false);
				}
				
				
			}
		});
		
		txtUsuarioRegistro.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (validarCorreoElectronico(txtUsuarioRegistro.getText())) {
		            // El correo electrónico es válido, continuar con el registro
					lblMensaje1.setText("");
					imgPasswordWrong1.setVisible(false);
		        } else {
		            // El correo electrónico no es válido, mostrar mensaje de error
		        	lblMensaje1.setText("Correo electrónico no válido.");
					imgPasswordWrong1.setVisible(true);
		        }
				if(txtUsuarioRegistro.getText().isEmpty()) {
					lblMensaje1.setText("");
					imgPasswordWrong1.setVisible(false);
				}
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
			imgPasswordWrong.setVisible(false);
		}

		if (confirmContrasena.isEmpty()) {
			imgPasswordWrong.setVisible(false);
		}

	}

	// Método para validar contraseña
	private boolean validarContrasena(String contrasena) {
	    // La contraseña debe contener al menos una letra minúscula, una letra mayúscula,
	    // un número y tener una longitud mínima de 8 caracteres
	    String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$";
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(contrasena);
	    return matcher.matches();
	}
	private void mostrarAlerta(String mensaje, Alert.AlertType type) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}
}