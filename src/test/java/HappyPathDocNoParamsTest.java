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
public class HappyPathDocNoParamsTest extends BaseTest {

    private static int docId;
    private static String docOwner;
    private static String docToken;

    private static final String docData = Base64Resources.getDocData();

    @Test
    @Order(1)
    void uploadDocumentWithoutParameters() {
        String[] names = {"docData", "docOwner", "documentName", "applicationMode"};
        String[][] vals = {{docData}, {config.getSignerLogin()}, {"Integration_test" + getTimeStamp()}, {"EDIT"}};
        var conf = new ParamsConfig(names, vals);
        var spec = this.uploadDocument(conf)
                .statusCode(200)
                .assertThat()
                .body("uploadResult", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_DOC_VALID_SCHEMA_JSON))
                .extract();
        docId = spec.path("document.docid");
        docOwner = spec.path("document.docowner");
        docToken = spec.path("document.doctoken");
    }

    @Test
    @Order(2)
    void createDocLink() {
        String[] names = {"docId", "docOwner", "doctoken", "showPanelRight", "showPanelTop", "showEmailsInSig", "showPRFinalize"};
        String[][] vals = {{String.valueOf(docId)}, {docOwner}, {docToken}, {"true"}, {"true"}, {"false"}, {"true"}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, createDocLinkPath)
                .statusCode(200)
                .assertThat()
                .body("createDocLinkResult", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(CREATE_DOC_LINK_VALID_SCHEMA_JSON));
    }
}
