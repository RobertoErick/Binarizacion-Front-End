package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
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
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.FileInputStream;

public class PantallaProcesarController {
	private Stage stage;
	private Scene scene;
	private Parent root;
	
    private ResourceBundle bundle;
    private Locale locale;
    private static final String LANGUAGE_PREF_KEY = "language";
    private Preferences prefs;
    
	@FXML
	private Button btnCargarImagen;
	@FXML
	private Button btnSeleccionarDestino;
	@FXML
	private Button BtnProcesarImagen;
	@FXML
	private GridPane imageGridPane;
	@FXML
	private Label LabelDestino;
	@FXML
	private Pane PantallaCarga;
	@FXML
	private Label LabelProcesarImagenes;
	
    private static String usuario;
    private static String destino;

    // Método estático para establecer el usuario
    public static void setUsuario(String usuario) {
        PantallaProcesarController.usuario = usuario;
        
    }
    
    private boolean verificarCondiciones() {
        return !selectedImagePaths.isEmpty() && carpetaDestinoSeleccionada();
    }
    
    private void actualizarEstadoBotonProcesar() {
        BtnProcesarImagen.setDisable(!verificarCondiciones());
    }

    // Llamar a este método cuando se carguen imágenes o se seleccione un destino
    private void actualizarEstado() {
        actualizarEstadoBotonProcesar();
    }
    
    
    public class FirebaseManager {
        private static FirebaseApp firebaseAppInstance;

