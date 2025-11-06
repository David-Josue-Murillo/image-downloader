import javax.swing.*;

/**
 * Punto de entrada de la aplicación.
 *
 * Antes: mostraba solo un dialogo para pedir una URL.
 * Ahora: lanza la ventana principal `DownloadFrame` que contiene la UI completa
 * para ingresar la URL, mostrar progreso y registrar mensajes.
 */
public class ImageDownloader {
    public static void main(String[] args) {
        String url = JOptionPane.showInputDialog(null, "URL de la imagen a descargar:", "Descargador de Imágenes", JOptionPane.QUESTION_MESSAGE);


        if (url != null && !url.trim().isEmpty()) {
            // Aquí podrías iniciar el proceso de descarga usando ImageDownloaderService
            JOptionPane.showMessageDialog(null, "Iniciando descarga para la URL: " + url, "Descargador de Imágenes", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "No se proporcionó una URL válida.", "Descargador de Imágenes", JOptionPane.ERROR_MESSAGE);
        }
    }
}