package application;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.event.ActionEvent;

import javafx.scene.control.Label;
import javafx.stage.Stage;

public class EscenaPrincipalController {
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
	@FXML
	private Button btnProcesarImagenes;
	@FXML
	private Label LabelUsuario;

	// Event Listener on Button[#btnProcesarImagenes].onAction
	@FXML
	public void CerrarSesion(ActionEvent event) {
		// TODO Autogenerated
		LabelUsuario.setText("");
	}
	
	public void Salir(ActionEvent event) {
		// TODO Autogenerated
		Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
	}
}
