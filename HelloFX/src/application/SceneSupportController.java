package application;

import java.io.IOException;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SceneSupportController {
	private Stage stage;
	private Scene scene;
	private Parent root;
	
    private ResourceBundle bundle;
    private Locale locale;
    
    private static final String LANGUAGE_PREF_KEY = "language";
    private Preferences prefs;

	// Navegacion entre pantallas
	public void switchPantallaPrincipal(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaPrincipal.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	

    @FXML
    private TextField txtAsunto;
   
    @FXML
    private TextArea txtMensaje;
    
    @FXML
    private Label title;
    
    @FXML
    private Label subtitle;
    @FXML
    private Button send;
    
    
    
    @FXML
    private void initialize() {
    	
    	prefs = Preferences.userNodeForPackage(SceneSupportController.class);
        String language = prefs.get(LANGUAGE_PREF_KEY, "en"); // Valor por defecto es inglés
        setLanguage(language);

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
    	txtAsunto.setPromptText(bundle.getString("support.subject"));
    	txtMensaje.setPromptText(bundle.getString("support.msj"));
    	title.setText(bundle.getString("support.title"));
        subtitle.setText(bundle.getString("support.subtitle"));
        send.setText(bundle.getString("support.send"));

    }

    
    private static String usuario;

    // Método estático para establecer el usuario
    public static void setUsuario(String usuario) {
        SceneSupportController.usuario = usuario;
        
    }

    // Método para enviar el formulario de contacto
    @FXML
    private void enviarFormulario(ActionEvent event) {
    	// Validar que los campos no estén vacíos
        if (txtAsunto.getText().isEmpty() || txtMensaje.getText().isEmpty() || usuario.isEmpty()) {
            mostrarAlerta(bundle.getString("support.alert1"), AlertType.WARNING);
            return; // Salir del método si algún campo está vacío
        }
        
    	// Encontrar la posición del símbolo '@'
    	int indiceArroba = usuario.indexOf('@');
    	// Cortar el correo electrónico antes del símbolo '@' utilizando el método substring
    	String user = usuario.substring(0, indiceArroba);

        // Obtener los valores ingresados en los campos del formulario
        String nombre = user;
        String asunto = txtAsunto.getText();
        String correo = usuario;
        String mensaje = txtMensaje.getText();

        // Configuración para enviar el correo electrónico
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com"); // Reemplaza con tu servidor SMTP
        props.put("mail.smtp.port", "587"); // Puerto SMTP

        // Información de autenticación
        String username = "luahotel44@gmail.com"; // Reemplaza con tu dirección de correo
        String password = "tdvz slyg jcnw nfep"; // Reemplaza con tu contraseña

        // Crear una sesión de correo electrónico
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Crear un mensaje de correo electrónico
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("microcontroladoresati2020@gmail.com")); // Reemplaza con la dirección del destinatario
            message.setSubject("Formulario de contacto");
            message.setText("Nombre: " + nombre + "\nCorreo: " + correo + "\nAsunto: " + asunto + "\nMensaje: " + mensaje);

            // Enviar el mensaje de correo electrónico
            Transport.send(message);

            // Mostrar una alerta de éxito
            mostrarAlerta(bundle.getString("support.alert2"), AlertType.INFORMATION);
            
            txtAsunto.setText(null);
            txtMensaje.setText(null);

            

        } catch (MessagingException e) {
            // Mostrar una alerta si ocurre un error al enviar el correo
            mostrarAlerta(bundle.getString("support.alert3") + e.getMessage(), AlertType.ERROR);
        }
    }

    // Método para mostrar una alerta
    private void mostrarAlerta(String mensaje, AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
