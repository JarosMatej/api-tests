import config.BaseTest;
import config.DocumentDeleter;
import config.ParamsConfig;
import org.junit.platform.suite.api.AfterSuite;
import org.junit.platform.suite.api.Suite;

@Suite(failIfNoTests = false)
public class TearDownTest extends BaseTest {
    @AfterSuite
    static void TearDown() {
        var tokens = DocumentDeleter.getDocTokens();

        if (tokens.isEmpty()) {
            return;
        }

        String[] names = {"docTokens"};
        String[][] values = {{DocumentDeleter.serializeDocTokens()}};
        var conf = new ParamsConfig(names, values);
        BaseTest bs = new BaseTest();
        bs.authenticatedRequestSpec(conf, deleteDocumentPath).statusCode(200);

    }
}
