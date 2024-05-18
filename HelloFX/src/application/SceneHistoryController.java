package application;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;

public class SceneHistoryController {
	
	private Stage stage;
	private Scene scene;
	private Parent root;

    private static String usuario;

    @FXML
    private TableView<HistorialItem> tableView;

    @FXML
    private TableColumn<HistorialItem, String> usuarioColumn;

    @FXML
    private TableColumn<HistorialItem, String> rutaColumn;

	@FXML
	private Pane PantallaCarga;
	@FXML
	private Label LabelProcesarImagenes;
    // Método para inicializar la pantalla
    @FXML
    public void initialize() {
    	try {
        	String filePath = Paths.get("").toAbsolutePath().toString() + "/src/firebase/bns-binarizadorniblack-sauvola-firebase-adminsdk-rrjcl-8dfb98c4cb.json";
        	FileInputStream serviceAccount = new FileInputStream(filePath);

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://bns-binarizadorniblack-sauvola-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(options);
            
            // Si se alcanza este punto, la inicialización fue exitosa
            System.out.println("Conexión a Firebase exitosa");
        } catch (Exception e) {
            e.printStackTrace();
        }
    	
        // Configurar las columnas de la tabla
        usuarioColumn.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        rutaColumn.setCellValueFactory(new PropertyValueFactory<>("ruta"));

        // Obtener una referencia a la ubicación en la base de datos donde están guardados los datos
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        // Leer los datos de Firebase
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            	// Limpiar la tabla
                tableView.getItems().clear();

            	 Task<Void> task = new Task<Void>() {
     	            @Override
     	            protected Void call() throws Exception {
     	            // Procesar las imágenes
     	            // Recorrer los datos y agregarlos a la tabla
     	                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
     	                    String user = snapshot.child("user").getValue(String.class);
     	                    String ruta = snapshot.child("ruta").getValue(String.class);

     	                    if(user == usuario) {
     	                    	 // Crear un nuevo objeto HistorialItem y agregarlo a la tabla
     	                        tableView.getItems().add(new HistorialItem(dataSnapshot.toString(), ruta));
     	                    }
     	                   
     	                }
     	                return null;
     	            }
     	        };
     	        
            	// Mostrar la pantalla de carga mientras se ejecuta el Task en segundo plano
    	        PantallaCarga.setVisible(true);
    	        new Thread(task).start();

    	        task.setOnSucceeded(e -> {
    	            // Ocultar la pantalla de carga cuando el Task ha terminado de ejecutarse
    	            PantallaCarga.setVisible(false);
    	    		// Mostrar mensaje de proceso finalizado
    	    		mostrarAlerta("Procesamiento de imágenes completado.", "Proceso Completado");
    	        });

    	        task.setOnFailed(e -> {
    	            // Ocultar la pantalla de carga y mostrar un mensaje de error si el Task falla
    	            PantallaCarga.setVisible(false);
    	        });
            	
                
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Manejar el error si la lectura de datos falla
                System.err.println("Error al leer datos de Firebase: " + databaseError.getMessage());
            }
        });
    }

    // Clase para representar un elemento del historial
    public static class HistorialItem {
        private final String ruta;
        private final String fecha;

        public HistorialItem(String ruta,String fecha) {
            this.ruta = ruta;
            this.fecha = fecha;
        }

        public String getRuta() {
            return ruta;
        }
        public String getFecha() {
            return fecha;
        }
    }
    
    // Método estático para establecer el usuario
    public static void setUsuario(String usuario) {
        SceneHistoryController.usuario = usuario;
        
    }
	// Navegacion entre pantallas
	public void switchPantallaPrincipal(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaPrincipal.fxml"));
		stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	private void mostrarAlerta(String mensaje, String title) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}
}
