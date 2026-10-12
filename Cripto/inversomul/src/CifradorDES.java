import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Arrays;

public class CifradorDES {
    
    public static void archivo(String ruta, boolean cifrar, String claveStr) throws Exception {
        File archivo_o = new File(ruta);
        String ruta_d = nombre(ruta, cifrar);
        boolean bmp = ruta.toLowerCase().endsWith(".bmp");
        int t_cabecera = 54;

        // DES requiere una clave de exactamente 8 bytes (64 bits)
        byte[] keyBytes = Arrays.copyOf(claveStr.getBytes("UTF-8"), 8);
        SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
        
        // Usamos DES en modo ECB (Electronic Codebook)
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        if (cifrar) {
            cipher.init(Cipher.ENCRYPT_MODE, key);
        } else {
            cipher.init(Cipher.DECRYPT_MODE, key);
        }

        try (FileInputStream fis = new FileInputStream(archivo_o);
             FileOutputStream fos = new FileOutputStream(ruta_d)) {
            
            // Si es un BMP, copiamos la cabecera intacta para no corromper la imagen
            if (bmp) {
                byte[] cabecera = new byte[t_cabecera];
                int leidos = fis.read(cabecera);
                if (leidos > 0) {
                    fos.write(cabecera, 0, leidos);
                }
            }

            // Procesamos el resto del archivo (o el archivo completo si es TXT) en bloques
            byte[] buffer = new byte[8192];
            int leidos;
            while ((leidos = fis.read(buffer)) != -1) {
                byte[] output = cipher.update(buffer, 0, leidos);
                if (output != null) {
                    fos.write(output);
                }
            }
            
            // Finalizamos el cifrado/descifrado (aplica el padding)
            byte[] outputBytes = cipher.doFinal();
            if (outputBytes != null) {
                fos.write(outputBytes);
            }
        }
    }

    private static String nombre(String ruta, boolean cifrar) {
        int punto = ruta.lastIndexOf('.');
        String sufijo = cifrar ? "_E_DES" : "_D_DES";
        if (punto == -1) {
            return ruta + sufijo;
        } else {
            String nombreb = ruta.substring(0, punto);
            String nombresufijo = ruta.substring(punto);
            return nombreb + sufijo + nombresufijo;
        }
    }
}