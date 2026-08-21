package config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Config INSTANCE = new Config();
    private final Dotenv dotenv;

    private Config() {
        this.dotenv = Dotenv.configure().ignoreIfMissing().load();
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public String getServer() {
        return dotenv.get("SERVER", "https://test.signosoft.com/api");
    }

    public String getDocOwner() {
        return dotenv.get("DOC_OWNER");
    }

    public String getUsername() {
        return dotenv.get("LOGIN");
    }

    public String getPassword() {
        return dotenv.get("PASSWORD");
    }

    public String getSignerLogin() {
        return dotenv.get("SIGNER_LOGIN");
    }

    public String getName() {
        return dotenv.get("NAME");
    }

    public String get(String key) {
        return dotenv.get(key);
    }
}
