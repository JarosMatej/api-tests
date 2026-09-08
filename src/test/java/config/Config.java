package config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
    private static final Config INSTANCE = new Config();

    private Config() {
    }

    public static Config getInstance() {
        return INSTANCE;
    }

    public String getServer() {
        return Config.dotenv.get("SERVER", "https://test.signosoft.com/api");
    }

    public String getDocOwner() {
        return Config.dotenv.get("DOC_OWNER");
    }

    public String getUsername() {
        return Config.dotenv.get("LOGIN");
    }

    public String getPassword() {
        return Config.dotenv.get("PASSWORD");
    }

    public String getSignerLogin() {
        return Config.dotenv.get("SIGNER_LOGIN");
    }

    public String getName() {
        return Config.dotenv.get("NAME");
    }

    public String get(String key) {
        return Config.dotenv.get(key);
    }
}
