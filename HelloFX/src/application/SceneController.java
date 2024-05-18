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
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
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

public class SceneController {
	
	private Stage stage;
	private Scene scene;
	private Parent root;
	
    private ResourceBundle bundle;
    private Locale locale;
    
    private static final String LANGUAGE_PREF_KEY = "language";
    private Preferences prefs;
	
	@FXML
	private Label LabelUsuario;

    private static String usuario;
    
	@FXML
	private Button btnProcesarImagenes;
	@FXML
	private Button btnHistorial;
	@FXML
	private Button btnConfiguracion;
	@FXML
	private Button btnSoporte;
	@FXML
	private Button btnCerrarSesion;
	@FXML
	private Button btnSalir;

    // Método estático para establecer el usuario
    public static void setUsuario(String usuario) {
        SceneController.usuario = usuario;
        
    }
    @FXML
    private void initialize() {
    	prefs = Preferences.userNodeForPackage(SceneController.class);
        String language = prefs.get(LANGUAGE_PREF_KEY, "en"); // Valor por defecto es inglés
        setLanguage(language);
    	// Encontrar la posición del símbolo '@'
    	int indiceArroba = usuario.indexOf('@');
    	// Cortar el correo electrónico antes del símbolo '@' utilizando el método substring
    	String user = usuario.substring(0, indiceArroba);
    	LabelUsuario.setText(user);
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

    	btnProcesarImagenes.setText(bundle.getString("menu.procesar"));
    	btnHistorial.setText(bundle.getString("menu.historial"));
    	btnConfiguracion.setText(bundle.getString("menu.config"));
    	btnSoporte.setText(bundle.getString("menu.sup"));
        btnCerrarSesion.setText(bundle.getString("menu.logoff"));
        btnSalir.setText(bundle.getString("menu.exit"));
    }

    
 // Event Listener on Button.onAction
 	@FXML
 	public void switchPantallaPrincipal(ActionEvent event) throws IOException {
 		Parent root = FXMLLoader.load(getClass().getResource("PantallaPrincipal.fxml"));
 		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
 		scene = new Scene(root);
 		stage.setScene(scene);
 		stage.show();
 	}

	public void switchPantallaProcesar(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaProcesar.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		PantallaProcesarController.setUsuario(usuario);
	}
	
	public void switchPantallaSignUp(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaSignUp.fxml"));
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
	
	public void switchPantallaHistory(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaHistory.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		SceneHistoryController.setUsuario(usuario);
	}
	
	public void switchPantallaSupport(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaSupport.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		SceneSupportController.setUsuario(usuario);
	}
	
	public void switchPantallaConfig(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaConfig.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		
		SceneConfigController.setUsuario(usuario);

	}
	
	// Accion de Cerrar Sesion de la PantallaPrincipal
	public void CerrarSesion(ActionEvent event) throws IOException {
		// TODO Autogenerated
		LabelUsuario.setText("");
		switchPantallaLogin(event);
	}

	// Accion de salir del programa de la PantallaPrincipal
	public void Salir(ActionEvent event) {
		// TODO Autogenerated
		Stage stage = (Stage) btnSalir.getScene().getWindow();
		stage.close();
	}
}