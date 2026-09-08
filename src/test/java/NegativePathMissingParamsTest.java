import config.Base64Resources;
import config.BaseTest;
import config.ParamsConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class NegativePathMissingParamsTest extends BaseTest {

    private static final int docId = 0;
    private static final String docOwner = "";
    private static final String docToken = "";

    private static final String docData = Base64Resources.getDocDataNegativePathMissingParams();
    @Test
    @Order(1)
    void loginWith400() {
        String[] names = {"login"};
        String[][] vals = {{config.getUsername()}};
        var conf = new ParamsConfig(names, vals);
        this.defaultRequestSpec(conf, loginPath)
                .statusCode(400)
                .assertThat()
                .body("error", equalTo("MISSING_PARAM"))
                .body(matchesJsonSchemaInClasspath(LOGIN_MISSING_PARAM_ERROR_SCHEMA_JSON));
    }

    @Test
    @Order(2)
    void uploadDocumentMissingDocName() {
        String parametersStr = "type=signature|subtype=biometric|name=sig2|page=1|positionX=20|positionY=20|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true;type=signature|subtype=biometric|name=sig3|page=1|positionX=40|positionY=40|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true";
        String[] names = {"docOwner", "applicationMode", "parameters", "docData"};
        String[][] vals = {{config.getSignerLogin()}, {"EDIT"}, {parametersStr}, {docData}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, uploadDocPath)
                .statusCode(400)
                .assertThat()
                .body("error", equalTo("MISSING_PARAM"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_MISSING_PARAM_ERROR_SCHEMA_JSON));
    }

    @Test
    @Order(3)
    void createDocLink() {
        String[] names = {"docId", "docOwner", "doctoken", "showPanelRight", "showPanelTop", "showEmailsInSig", "callback", "finalizeType", "finalizeUrl", "finalizeAutoExecute", "finalizeEnablePlugin", "showPRFinalize", "showPREmail", "showPRDownload", "finalizeWebhookUrl"};
        String[][] vals = {{String.valueOf(docId)}, {docOwner}, {docToken}, {"true"}, {"true"}, {"false"}, {""}, {"redirect"}, {"http://signosoft.ais-service.cz:8080/SigndocIntegrationService/resultinfo.jsp"}, {"confirm"}, {"true"}, {"true"}, {"false"}, {"false"}, {"-"}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, createDocLinkPath)
                .statusCode(200)
                .assertThat()
                .body("createDocLinkResult", equalTo("WRONG_DOC_TOKEN"))
                .body(matchesJsonSchemaInClasspath(CREATE_DOC_LINK_ERROR_SCHEMA_JSON));
    }
}
