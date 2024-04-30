package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.awt.image.BufferedImage;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class SceneLoginController {
	@FXML
	private void initialize() {
		// Agregar un evento de teclado al campo de contraseña
		txtContrasena.setOnKeyPressed(event -> {
			// Verificar si la tecla presionada es Enter
			if (event.getCode().equals(KeyCode.ENTER)) {
				// Llamar al método iniciarSesion
				try {
					iniciarSesion(new ActionEvent());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

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

	public void switchPantallaSignUp(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaSignUp.fxml"));

		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	@FXML
	private TextField txtUsuario;
	@FXML
	private PasswordField txtContrasena;

	public void iniciarSesion(ActionEvent event) throws IOException {
		String usuario = txtUsuario.getText();
		String contrasena = txtContrasena.getText();

		// Aquí puedes verificar las credenciales, por ejemplo, comparándolas con
		// valores fijos

		try {
			// Construir el cuerpo de la solicitud
			JsonObject requestBody = new JsonObject();
			requestBody.addProperty("email", usuario);
			requestBody.addProperty("password", contrasena);
			String requestBodyString = requestBody.toString();

			// Establecer la URL de la solicitud
			URL url = new URL(
					"https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyCF1YvvKuz-i5XhYs3HVtOQFYUFMityWWk");

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
				mostrarAlerta("Inicio de sesión exitoso.", Alert.AlertType.INFORMATION);
				// Si las credenciales son válidas, puedes cambiar a la siguiente pantalla
				switchPantallaPrincipal(event);
			} else {
				// La solicitud falló
				String errorMessage = connection.getResponseMessage();
				mostrarAlerta("Error de inicio de sesión: " + errorMessage, Alert.AlertType.ERROR);
			}
		} catch (IOException e) {
			mostrarAlerta("Error de conexión: " + e.getMessage(), Alert.AlertType.ERROR);
		}

	}

	private void mostrarAlerta(String mensaje, Alert.AlertType type) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}
}