        public static synchronized FirebaseApp getFirebaseApp() {
            if (firebaseAppInstance == null) {
                try {
                    String filePath = Paths.get("").toAbsolutePath().toString() + "/src/firebase/bns-binarizadorniblack-sauvola-firebase-adminsdk-rrjcl-8dfb98c4cb.json";
                    FileInputStream serviceAccount = new FileInputStream(filePath);

                    FirebaseOptions options = new FirebaseOptions.Builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .setDatabaseUrl("https://bns-binarizadorniblack-sauvola-default-rtdb.firebaseio.com")
                            .build();

                    firebaseAppInstance = FirebaseApp.initializeApp(options);
                    
                    // Si se alcanza este punto, la inicialización fue exitosa
                    System.out.println("Conexión a Firebase exitosa");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return firebaseAppInstance;
        }
    }
    
    @FXML
	public void initialize() {
    	prefs = Preferences.userNodeForPackage(PantallaProcesarController.class);
        String language = prefs.get(LANGUAGE_PREF_KEY, "en"); // Valor por defecto es inglés
        setLanguage(language);
    	
    	 FirebaseApp firebaseApp = FirebaseManager.getFirebaseApp();
    	    
     // Configurar el estado inicial del botón de procesar
        actualizarEstado();
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

    	btnCargarImagen.setText(bundle.getString("process.upload"));
    	btnSeleccionarDestino.setText(bundle.getString("process.folder"));
    	BtnProcesarImagen.setText(bundle.getString("process.process"));
    	LabelProcesarImagenes.setText(bundle.getString("process.carga"));
    	LabelDestino.setText(bundle.getString("process.dest"));
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

	// Objetos y acciones usados en la PantallaProcesar

	List<String> imagePaths = new ArrayList<>(); // Lista para almacenar las rutas de las imágenes seleccionadas
	private List<String> selectedImagePaths = new ArrayList<>();

	// La ruta de la imagen se coloco por fuera de la funcion para que pudiera
	// usarse en otras
	public String rutaImagen;

	// Accion de Seleccionar imagen de la PantallaProcesar
	public void SeleccionarArchivo(ActionEvent event) {
		SeleccionarArchivo archivoSeleccionado = new SeleccionarArchivo();
		selectedImagePaths = archivoSeleccionado.selectFiles(imageGridPane);
		
		actualizarEstado();
	}

	// Solo se llama cuando se necesita seleccionar la imagen
	public class SeleccionarArchivo {
		public List<String> selectFiles(GridPane gridPane) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle(bundle.getString("process.msj2"));
			fileChooser.getExtensionFilters().addAll(new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

			List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
			List<String> imagePaths = new ArrayList<>();

			if (selectedFiles != null) {
				int column = 0;
				int row = 0;

				for (File file : selectedFiles) {
					// Crear ImageView con botón de eliminación y obtener el StackPane resultante
					StackPane imageStackPane = createImageViewWithDeleteButton(file, gridPane);

					// Agregar el StackPane al GridPane en la posición actual
					GridPane.setConstraints(imageStackPane, column, row);
					gridPane.getChildren().add(imageStackPane);

					// Actualizar posición en el GridPane
					column++;
					if (column > 3) {
						column = 0;
						row++;
					}

					// Agregar la ruta de la imagen a la lista de rutas
					imagePaths.add(file.getAbsolutePath());
				}
				//Se reorganiza el panel cada que se selecciona una imagen
				rearrangeGridPane(gridPane);
			}

			return imagePaths;
		}

		private ImageViewWithDeleteButton createImageViewWithDeleteButton(File file, GridPane gridPane) {
			ImageView imageView = new ImageView();
			imageView.setFitWidth(80);
			imageView.setFitHeight(80);
			imageView.setPreserveRatio(true);

			try {
				BufferedImage bufferedImage = ImageIO.read(file);
				WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
				imageView.setImage(image);
			} catch (IOException e) {
				e.printStackTrace();
			}

			String imagePath = file.getAbsolutePath(); // Obtener la ruta de la imagen

			Button deleteButton = new Button("X");
			deleteButton.setStyle(
				    "-fx-background-color: white; " +
				    "-fx-text-fill: black; " +         
				    "-fx-font-weight: bold; " +
				    "-fx-font-size: 15px; " +
				    "-fx-background-radius: 100%; " +  
				    "-fx-cursor: hand;"                
				);
			deleteButton.setOnAction(event -> {
				StackPane stackPane = (StackPane) imageView.getParent();

				if (stackPane != null) {
					gridPane.getChildren().remove(stackPane);

					// Eliminar la ruta de la imagen de selectedImagePaths
					ImageViewWithDeleteButton imageViewWithDeleteButton = (ImageViewWithDeleteButton) stackPane;
					String removedImagePath = imageViewWithDeleteButton.getImagePath();
					selectedImagePaths.remove(removedImagePath);

					// Reorganizar las imágenes restantes en el GridPane
					rearrangeGridPane(gridPane);
					
					actualizarEstado();
				}
			});

			StackPane.setAlignment(deleteButton, Pos.TOP_RIGHT);

			StackPane stackPane = new ImageViewWithDeleteButton(imageView, imagePath);
			stackPane.getChildren().add(deleteButton);

			return (ImageViewWithDeleteButton) stackPane;
		}

		private void rearrangeGridPane(GridPane gridPane) {
			// Limpiar el GridPane y volver a agregar las imágenes restantes
			List<Node> children = new ArrayList<>(gridPane.getChildren());
			gridPane.getChildren().clear();

			int column = 0;
			int row = 0;

			for (Node node : children) {
				GridPane.setConstraints(node, column, row);
				gridPane.getChildren().add(node);

				column++;
				if (column > 3) {
					column = 0;
					row++;
				}
			}
		}

		public class ImageViewWithDeleteButton extends StackPane {
			private String imagePath;

			public ImageViewWithDeleteButton(ImageView imageView, String imagePath) {
				super(imageView);
				this.imagePath = imagePath;
			}

			public String getImagePath() {
				return imagePath;
			}
		}

	}

	// Estas declaraciones se van a usar en el destino de la imagen y procesar
	// imagenes, por eso se declara afuera
	Mat gray;
	List<Mat> imageNiblack = new ArrayList<>();
	List<Mat> imageSauvola = new ArrayList<>();
	List<Double> PSNRNiblack = new ArrayList<>();
	List<Double> PSNRdSauvola = new ArrayList<>();
	List<Double> jaccardNiblack = new ArrayList<>();
	List<Double> jaccardSauvola = new ArrayList<>();
	List<Integer> UmbralSauvola = new ArrayList<>();
	List<Integer> UmbralNiblack = new ArrayList<>();

	// Método para el botón que inicia el procesamiento de imágenes
	@FXML
	private void handleProcesarButton(ActionEvent event) {
	    // Verificar si se ha seleccionado la carpeta destino y al menos una imagen
	    if (selectedImagePaths.isEmpty() || !carpetaDestinoSeleccionada()) {
	        // Mostrar un mensaje de alerta al usuario
	        mostrarAlerta(bundle.getString("process.alert1"), bundle.getString("process.titlealert1"));
	    } else {
	        Task<Void> task = new Task<Void>() {
	            @Override
	            protected Void call() throws Exception {
	                // Procesar las imágenes
	                ProcesarImagenes(selectedImagePaths);
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
	    		mostrarAlerta(bundle.getString("process.alert2"), bundle.getString("process.titlealert2"));
	        });

	        task.setOnFailed(e -> {
	            // Ocultar la pantalla de carga y mostrar un mensaje de error si el Task falla
	            PantallaCarga.setVisible(false);
	        });
	    }
	}

	private void mostrarAlerta(String mensaje, String title) {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(mensaje);
		alert.showAndWait();
	}

	String rutaCarpetaDestino = "";

	private boolean carpetaDestinoSeleccionada() {
		return !rutaCarpetaDestino.isEmpty(); // Verificar si la ruta de la carpeta destino no está vacía
	}

	// Accion de seleccionar Destino de la PantallaProcesar
	public void SeleccionarCarpeta(ActionEvent event) {
	    DirectoryChooser directoryChooser = new DirectoryChooser();
	    directoryChooser.setTitle(bundle.getString("process.msj1"));

	    File selectedDirectory = directoryChooser.showDialog(null);

	    if (selectedDirectory != null) {
	        rutaCarpetaDestino = selectedDirectory.getAbsolutePath();

	        // Actualizar la interfaz o mostrar la ruta seleccionada
	        System.out.println("Carpeta destino seleccionada: " + rutaCarpetaDestino);
	        LabelDestino.setText(""+rutaCarpetaDestino);
	    } else {
	        // El usuario canceló la selección, puedes mostrar un mensaje o realizar otra acción
	        System.out.println("Selección de carpeta cancelada.");
	    }
	    
	    actualizarEstado();
	}


	public void ProcesarImagenes(List<String> imagePathsevent) {
		// Cargar la biblioteca OpenCV
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	
		for (String path : imagePathsevent) {
			// Obtener el nombre de archivo de la imagen original sin la extensión
			String originalFileName = new File(path).getName();
			String fileNameWithoutExtension = originalFileName.substring(0, originalFileName.lastIndexOf('.'));

			// Crear la estructura de directorios si no existe
			String destinationFolder = rutaCarpetaDestino + "\\" + fileNameWithoutExtension;
			destino = destinationFolder;
			File directory = new File(destinationFolder);
			if (!directory.exists()) {
				directory.mkdirs(); // Crear la estructura de directorios completa
			}
			
	        guardarEnFirebase(rutaCarpetaDestino);


			// Procesar la imagen utilizando la ruta `path`
			// Leer la imagen seleccionada
			Mat image = Imgcodecs.imread(path);

			// Convertir a escala de grises
			gray = new Mat(image.rows(), image.cols(), CvType.CV_8U);
			for (int row = 0; row < image.rows(); row++) {
				for (int col = 0; col < image.cols(); col++) {
					double[] pixel = image.get(row, col);
					int grayValue = (int) (0.299 * pixel[2] + 0.587 * pixel[1] + 0.114 * pixel[0]);
					gray.put(row, col, grayValue);
				}
			}

			// ---------------------------------------------------------------------------------------------------------

			// Calcular el umbral de Niblack
			/*
			 * double kniblack = 2.0; while (kniblack < 0.0 || kniblack > 1.0) { kniblack =
			 * Double.parseDouble(JOptionPane.
			 * showInputDialog("Valor de k para calculo de umbral de niblack")); if
			 * (kniblack < 0.0 || kniblack > 1.0) { JOptionPane.showMessageDialog(null,
			 * "El valor de k debe estar entre 0.0 y 1.0"); } }
			 */
			// Calcular umbral de Niblack manualmente
			double kniblack = 0.0;

			imageNiblack = new ArrayList<>();

			for (int w = 0; w < 10; w++) {
				kniblack += 0.10;
				Mat thresholdNiblack = new Mat(gray.size(), CvType.CV_8UC1);

				for (int row = 0; row < gray.rows(); row++) {
					for (int col = 0; col < gray.cols(); col++) {
						// Calcular la media y la desviacion estandar de la ventana
						int windowSize = 15;
						int windowOffset = windowSize / 2;
						double sum = 0;
						double sumSquared = 0;
						for (int i = row - windowOffset; i <= row + windowOffset; i++) {
							for (int j = col - windowOffset; j <= col + windowOffset; j++) {
								if (i >= 0 && i < gray.rows() && j >= 0 && j < gray.cols()) {
									double pixelValue = gray.get(i, j)[0];
									sum += pixelValue;
									sumSquared += pixelValue * pixelValue;
								}
							}
						}
						double windowSizeDouble = windowSize * windowSize;
						double mean = sum / windowSizeDouble;
						double stdDev = Math.sqrt((sumSquared / windowSizeDouble) - (mean * mean));

						/////
						/////
						/////
						/// FORMULA NIBLACK
						int thresholdValue = (int) (mean + (kniblack * stdDev));
						double pixelValue = gray.get(row, col)[0];
						if (pixelValue > thresholdValue) {
							thresholdNiblack.put(row, col, 255);
						} else {
							thresholdNiblack.put(row, col, 0);
						}
					}
				}
				System.out
						.println("K=" + kniblack + "\nUmbral Niblack: " + (int) Core.mean(thresholdNiblack).val[0]);

				UmbralNiblack.add((int) Core.mean(thresholdNiblack).val[0]);

				// Calcular el MSE (error cuadr�tico medio)
				/*
				 * El MSE se calcula como el promedio de los cuadrados de las diferencias entre
				 * los valores de p�xeles correspondientes de las dos im�genes.
				 */
				double mse = 0;
				for (int i = 0; i < gray.rows(); i++) {
					for (int j = 0; j < gray.cols(); j++) {
						double[] pixelOriginal = gray.get(i, j);
						double[] pixelProcesada = thresholdNiblack.get(i, j);
						double diff = pixelOriginal[0] - pixelProcesada[0];
						mse += diff * diff;
					}
				}
				mse /= gray.rows() * gray.cols();

				/*
				 * PSNR (Relaci�n Se�al-Ruido de Pico, por sus siglas en ingl�s) es una medida
				 * de la calidad de la reconstrucci�n de im�genes que han sido comprimidas. Se
				 * calcula comparando la imagen original con la imagen reconstruida y se mide la
				 * cantidad de ruido introducido durante el proceso de compresi�n. Un valor m�s
				 * alto de PSNR indica una mejor calidad de la imagen reconstruida.
				 * 
				 * PSNR se calcula utilizando el error cuadr�tico medio (MSE, por sus siglas en
				 * ingl�s) entre la imagen original y la imagen reconstruida. Una vez que se ha
				 * calculado el MSE, se puede calcular el PSNR (en dB) utilizando la f�rmula:
				 * PSNR = 20 * log10(MAX / sqrt(MSE)), donde MAX es el valor m�ximo posible del
				 * p�xel en la imagen (255) y sqrt(MSE) es la ra�z cuadrada del MSE 1.
				 */

				// Calcular el PSNR - (Entre mas alto es este valor mejor fue la reconstrucci�n
				// de la imagen)
				double psnr_Niblack = 20 * Math.log10(255.0 / Math.sqrt(mse));

				PSNRNiblack.add(psnr_Niblack);

				System.out.println("PSNR_Niblack: " + psnr_Niblack);
				imageNiblack.add(thresholdNiblack);
			}

			// ---------------------------------------------------------------------------------------------------------
			double ksauvola = 0.0;
			imageSauvola = new ArrayList<>();

			for (int w = 0; w < 10; w++) {
				ksauvola += 0.10;
				// Aplicar tecnica Sauvola manualmente
				Mat sauvola = new Mat(gray.rows(), gray.cols(), CvType.CV_8UC1);
				int windowSize = 25;
				// Mismo valor de K utilizado en el articulo

				while (ksauvola < 0.0 || ksauvola > 1.0) {
					ksauvola = Double
							.parseDouble(JOptionPane.showInputDialog(bundle.getString("process.joption1")));
					if (kniblack < 0.0 || kniblack > 1.0) {
						JOptionPane.showMessageDialog(null, bundle.getString("process.joption2"));
					}
				}
				for (int row = 0; row < gray.rows(); row++) {
					for (int col = 0; col < gray.cols(); col++) {
						double[] pixel = gray.get(row, col);
						double sum = 0;
						double sumSquared = 0;
						int count = 0;
						for (int i = -windowSize / 2; i <= windowSize / 2; i++) {
							for (int j = -windowSize / 2; j <= windowSize / 2; j++) {
								if (row + i < 0 || row + i >= gray.rows() || col + j < 0 || col + j >= gray.cols()) {
									continue;
								}
								double[] neighbor = gray.get(row + i, col + j);
								sum += neighbor[0];
								sumSquared += Math.pow(neighbor[0], 2);
								count++;
							}
						}
						double mean = sum / count;
						double variance = (sumSquared - Math.pow(sum, 2) / count) / count;

						//// FORMULA SAUVOLA
						// double thresholdValue = mean * (1 - k * (1- Math.sqrt(variance / 128)));
						double desv = Math.sqrt(variance);
						int thresholdValue = (int) (mean * (1 - ksauvola * (1 - (desv / 128))));
						if (pixel[0] > thresholdValue) {
							sauvola.put(row, col, 255);
						} else {
							sauvola.put(row, col, 0);
						}
					}
				}
				System.out.println("K=" + ksauvola + "\nUmbral de Sauvola: " + (int) Core.mean(sauvola).val[0]);

				UmbralSauvola.add((int) Core.mean(sauvola).val[0]);

				double mse = 0;
				// Calcular el MSE
				mse = 0;
				for (int i = 0; i < gray.rows(); i++) {
					for (int j = 0; j < gray.cols(); j++) {
						double[] pixelOriginal = gray.get(i, j);
						double[] pixelProcesada = sauvola.get(i, j);
						double diff = pixelOriginal[0] - pixelProcesada[0];
						mse += diff * diff;
					}
				}
				mse /= gray.rows() * gray.cols();

				// Calcular el PSNR
				double psnr_Sauvola = 20 * Math.log10(255.0 / Math.sqrt(mse));

				PSNRdSauvola.add(psnr_Sauvola);

				System.out.println("PSNR_Sauvola: " + psnr_Sauvola);

				imageSauvola.add(sauvola);
			}

			Imgcodecs.imwrite(rutaCarpetaDestino + "/" + fileNameWithoutExtension + "/ImagenEnGrises.jpg", gray);

			int posicion = 0;
			for (Mat imagen : imageNiblack) {
				posicion++;
				Imgcodecs.imwrite(
						rutaCarpetaDestino + "/" + fileNameWithoutExtension + "/ResultadoNiblack" + posicion + ".jpg",
						imagen);
			}
			posicion = 0;
			for (Mat imagen : imageSauvola) {
				posicion++;
				Imgcodecs.imwrite(
						rutaCarpetaDestino + "/" + fileNameWithoutExtension + "/ResultadoSauvola" + posicion + ".jpg",
						imagen);
			}

			for (int w = 0; w < 10; w++) {
				double jaccardS = jaccard(gray, imageSauvola.get(w));
				double jaccardN = jaccard(gray, imageNiblack.get(w));
				jaccardSauvola.add(jaccardS);
				jaccardNiblack.add(jaccardN);
			}

			List<String> HISTSA = new ArrayList<>();
			List<String> HISTNI = new ArrayList<>();

			for (int w = 0; w < 10; w++) {
				int[] histogramSauvola = new int[256];
				for (int i = 0; i < imageSauvola.get(w).rows(); i++) {
					for (int j = 0; j < imageSauvola.get(w).cols(); j++) {
						int value = (int) imageSauvola.get(w).get(i, j)[0];
						histogramSauvola[value]++;
					}
				}

				int[] histogramNiblack = new int[256];
				for (int i = 0; i < imageNiblack.get(w).rows(); i++) {
					for (int j = 0; j < imageNiblack.get(w).cols(); j++) {
						int value = (int) imageNiblack.get(w).get(i, j)[0];
						histogramNiblack[value]++;
					}
				}

				String valores = histogramSauvola[0] + "," + histogramSauvola[255];

				HISTNI.add(valores);
				valores = histogramNiblack[0] + "," + histogramNiblack[255];
				HISTSA.add(valores);
			}

			// Histograma imagen en grises
			int[] histogramGray = new int[256];
			for (int i = 0; i < gray.rows(); i++) {
				for (int j = 0; j < gray.cols(); j++) {
					int value = (int) gray.get(i, j)[0];
					histogramGray[value]++;
				}
			}

			// Conjunto de resultados
			try {
				FileWriter writer = new FileWriter(
						rutaCarpetaDestino + "/" + fileNameWithoutExtension + "/Histograma_Jaccard_PSNR.csv");
				writer.write("0,255,Jaccard,PSNR,Umbral,K");

				double contadorK = 0.0;
				for (int w = 0; w < 10; w++) {
					contadorK += 0.1;

					writer.write("\n");
					writer.write(HISTNI.get(w) + "," + jaccardSauvola.get(w) + "," + PSNRdSauvola.get(w) + ","
							+ UmbralSauvola.get(w) + "," + contadorK + ",Sauvola");
				}

				writer.write("\n");

				contadorK = 0.0;
				for (int w = 0; w < 10; w++) {
					contadorK += 0.1;

					writer.write("\n");
					writer.write(HISTSA.get(w) + "," + jaccardNiblack.get(w) + "," + PSNRNiblack.get(w) + ","
							+ UmbralNiblack.get(w) + "," + contadorK + ",Niblack");
				}
				writer.write("\n");
				writer.write("\n");
				for (int w = 0; w < 256; w++) {
					writer.write(w + ",");

				}
				writer.write("\n");
				for (int w = 0; w < 256; w++) {
					writer.write(String.valueOf(histogramGray[w]) + ",");
				}

				writer.write("ImagenEnGrises");
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Matriz Escala de grises
			try {
				FileWriter writer = new FileWriter(
						rutaCarpetaDestino + "/" + fileNameWithoutExtension + "/matrizGrises.csv");

				for (int i = 0; i < gray.rows(); i++) {
					for (int j = 0; j < gray.cols(); j++) {
						double[] value = gray.get(i, j);
						writer.write(String.valueOf(value[0]));
						if (j < gray.cols() - 1) {
							writer.write(",");
						}
					}
					writer.write("\n");
				}

				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (int w = 0; w < 10; w++) {
				// Guardar matriz de imagen Sauvola
				try {
					FileWriter writer = new FileWriter(
							rutaCarpetaDestino + "/" + fileNameWithoutExtension + "/matrizImagenSauvola" + w + ".csv");

					for (int i = 0; i < imageSauvola.get(w).rows(); i++) {
						for (int j = 0; j < imageSauvola.get(w).cols(); j++) {
							double[] value = imageSauvola.get(w).get(i, j);
							writer.write(String.valueOf(value[0]));
							if (j < imageSauvola.get(w).cols() - 1) {
								writer.write(",");
							}
						}
						writer.write("\n");
					}

					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

				// Guardar matriz de imagen Niblack
				try {
					FileWriter writer = new FileWriter(
							rutaCarpetaDestino + "/" + fileNameWithoutExtension + "/matrizImagenNiblack" + w + ".csv");

					for (int i = 0; i < imageNiblack.get(w).rows(); i++) {
						for (int j = 0; j < imageNiblack.get(w).cols(); j++) {
							double[] value = imageNiblack.get(w).get(i, j);
							writer.write(String.valueOf(value[0]));
							if (j < imageNiblack.get(w).cols() - 1) {
								writer.write(",");
							}
						}
						writer.write("\n");
					}

					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}


	private double jaccard(Mat img1, Mat img2) {
		int intersection = 0;
		int union = 0;

		for (int row = 0; row < img1.rows(); row++) {
			for (int col = 0; col < img1.cols(); col++) {
				double[] pixel1 = img1.get(row, col);
				double[] pixel2 = img2.get(row, col);

				if (pixel1[0] == 255 && pixel2[0] == 255) {
					intersection++;
					union++;
				} else if (pixel1[0] == 255 || pixel2[0] == 255) {
					union++;
				}
			}
		}

		double div = (double) intersection / (double) union;

		return div;
	}
	private void guardarEnFirebase(String rutaCarpetaDestino) {
	    // Obtener una referencia a la base de datos
	    FirebaseDatabase database = FirebaseDatabase.getInstance();
	    
	    // Obtener la fecha actual
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String fechaActual = sdf.format(new Date());
	    
	    // Obtener una referencia a la ubicación en la base de datos donde quieres guardar los datos
	    DatabaseReference ref = database.getReference(fechaActual);
	    
	   
	    
	    // Guardar los datos en Firebase
	    ref.child("user").setValueAsync(usuario);
	    ref.child("ruta").setValueAsync(destino);

	    
	    System.out.println("Datos guardados en Firebase.");
	}
	// Solo se llama cuando se necesita seleccionar la carpeta de destino
	public class CarpetaDestino {
		public String selectCarpet() {
			// Seleccionar la carpeta destino para guardar la imagen transformada
			String rutaCarpetaDestino;
			// Crear un nuevo objeto FileDialog
			FileDialog carpetaDestino = new FileDialog((Frame) null, bundle.getString("process.msj1"), FileDialog.LOAD);
			carpetaDestino.setMode(FileDialog.SAVE);
			carpetaDestino.setVisible(true);
			rutaCarpetaDestino = carpetaDestino.getDirectory();
			carpetaDestino.dispose();
			return rutaCarpetaDestino;
		}
	}
}