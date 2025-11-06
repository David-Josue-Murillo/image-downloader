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
        ImageDownloaderService downloader;
        FileManager fileManager = new FileManager("imagenes_descargadas");
        downloader = new ImageDownloaderService(fileManager);

        String url = JOptionPane.showInputDialog(null, "URL de la imagen a descargar:", "Descargador de Imágenes", JOptionPane.QUESTION_MESSAGE);
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor ingrese una URL válida.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DownloadResult result = downloader.download(url, "imagenes_descargadas");
            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(null, result.getMessage(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, result.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}