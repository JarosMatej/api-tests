package config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class DocumentDeleter {
    private static final Path PATH_FILE = Path.of("target/doc-tokens-path.txt");

    private static Path getTokensFile() throws IOException {
        if (Files.exists(PATH_FILE)) {
            return Path.of(Files.readString(PATH_FILE).trim());
        }
        Path tempFile = Files.createTempFile("doc-tokens-", ".tmp");
        Files.createDirectories(PATH_FILE.getParent());
        Files.writeString(PATH_FILE, tempFile.toString());
        return tempFile;
    }

    public static void addToken(String token) {
        try {
            Path file = getTokensFile();
            Files.writeString(file, token + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save token", e);
        }
    }

    private static String authenticate() {
        Config config = Config.getInstance();
        return given()
            .baseUri(config.getServer())
            .param("login", config.getUsername())
            .param("password", config.getPassword())
            .log().all()
            .when()
            .post("/restLogin")
            .then()
            .log().all()
            .statusCode(200)
            .extract()
            .path("token");
    }

    public static void main(String[] args) {
        try {
            if (!Files.exists(PATH_FILE)) {
                return;
            }
            Path file = Path.of(Files.readString(PATH_FILE).trim());
            if (!Files.exists(file)) {
                return;
            }
            List<String> tokens = Files.readAllLines(file);
            if (tokens.isEmpty()) {
                return;
            }
            
            StringBuilder tokensArg = new StringBuilder("[");
            tokensArg.append("\"").append(tokens.getFirst()).append("\"");
            for (int i = 1; i < tokens.size(); ++i) {
                tokensArg.append(", \"").append(tokens.get(i)).append("\"");
            }
            tokensArg.append("]");
            
            Config config = Config.getInstance();
            String token = authenticate();
            
            given()
                .baseUri(config.getServer())
                .header("Authorization", "Bearer " + token)
                .param("docTokens", tokensArg.toString())
                .log().all()
                .when()
                .post("/REST/deleteDocument")
                .then()
                .log().all()
                .statusCode(200)
                .assertThat()
                .body("deleteResult", equalTo("DELETED"));
                
            Files.deleteIfExists(file);
            Files.deleteIfExists(PATH_FILE);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete documents", e);
        }
    }
}
