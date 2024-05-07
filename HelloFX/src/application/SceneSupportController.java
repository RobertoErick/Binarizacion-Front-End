package application;

import java.io.IOException;
import java.util.Properties;

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
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SceneSupportController {
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
	

    @FXML
    private TextField txtAsunto;
    

    @FXML
    private TextArea txtMensaje;
    
    private static String usuario;

    // Método estático para establecer el usuario
    public static void setUsuario(String usuario) {
        SceneSupportController.usuario = usuario;
        
    }

    // Método para enviar el formulario de contacto
    @FXML
    private void enviarFormulario(ActionEvent event) {
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
            mostrarAlerta("Formulario enviado correctamente.", AlertType.INFORMATION);

            

        } catch (MessagingException e) {
            // Mostrar una alerta si ocurre un error al enviar el correo
            mostrarAlerta("Error al enviar el formulario: " + e.getMessage(), AlertType.ERROR);
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
