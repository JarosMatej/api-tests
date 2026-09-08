import config.BaseTest;
import config.ParamsConfig;
import config.Base64Resources;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HappyPathTest extends BaseTest {

    private static int docId;
    private static String docOwner;
    private static String docToken;


    @Test
    @Order(1)
    void uploadDoc() {
        var docData = Base64Resources.getDocData();
        var conf = getConf(docData);
        var spec = this.uploadDocument(conf)
                .statusCode(200)
                .assertThat().body("uploadResult", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_DOC_VALID_SCHEMA_JSON))
                .extract();
        docId = spec.path("document.docid");
        docOwner = spec.path("document.docowner");
        docToken = spec.path("document.doctoken");
    }

    private static @NonNull ParamsConfig getConf(String docData) {
        String[] names = {"docData", "docOwner", "documentName", "applicationMode", "parameters"};
        String[][] vals = {{docData}, {config.getDocOwner()}, {"integration_test" + getTimeStamp()}, {"EDIT"}, {"type=signature|subtype=biometric|name=sig2|page=1|positionX=20|positionY=20|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true;type=signature|subtype=biometric|name=sig3|page=1|positionX=40|positionY=40|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true"}};
        return new ParamsConfig(names, vals);
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
