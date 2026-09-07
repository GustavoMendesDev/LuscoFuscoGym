package school.sptech.exercicio_jdbc.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class FileUtil {

    public static String readFile(String name) {
        try (InputStream is = FileUtil.class.getResourceAsStream(name)) {
            if (is == null) {
                throw new IllegalArgumentException("Arquivo não encontrado: " + name);
            }

            try (BufferedReader reader =
                  new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }

        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler o arquivo: " + name, e);
        }
    }
}
