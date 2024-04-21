package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.FileChooser;
import java.awt.image.BufferedImage;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;


public class SceneController {
	private Stage stage;
	private Scene scene;
	private Parent root;
	
	//Navegacion entre pantallas
	public void switchPantallaPrincipal(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaPrincipal.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
	
	public void switchPantallaProcesar(ActionEvent event) throws IOException {
		Parent root = FXMLLoader.load(getClass().getResource("PantallaProcesar.fxml"));
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}
    
	//Objetos y acciones usados en la PantallaPrincipal
    @FXML
	private Label LabelUsuario;
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
	@FXML
	private GridPane imageGridPane;
	@FXML
    private ScrollPane imageScrollPane;
	
	//Accion de Cerrar Sesion de la PantallaPrincipal
	public void CerrarSesion(ActionEvent event) {
		// TODO Autogenerated
		LabelUsuario.setText("");
	}
	
	//Accion de salir del programa de la PantallaPrincipal
	public void Salir(ActionEvent event) {
		// TODO Autogenerated
		Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
	}
	
	//Objetos y acciones usados en la PantallaProcesar
	
	//La ruta de la imagen se coloco por fuera de la funcion para que pudiera usarse en otras
	public String rutaImagen;
	//Accion de Seleccionar imagen de la PantallaProcesar
	public void SeleccionarArchivo(ActionEvent event) {
		SeleccionarArchivo archivoSeleccionado = new SeleccionarArchivo();
        archivoSeleccionado.selectFiles(imageGridPane);
	}
	
	//Solo se llama cuando se necesita seleccionar la imagen
		public class SeleccionarArchivo {
			public void selectFiles(GridPane gridPane) {
		        FileChooser fileChooser = new FileChooser();
		        fileChooser.setTitle("Seleccionar imágenes");
		        fileChooser.getExtensionFilters().addAll(
		                new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
		        // Mostrar el diálogo para que el usuario seleccione múltiples archivos
		        List<File> selectedFiles = fileChooser.showOpenMultipleDialog(null);
		        if (selectedFiles != null) {
		            int column = 0;
		            int row = 0;
		            // Recorrer los archivos seleccionados y cargar las imágenes
		            for (File file : selectedFiles) {
		                ImageViewWithDeleteButton imageViewWithDeleteButton = createImageViewWithDeleteButton(file, gridPane);
		                GridPane.setConstraints(imageViewWithDeleteButton, column, row);
		                gridPane.getChildren().add(imageViewWithDeleteButton);
		                column++;
		                if (column > 3) {
		                    column = 0;
		                    row++;
		                }
		            }
		        }
		    }

			private ImageViewWithDeleteButton createImageViewWithDeleteButton(File file, GridPane gridPane) {
			    ImageView imageView = new ImageView();
			    imageView.setFitWidth(80); // Ancho deseado para las imágenes
			    imageView.setFitHeight(80); // Altura deseada para las imágenes
			    imageView.setPreserveRatio(true);
			    
			    try {
			        BufferedImage bufferedImage = ImageIO.read(file);
			        WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
			        imageView.setImage(image);
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			    
			    // Crear un rectángulo blanco para el borde
			    Rectangle border = new Rectangle(85, 85);
			    border.setFill(Color.TRANSPARENT);
			    border.setStroke(Color.WHITE);
			    
			    // Envolver la ImageView y el rectángulo dentro de un StackPane
			    StackPane imagePane = new StackPane();
			    imagePane.getChildren().addAll(imageView, border);
			    
			    // Crear un botón para eliminar la imagen
			    Button deleteButton = new Button("X");
			    deleteButton.setOnAction(event -> {
			        // Obtener el StackPane padre del botón y eliminarlo del GridPane
			        StackPane parentStackPane = (StackPane) deleteButton.getParent();
			        gridPane.getChildren().remove(parentStackPane);
			    });
			    deleteButton.setStyle("-fx-background-color: black; -fx-text-fill: white; -fx-border-radius: 10px; -fx-border-color: white;");
			    
			    // Cambiar el cursor cuando se pasa sobre el botón
			    deleteButton.setCursor(Cursor.HAND);
			    
			    // Posicionar el botón en la esquina superior derecha de la imagen
			    StackPane.setAlignment(deleteButton, javafx.geometry.Pos.TOP_RIGHT);
			    
			    // Envolver la imagen y el botón dentro de un StackPane
			    StackPane imageViewWithDeleteButton = new StackPane();
			    imageViewWithDeleteButton.getChildren().addAll(imagePane, deleteButton);
			    
			    return new ImageViewWithDeleteButton(imageViewWithDeleteButton);
			}

		    
		    public class ImageViewWithDeleteButton extends BorderPane {
		        public ImageViewWithDeleteButton(StackPane content) {
		            super(content);
		        }
		    }
		}
	
	//Estas declaraciones se van a usar en el destino de la imagen y procesar imagenes, por eso se declara afuera
	Mat gray;
	List<Mat> imageNiblack = new ArrayList<>();
	List<Mat> imageSauvola = new ArrayList<>();
	List<Double> PSNRNiblack = new ArrayList<>();
	List<Double> PSNRdSauvola = new ArrayList<>();
	List<Double> jaccardNiblack = new ArrayList<>();
	List<Double> jaccardSauvola = new ArrayList<>();
	List<Integer> UmbralSauvola = new ArrayList<>();
	List<Integer> UmbralNiblack = new ArrayList<>();
	public void ProcesarImagenes(ActionEvent event) {
		// Cargar la biblioteca OpenCV
				System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

				// Leer la imagen seleccionada
				Mat image = Imgcodecs.imread(imageGridPane);

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
					System.out.println("K=" + kniblack + "\nUmbral de Niblack: " + (int) Core.mean(thresholdNiblack).val[0]);

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
								.parseDouble(JOptionPane.showInputDialog("Valor de k para calculo de umbral de sauvola"));
						if (kniblack < 0.0 || kniblack > 1.0) {
							JOptionPane.showMessageDialog(null, "El valor de k debe estar entre 0.0 y 1.0");
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
							// double thresholdValue = mean * (1 + k * (Math.sqrt(variance / 128) - 1));
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
	}
	
	//Accion de seleccionar Destino de la PantallaProcesar
	public void SeleccionarCarpeta(ActionEvent event) {
		CarpetaDestino carpetaDestino = new CarpetaDestino();

		String rutaCarpetaDestino = carpetaDestino.selectCarpet();

		Imgcodecs.imwrite(rutaCarpetaDestino + "/ImagenEnGrises.jpg", gray);

		int posicion = 0;
		for (Mat imagen : imageNiblack) {
			posicion++;
			Imgcodecs.imwrite(rutaCarpetaDestino + "/ResultadoNiblack" + posicion + ".jpg", imagen);
		}
		posicion = 0;
		for (Mat imagen : imageSauvola) {
			posicion++;
			Imgcodecs.imwrite(rutaCarpetaDestino + "/ResultadoSauvola" + posicion + ".jpg", imagen);
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
			FileWriter writer = new FileWriter(rutaCarpetaDestino + "/Histograma_Jaccard_PSNR.csv");
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
			FileWriter writer = new FileWriter(rutaCarpetaDestino + "/matrizGrises.csv");

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
				FileWriter writer = new FileWriter(rutaCarpetaDestino + "/matrizImagenSauvola" + w + ".csv");

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
				FileWriter writer = new FileWriter(rutaCarpetaDestino + "/matrizImagenNiblack" + w + ".csv");

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
	
	private double jaccard(Mat gray2, Mat mat) {
		// TODO Auto-generated method stub
		return 0;
	}

	//Solo se llama cuando se necesita seleccionar la carpeta de destino 
	public class CarpetaDestino {
		public String selectCarpet() {
			// Seleccionar la carpeta destino para guardar la imagen transformada
					String rutaCarpetaDestino;
					// Crear un nuevo objeto FileDialog
					FileDialog carpetaDestino = new FileDialog((Frame) null, "Seleccionar carpeta destino", FileDialog.LOAD);
					carpetaDestino.setMode(FileDialog.SAVE);
					carpetaDestino.setVisible(true);
					rutaCarpetaDestino = carpetaDestino.getDirectory();
					carpetaDestino.dispose();
					return rutaCarpetaDestino;
		}
	}
}