package neat.xor;

import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Config {
    private static Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getResourceAsStream("/neatconfig.properties")) {
            if (in == null) {
                throw new FileNotFoundException("neatconfig.properties introuvable dans le classpath");
            }
            props.load(in);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la config");
            e.printStackTrace();
        }
    }

    public static int getInt(String key) {
        return Integer.parseInt(props.getProperty(key));
    }

    public static double getDouble(String key) {
        return Double.parseDouble(props.getProperty(key));
    }

    public static String getString(String key) {
        return props.getProperty(key);
    }

    public static boolean getBool(String key) {
        return Boolean.parseBoolean(props.getProperty(key));
    }
}
