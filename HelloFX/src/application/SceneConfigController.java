package application;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
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
    
    private ResourceBundle bundle;
    private Locale locale;
    
    private static String usuario;
    @FXML
    private MenuItem menuItemEspañol;
    @FXML
    private MenuItem menuItemIngles;
    @FXML
    private MenuButton menuButtonIdioma;
    @FXML
    private Label title;
    @FXML
    private Label change;
    
    private static final String LANGUAGE_PREF_KEY = "language";
    private Preferences prefs;

    // Método estático para establecer el usuario
    public static void setUsuario(String usuario) {
        SceneConfigController.usuario = usuario;
    }
    
    @FXML
    private void initialize() {
    	prefs = Preferences.userNodeForPackage(SceneConfigController.class);
        String language = prefs.get(LANGUAGE_PREF_KEY, "en"); // Valor por defecto es inglés
        setLanguage(language);
        
        menuItemEspañol.setOnAction(this::cambiarIdiomaEspañol);
        menuItemIngles.setOnAction(this::cambiarIdiomaIngles);
    }
 // Método para manejar el cambio de idioma a español
    private void cambiarIdiomaEspañol(ActionEvent event) {
        setLanguage("en");
        saveLanguagePreference("en");
    }

    // Método para manejar el cambio de idioma a inglés
    private void cambiarIdiomaIngles(ActionEvent event) {
        setLanguage("es");
        saveLanguagePreference("es");
    }
    
    // Guardar la preferencia de idioma
    private void saveLanguagePreference(String language) {
        prefs.put(LANGUAGE_PREF_KEY, language);
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
    	menuItemEspañol.setText(bundle.getString("config.esp"));
    	menuItemIngles.setText(bundle.getString("config.en"));
    	menuButtonIdioma.setText(bundle.getString("config.language"));
        title.setText(bundle.getString("config.title"));
        change.setText(bundle.getString("config.change"));
    }

    // Navegacion entre pantallas
    public void switchPantallaPrincipal(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("PantallaPrincipal.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



}
