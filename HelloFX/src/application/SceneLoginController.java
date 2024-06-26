package application;

import javafx.fxml.FXML;
import javafx.scene.image.Image;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;
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
import java.util.ResourceBundle;
import java.util.Locale;
import java.util.MissingResourceException;

public class SceneLoginController {
    private ResourceBundle bundle;
    private Locale locale;
    
    @FXML
    private TextField txtUsuario;
    @FXML
    private TextField txtContrasenaAux;
    @FXML
    private PasswordField txtContrasena;

    @FXML
    private ImageView chkMostrarContrasena;

    @FXML
    private CheckBox chkRecordar;

    private Preferences prefs;
    
    @FXML
    private Button btnIniciarSesion;
    @FXML
    private Button btnSwitchRegistro;
    @FXML
    private Button btnOlvidar;
    @FXML
    private Label greetings;


    private static final String LANGUAGE_PREF_KEY = "language";
    
    @FXML
    private void initialize() {
    	prefs = Preferences.userNodeForPackage(SceneLoginController.class);
        String language = prefs.get(LANGUAGE_PREF_KEY, "en"); // Valor por defecto es inglés
        setLanguage(language);
    	// Inicializar el objeto Preferences
        prefs = Preferences.userNodeForPackage(SceneLoginController.class);

    	cargarDatosGuardados();
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

        // Carga las imágenes del ojo abierto y cerrado
        Image ojoAbierto = new Image(getClass().getResourceAsStream("/Imagenes/Escena Registro/ojo_abierto.png"));
        Image ojoCerrado = new Image(getClass().getResourceAsStream("/Imagenes/Escena Registro/ojo_cerrado.png"));

        // Configura la imagen inicial del icono del ojo como cerrado
        chkMostrarContrasena.setImage(ojoAbierto);
    }

    private void setLanguage(String language) {
        try {
            locale = new Locale(language);
            bundle = ResourceBundle.getBundle("application.messages", locale);
            applyLanguage();
        } catch (MissingResourceException e) {
            e.printStackTrace();
            System.out.println("Could not find resource bundle for language: " + language);
        }
    }
    
    private void applyLanguage() {
        txtUsuario.setPromptText(bundle.getString("login.email"));
        txtContrasena.setPromptText(bundle.getString("login.password"));
        txtContrasenaAux.setPromptText(bundle.getString("login.password"));
        chkRecordar.setText(bundle.getString("login.rememberMe"));
        btnOlvidar.setText(bundle.getString("login.forgotPassword"));
        btnIniciarSesion.setText(bundle.getString("login.signIn"));
        btnSwitchRegistro.setText(bundle.getString("login.signUp"));
        greetings.setText(bundle.getString("login.welcome"));
    }

    
    private void cargarDatosGuardados() {
        String usuarioGuardado = prefs.get("usuario", "");
        String contrasenaGuardada = prefs.get("contrasena", "");
        boolean recordar = prefs.getBoolean("recordar", false);
        
        txtUsuario.setText(usuarioGuardado);
        txtContrasena.setText(contrasenaGuardada);
        txtContrasenaAux.setText(contrasenaGuardada);
        chkRecordar.setSelected(recordar);
    }
    
    @FXML
    public void toggleMostrarContrasena(MouseEvent event) {
    	   // Carga las imágenes del ojo abierto y cerrado
        Image ojoAbierto = new Image(getClass().getResourceAsStream("/Imagenes/Escena Registro/ojo_abierto.png"));
        Image ojoCerrado = new Image(getClass().getResourceAsStream("/Imagenes/Escena Registro/ojo_cerrado.png"));

        if (txtContrasena.isVisible()) {
        	txtContrasenaAux.setText(txtContrasena.getText());
            txtContrasena.setVisible(false);
            txtContrasenaAux.setVisible(true);
            chkMostrarContrasena.setImage(ojoCerrado);

        } else {
            txtContrasena.setVisible(true);
        	txtContrasena.setText(txtContrasenaAux.getText());
            txtContrasenaAux.setVisible(false);
            txtContrasena.setVisible(true);

            chkMostrarContrasena.setImage(ojoAbierto);

        }
        
        chkMostrarContrasena.toFront();

    }

    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchPantallaPrincipal2() throws IOException {
        // Pasar el valor del usuario a la siguiente pantalla
        String user = txtUsuario.getText();

        SceneController.setUsuario(user);
        // Utilizar cualquier nodo de la escena para obtener la ventana y la escena actual
        Parent root = FXMLLoader.load(getClass().getResource("PantallaPrincipal.fxml"));
        Scene scene = txtUsuario.getScene();
        Stage stage = (Stage) scene.getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

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

    public void iniciarSesion(ActionEvent event) throws IOException {
    	
    	 if (!txtContrasena.isVisible()) {
          	txtContrasena.setText(txtContrasenaAux.getText());
         } 
    	
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
            	if (chkRecordar.isSelected()) {
                    prefs.put("usuario", usuario);
                    prefs.put("contrasena", contrasena);
                    prefs.putBoolean("recordar", true);
                } else {
                    prefs.remove("usuario");
                    prefs.remove("contrasena");
                    prefs.putBoolean("recordar", false);
                }
                // La solicitud fue exitosa
                mostrarAlerta(bundle.getString("login.alert6"), Alert.AlertType.INFORMATION);
                // Si las credenciales son válidas, puedes cambiar a la siguiente pantalla
                switchPantallaPrincipal2();
            } else {
                // La solicitud falló
                String errorMessage = connection.getResponseMessage();
                mostrarAlerta(bundle.getString("login.alert5") + errorMessage, Alert.AlertType.ERROR);
            }
        } catch (IOException e) {
            mostrarAlerta(bundle.getString("login.alert3") + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
 // Método para restablecer la contraseña
    @FXML
    private void restablecerContrasena(ActionEvent event) {
        String usuario = txtUsuario.getText();

        // Validar que se haya ingresado un usuario
        if (usuario.isEmpty()) {
            mostrarAlerta(bundle.getString("login.alert4"), AlertType.WARNING);
            return;
        }

        try {
            // Construir el cuerpo de la solicitud
            JsonObject requestBody = new JsonObject();
            requestBody.addProperty("email", usuario);
            requestBody.addProperty("requestType", "PASSWORD_RESET");
            String requestBodyString = requestBody.toString();

            // Establecer la URL de la solicitud
            URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=AIzaSyCF1YvvKuz-i5XhYs3HVtOQFYUFMityWWk");

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
                mostrarAlerta(bundle.getString("login.alert1"), AlertType.INFORMATION);
            } else {
                // La solicitud falló
                String errorMessage = connection.getResponseMessage();
                mostrarAlerta(bundle.getString("login.alert2") + errorMessage, AlertType.ERROR);
            }
        } catch (IOException e) {
            mostrarAlerta(bundle.getString("login.alert3") + e.getMessage(), AlertType.ERROR);
        }
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType type) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
