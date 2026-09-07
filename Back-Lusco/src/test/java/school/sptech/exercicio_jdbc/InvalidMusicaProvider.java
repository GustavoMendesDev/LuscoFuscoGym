package school.sptech.exercicio_jdbc;

import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

public class InvalidMusicaProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(
              Arguments.of("Deve retornar 400 quando nome da música for invalido", "new-music-invalid-name.json"),
              Arguments.of("Deve retornar 400 quando nome da música for null", "new-music-invalid-name-null.json"),
              Arguments.of("Deve retornar 400 quando nome do artista for invalido", "new-music-invalid-artist.json"),
              Arguments.of("Deve retornar 400 quando nome do artista for null", "new-music-invalid-artist-null.json"),
              Arguments.of("Deve retornar 400 quando nome do álbum for invalido", "new-music-invalid-album.json"),
              Arguments.of("Deve retornar 400 quando nome do álbum for null", "new-music-invalid-album-null.json"),
              Arguments.of("Deve retornar 400 quando duração for negativa", "new-music-invalid-duration.json"),
              Arguments.of("Deve retornar 400 quando duração for zero", "new-music-invalid-duration-2.json")
        );
    }
}
