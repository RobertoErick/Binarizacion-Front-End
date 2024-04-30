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

		if (confirmContrasena.isEmpty()) {
			imgPasswordWrong.setVisible(false);
		}

	}

	private void mostrarAlerta(String mensaje, Alert.AlertType type) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}
}
