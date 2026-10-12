import java.awt.*;
import java.io.File;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class interfazDES extends JFrame {
    private JTextField txtClave;

    public interfazDES() {
        setTitle("Cifrador DES (Data Encryption Standard)");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(250, 246, 248));

        // Títulos
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(40, 0, 20, 0));

        JLabel title = new JLabel("Cifrador DES", SwingConstants.CENTER);
        title.setFont(new Font("Montserrat", Font.BOLD, 32));
        title.setForeground(new Color(45, 52, 54));

        headerPanel.add(title);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        JPanel cardPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 35));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(223, 230, 233), 1, true),
                new EmptyBorder(10, 50, 10, 50)
        ));

        JLabel lblClave = new JLabel("Clave DES (Máx 8 caracteres):");
        lblClave.setFont(new Font("Montserrat", Font.BOLD, 20));
        lblClave.setForeground(new Color(45, 52, 54));

        txtClave = new JTextField(8);
        txtClave.setFont(new Font("Montserrat", Font.BOLD, 24));
        txtClave.setHorizontalAlignment(JTextField.CENTER);
        txtClave.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(162, 155, 254), 2, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        cardPanel.add(lblClave);
        cardPanel.add(txtClave);
        centerWrapper.add(cardPanel);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        // Botones
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 40));
        footerPanel.setOpaque(false);

        JButton b_Cifrar = crearBoton("Cifrar Archivo", new Color(108, 92, 231));
        JButton b_Descifrar = crearBoton("Descifrar Archivo", new Color(225, 112, 85));

        footerPanel.add(b_Cifrar);
        footerPanel.add(b_Descifrar);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);

        // Eventos
        b_Cifrar.addActionListener(e -> e_accion(true));
        b_Descifrar.addActionListener(e -> e_accion(false));
    }

    private JButton crearBoton(String texto, Color colorFondo) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Montserrat", Font.BOLD, 18));
        boton.setForeground(Color.WHITE);
        boton.setBackground(colorFondo);
        boton.setOpaque(true);
        boton.setContentAreaFilled(true);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        return boton;
    }

    private void e_accion(boolean x) {
        String claveStr = txtClave.getText();
        if(claveStr.isEmpty() || claveStr.length() > 8){
            JOptionPane.showMessageDialog(this, "Por favor, ingresa una clave de hasta 8 caracteres (DES usa 64 bits).", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser selectorArchivo = new JFileChooser();
        selectorArchivo.setDialogTitle(x ? "Selecciona el archivo para Cifrar" : "Selecciona el archivo para Descifrar");
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos TXT y BMP", "txt", "bmp");
        selectorArchivo.setFileFilter(filtro);

        if (selectorArchivo.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = selectorArchivo.getSelectedFile();

            String mensajeTrabajando = x ? "ESPERE UN MOMENTO ESTAMOS TRABAJANDO EN EL CIFRADO..." 
                                         : "ESPERE UN MOMENTO ESTAMOS TRABAJANDO EN EL DESCIFRADO...";
            String tituloTrabajando = x ? "Cifrando archivo" : "Descifrando archivo";

            JOptionPane panelEspera = new JOptionPane(mensajeTrabajando, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
            JDialog dialogoEspera = panelEspera.createDialog(this, tituloTrabajando);
            dialogoEspera.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            new Thread(() -> {
                try {
                    // Llamamos a nuestra nueva clase CifradorDES
                    CifradorDES.archivo(archivoSeleccionado.getAbsolutePath(), x, claveStr);

                    SwingUtilities.invokeLater(() -> {
                        dialogoEspera.dispose();
                        JOptionPane.showMessageDialog(this,
                                "¡Proceso terminado con éxito!\nRevisa la carpeta original.",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    });

                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        dialogoEspera.dispose();
                        JOptionPane.showMessageDialog(this,
                                "Ocurrió un error:\n" + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();
            dialogoEspera.setVisible(true);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            interfazDES ventana = new interfazDES();
            ventana.setVisible(true);
        });
    }
}