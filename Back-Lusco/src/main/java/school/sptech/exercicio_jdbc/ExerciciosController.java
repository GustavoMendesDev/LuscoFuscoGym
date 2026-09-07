package school.sptech.exercicio_jdbc;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/exercicios")
public class ExerciciosController {

    private final JdbcTemplate template;

    public ExerciciosController(JdbcTemplate template) {
        this.template = template;
    }

    @GetMapping
    public ResponseEntity<List<Exercicio>> listar() {
        String sql = "SELECT * FROM exercicios";
        List<Exercicio> resultado = template.query(sql, new BeanPropertyRowMapper<>(Exercicio.class));
        return ResponseEntity.status(200).body(resultado);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Exercicio> listarPorId(@PathVariable Integer id) {
        String sql = "SELECT * FROM exercicios WHERE id = ?";
        try {
            Exercicio result = template.queryForObject(sql, new BeanPropertyRowMapper<>(Exercicio.class), id);
            return ResponseEntity.status(200).body(result);
        } catch (EmptyResultDataAccessException exception) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping
    public ResponseEntity<Exercicio> cadastrar(@RequestBody Exercicio exercicio) {

        if (!dadosValidos(exercicio)) {
            return ResponseEntity.status(400).build();
        }

        if (existeDuplicada(exercicio.getNome(), exercicio.getGrupo(), null)) {
            return ResponseEntity.status(409).build();
        }

        String sql = "INSERT INTO exercicios (nome, grupo, series, repeticoes, carga) VALUES (?, ?, ?, ?, ?)";
        KeyHolder holder = new GeneratedKeyHolder();

        template.update(con -> {
            PreparedStatement statement = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, exercicio.getNome());
            statement.setString(2, exercicio.getGrupo());
            statement.setInt(3, exercicio.getSeries());
            statement.setInt(4, exercicio.getRepeticoes());
            statement.setInt(5, exercicio.getCarga());
            return statement;
        }, holder);

        Number key = holder.getKey();
        if (key == null) {
            return ResponseEntity.status(500).build();
        }
        exercicio.setId(key.intValue());

        return ResponseEntity.status(201).body(exercicio);
    }


    @GetMapping("/search")
    public ResponseEntity<List<Exercicio>> buscarDinamico(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String grupo,
            @RequestParam(required = false) Integer series,
            @RequestParam(required = false) Integer repeticoes,
            @RequestParam(required = false) Integer carga

    ) {
        StringBuilder sql = new StringBuilder("SELECT * FROM exercicios WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (nome != null && !nome.isBlank()) {
            sql.append(" AND LOWER(nome) LIKE ?");
            parametros.add("%" + nome.toLowerCase() + "%");
        }
        if (grupo != null && !grupo.isBlank()) {
            sql.append(" AND LOWER(artista) LIKE ?");
            parametros.add("%" + grupo.toLowerCase() + "%");
        }
        if (series != null) {
            sql.append(" AND duracao = ?");
            parametros.add(series);
        }
        if (repeticoes != null) {
            sql.append(" AND duracao = ?");
            parametros.add(repeticoes);
        }
        if (carga != null) {
            sql.append(" AND duracao = ?");
            parametros.add(carga);
        }

        List<Exercicio> resultado = template.query(
                sql.toString(),
                new BeanPropertyRowMapper<>(Exercicio.class),
                parametros.toArray()
        );

        return ResponseEntity.status(200).body(resultado);
    }

    private boolean dadosValidos(Exercicio exercicio) {
        if (exercicio.getNome() == null || exercicio.getNome().isBlank()) return false;
        if (exercicio.getGrupo() == null || exercicio.getGrupo().isBlank()) return false;

        return exercicio.getSeries() != null && exercicio.getSeries() > 0
                && exercicio.getRepeticoes() != null && exercicio.getRepeticoes() > 0
                && exercicio.getCarga() != null && exercicio.getCarga() > 0;
    }

    private boolean existePorId(Integer id) {
        String sql = "SELECT COUNT(*) FROM exercicios WHERE id = ?";
        Integer count = template.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }

    private boolean existeDuplicada(String nome, String grupo, Integer idIgnorado) {
        String sql = "SELECT COUNT(*) FROM exercicios WHERE LOWER(nome) = ? AND LOWER(grupo) = ?"
                + (idIgnorado != null ? " AND id <> ?" : "");

        Integer count;
        if (idIgnorado != null) {
            count = template.queryForObject(sql, Integer.class,
                    nome.toLowerCase(), grupo.toLowerCase(), idIgnorado);
        } else {
            count = template.queryForObject(sql, Integer.class,
                    nome.toLowerCase(), grupo.toLowerCase());
        }
        return count != null && count > 0;
    }
}