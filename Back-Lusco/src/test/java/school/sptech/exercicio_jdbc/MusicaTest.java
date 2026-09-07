package school.sptech.exercicio_jdbc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.core.api.dataset.CompareOperation;
import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.api.DBRider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import school.sptech.exercicio_jdbc.utils.FileUtil;
import tools.jackson.databind.ObjectMapper;

@DBRider
@DisplayName("Musica Test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DBUnit(cacheConnection = false, alwaysCleanAfter = true, raiseExceptionOnCleanUp = true)
class MusicaTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper objectMapper;

    private RestTestClient client;

    @BeforeEach
    void setUp() {
        client = RestTestClient.bindToServer()
              .baseUrl("http://localhost:" + port)
              .build();
    }

    @Nested
    @DisplayName("GET /musicas")
    class GetMusicas {

        @Test
        @DisplayName("GET /musicas deve retornar lista de músicas corretamente")
        @DataSet(value = "datasets/input/musics.json")
        @ExpectedDataSet("datasets/expected/musics.json")
        void shouldReturnListOfMusicsCorrectly() throws Exception {
            var response = client.get().uri("/musicas")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();

            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(response.getResponseBody());

            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/get-musicas.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas deve retornar 200 com lista vazia quando não houver músicas no banco de dados")
        @DataSet(value = "datasets/input/empty-musics.json")
        @ExpectedDataSet(value = "datasets/expected/empty-musics.json")
        void shouldReturnEmptyListWhenDatabaseIsEmpty() {
            var response = client.get().uri("/musicas")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expectedRoot = objectMapper.readTree("[]");
            assertEquals(expectedRoot, root);
        }
    }

    @Nested
    @DisplayName("GET /musicas/{id}")
    @DataSet(value = "datasets/input/musics.json")
    class GetMusicaById {

        @Test
        @DisplayName("GET /musicas/{id} deve retornar música por id corretamente")
        @ExpectedDataSet("datasets/expected/musics.json")
        void shouldReturnMusicByIdCorrectly() throws Exception {
            var response = client.get().uri("/musicas/1")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();

            assertEquals(HttpStatus.OK, response.getStatus());
            assertNotNull(response.getResponseBody());

            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/musica-by-id-1.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/{id} deve retornar 404 quando música não existir")
        @ExpectedDataSet("datasets/expected/musics.json")
        void shouldReturn404WhenMusicDoesNotExist() {
            var response = client.get().uri("/musicas/999")
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        }
    }

    @Nested
    @DisplayName("POST /musicas")
    @DataSet(value = "datasets/input/musics.json")
    class PostMusica {

        @Test
        @DisplayName("POST /musicas deve criar música corretamente")
        @ExpectedDataSet(value = "datasets/expected/musics-after-post.json", compareOperation = CompareOperation.CONTAINS)
        void shouldCreateMusic() {
            var requestBody = FileUtil.readFile("/request/new-music.json");
            var response = client.post().uri("/musicas")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(requestBody)
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();

            assertEquals(HttpStatus.CREATED, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/new-music.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("POST /musicas deve retornar 409 quando música já existir")
        @DataSet(value = "datasets/input/musics.json")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturn409WhenMusicAlreadyExists() {
            var requestBody = FileUtil.readFile("/request/new-duplicated-music.json");
            var response = client.post().uri("/musicas")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(requestBody)
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.CONFLICT, response.getStatus());
        }

        @Test
        @DisplayName("POST /musicas deve retornar 409 quando nome/artista diferirem apenas em maiúsculas/minúsculas")
        @DataSet(value = "datasets/input/musics.json")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturn409WhenMusicAlreadyExistsIgnoringCase() {
            var requestBody = FileUtil.readFile("/request/new-duplicated-music-case-insensitive.json");
            var response = client.post().uri("/musicas")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(requestBody)
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.CONFLICT, response.getStatus());
        }

        @DisplayName("POST /musicas deve validar os campos")
        @ParameterizedTest(name = "{0}")
        @ArgumentsSource(InvalidMusicaProvider.class)
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldValidatePostMusica(String caso, String file) {
            var requestBody = FileUtil.readFile("/request/" + file);
            var response = client.post().uri("/musicas")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(requestBody)
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        }
    }

    @Nested
    @DisplayName("DELETE /musicas")
    @DataSet(value = "datasets/input/musics.json")
    class DeleteMusica {

        @Test
        @DisplayName("DELETE /musicas deve deletar música corretamente")
        @ExpectedDataSet(value = "datasets/expected/musics-after-delete-1.json")
        void shouldDeleteMusicCorrectly() {
            var response = client.delete().uri("/musicas/1")
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.NO_CONTENT, response.getStatus());
        }

        @Test
        @DisplayName("DELETE /musicas deve retornar 404 quando música não existir")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturn404WhenMusicDoesNotExist() {
            var response = client.delete().uri("/musicas/999")
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        }
    }

    @Nested
    @DisplayName("PUT /musicas")
    @DataSet(value = "datasets/input/musics.json")
    class PutMusica {

        @Test
        @DisplayName("PUT /musicas deve atualizar música corretamente")
        @ExpectedDataSet(value = "datasets/expected/musics-after-put-1.json", compareOperation = CompareOperation.CONTAINS)
        void shouldUpdateMusicWhenItExists() {
            var requestBody = FileUtil.readFile("/request/update-music.json");
            var response = client.put().uri("/musicas/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(requestBody)
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/update-music-1.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(root, expectedRoot);
        }

        @Test
        @DisplayName("PUT /musicas deve retornar 404 quando música não existir")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturn404WhenMusicDoesNotExist() {
            var requestBody = FileUtil.readFile("/request/update-music.json");
            var response = client.put().uri("/musicas/999")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(requestBody)
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.NOT_FOUND, response.getStatus());
        }

        @Test
        @DisplayName("PUT /musicas/{id} deve retornar 409 quando outro registro já tiver o mesmo nome e artista")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturn409WhenUpdatingToDuplicateNameAndArtist() {
            var requestBody = FileUtil.readFile("/request/update-music-duplicate.json");
            var response = client.put().uri("/musicas/2")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(requestBody)
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.CONFLICT, response.getStatus());
        }

        @DisplayName("PUT /musicas deve validar os campos")
        @ParameterizedTest(name = "{0}")
        @ArgumentsSource(InvalidMusicaProvider.class)
        @DataSet(value = "datasets/input/musics.json")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldValidatePutMusica(String caso, String file) {
            var requestBody = FileUtil.readFile("/request/" + file);
            var response = client.put().uri("/musicas/1")
                  .contentType(MediaType.APPLICATION_JSON)
                  .body(requestBody)
                  .exchange()
                  .expectBody()
                  .returnResult();
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatus());
        }
    }

    @Nested
    @DisplayName("GET /musicas/search")
    @DataSet(value = "datasets/input/musics.json")
    class SearchMusicas {

        @Test
        @DisplayName("GET /musicas/search deve retornar músicas filtradas por nome")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnMusicsFilteredByName() {
            var response = client.get().uri("/musicas/search?nome=in")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/musics-search-by-name.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/search deve retornar músicas filtradas por artista")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnMusicsFilteredByArtist() {
            var response = client.get().uri("/musicas/search?artista=Queen")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/musics-search-by-artist.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/search deve retornar músicas filtradas por álbum")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnMusicsFilteredByAlbum() {
            var response = client.get().uri("/musicas/search?album=Thriller")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/musics-search-by-album.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/search deve retornar músicas filtradas por duração")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnMusicsFilteredByDuration() {
            var response = client.get().uri("/musicas/search?duracao=178")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/musics-search-by-duration.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/search deve retornar 200 com lista vazia quando não houver músicas que atendam aos filtros")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnEmptyListWhenNoMusicsMatchFilters() {
            var response = client.get().uri("/musicas/search?nome=Nonexistent")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expectedRoot = objectMapper.readTree("[]");
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/search deve retornar musicas filtradas por nome e artista")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnMusicsFilteredByNameOrArtist() {
            var response = client.get().uri("/musicas/search?nome=come&artista=Nir")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/musics-search-by-name-and-artist.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/search deve retornar musicas filtradas por nome, artista e album")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnMusicsFilteredByNameArtistAndAlbum() {
            var response = client.get().uri("/musicas/search?nome=Wonder&artista=Oasis&album=Morning")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile(
                  "/response/musics-search-by-name-artist-and-album.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/search deve retornar musicas filtradas por nome, artista, album e duração")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnMusicsFilteredByNameArtistAlbumAndDuration() {
            var response = client.get().uri(
                        "/musicas/search?nome=Beat&artista=Michael&album=Thriller&duracao=258")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile(
                  "/response/musics-search-by-name-artist-album-and-duration.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }

        @Test
        @DisplayName("GET /musicas/search deve retornar musicas filtradas por album e duracao")
        @ExpectedDataSet(value = "datasets/expected/musics.json")
        void shouldReturnMusicsFilteredByAlbumOrDuration() {
            var response = client.get().uri("/musicas/search?album=Desperado&duracao=224")
                  .exchange()
                  .expectBody(String.class)
                  .returnResult();
            assertEquals(HttpStatus.OK, response.getStatus());
            var root = objectMapper.readTree(response.getResponseBody());
            var expected = FileUtil.readFile("/response/musics-search-by-album-and-duration.json");
            var expectedRoot = objectMapper.readTree(expected);
            assertEquals(expectedRoot, root);
        }
    }
}
