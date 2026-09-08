import config.BaseTest;
import config.ParamsConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NegativePathTest extends BaseTest {

    private static final int docId = 0;
    private static final String docOwner = "";
    private static final String docToken = "";

    @Test
    @Order(1)
    void loginWrongPassword() {
        String[] names = {"login", "password"};
        String[][] vals = {{config.getUsername()}, {config.getPassword() + "t"}};
        var conf = new ParamsConfig(names, vals);
        this.defaultRequestSpec(conf, loginPath)
                .statusCode(200)
                .assertThat()
                .body("result", equalTo("ERROR"))
                .body(matchesJsonSchemaInClasspath(LOGIN_ERROR_SCHEMA_JSON));
    }

    @Test
    @Order(2)
    void uploadDocumentMissingDocData() {
        String[] names = {"docOwner", "documentName", "applicationMode", "parameters"};
        String[][] vals = {{config.getDocOwner()}, {"Integration_test" + getTimeStamp()}, {"EDIT"}, {"type=signature|subtype=biometric|name=sig2|page=1|positionX=20|positionY=20|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true;type=signature|subtype=biometric|name=sig3|page=1|positionX=40|positionY=40|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true"}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, uploadDocPath)
                .statusCode(200)
                .assertThat()
                .body("uploadResult", equalTo("DOCDATA EMTPY"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_ERROR_SCHEMA_JSON));
    }

    @Test
    @Order(3)
    void createDocLink() {
        String[] names = {"docId", "docOwner", "doctoken", "showPanelRight", "showPanelTop", "showEmailsInSig", "showPRFinalize"};
        String[][] vals = {{String.valueOf(docId)}, {docOwner}, {docToken}, {"true"}, {"true"}, {"false"}, {"true"}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, createDocLinkPath)
                .statusCode(200)
                .assertThat()
                .body("createDocLinkResult", equalTo("WRONG_DOC_TOKEN"))
                .body(matchesJsonSchemaInClasspath(CREATE_DOC_LINK_ERROR_SCHEMA_JSON));
    }
}
