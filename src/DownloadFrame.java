
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadFrame extends JFrame {
    private final JTextField urlField;
    private final JTextArea logArea;
    private final JButton downloadButton;
    private final JProgressBar progressBar;

    private final ImageDownloaderService downloader;
    private final ExecutorService executor;

    public DownloadFrame() {
        super("Descargador de Imágenes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(550, 380);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Crear componentes
        urlField = new JTextField();
        downloadButton = new JButton("Descargar");
        logArea = new JTextArea();
        logArea.setEditable(false);
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);

        // Panel superior (entrada URL)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel(" URL de la imagen: "), BorderLayout.WEST);
        topPanel.add(urlField, BorderLayout.CENTER);
        topPanel.add(downloadButton, BorderLayout.EAST);

        // Panel inferior (progreso)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(progressBar, BorderLayout.CENTER);

        // Área de registro
        JScrollPane scrollPane = new JScrollPane(logArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Configuración de dependencias (principio D)
        FileManager fileManager = new FileManager("imagenes_descargadas");
        this.downloader = new ImageDownloaderService(fileManager);
        this.executor = Executors.newSingleThreadExecutor();

        // Acción del botón
        downloadButton.addActionListener(e -> iniciarDescarga());
    }

    private void iniciarDescarga() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese una URL válida.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        logArea.setText("Iniciando descarga...\n");
        progressBar.setValue(0);
        downloadButton.setEnabled(false);

        executor.submit(() -> {
            try {
                for (int i = 0; i <= 100; i += 15) { // Simula progreso visual
                    int finalI = i;
                    SwingUtilities.invokeLater(() -> progressBar.setValue(finalI));
                    Thread.sleep(150);
                }

                DownloadResult result = downloader.download(url, "imagenes_descargadas");

                SwingUtilities.invokeLater(() -> {
                    logArea.append(result.getMessage() + "\n");
                    if (result.isSuccess()) {
                        logArea.append("Archivo guardado en: " + result.getFilePath() + "\n");
                        progressBar.setValue(100);
                    } else {
                        progressBar.setValue(0);
                    }
                    downloadButton.setEnabled(true);
                });

            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        });
    }
}
