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
public class UploadSignDownLoadTest extends BaseTest {

    private static int docId;
    private static String docToken;
    private static int sigId;

    private static final String docData = Base64Resources.getDocData();

    private static final String sigBioData = Base64Resources.getSigBioData();
    private static final String sigImgBase64 = Base64Resources.getSigImgBase64();

    @Test
    @Order(1)
    void uploadDocument() {
        String parameters = "type=signature|subtype=biometric|name=sig2|page=1|positionX=20|positionY=20|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true;type=signature|subtype=biometric|name=sig3|page=1|positionX=40|positionY=40|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true;type=signature|subtype=biometric|name=sig4|page=1|positionX=60|positionY=60|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true;type=signature|subtype=biometric|name=sig5|page=1|positionX=80|positionY=80|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true";
        String[] names = {"docData", "documentName", "applicationMode", "docOwner", "parameters"};
        String[][] vals = {{docData}, {"Integration_test" + getTimeStamp()}, {"SIGN"}, {config.getUsername()}, {parameters}};
        var conf = new ParamsConfig(names, vals);
        var spec = this.uploadDocument(conf)
                .statusCode(200)
                .assertThat()
                .body("uploadResult", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_DOC_VALID_SCHEMA_JSON))
                .extract();
        docId = spec.path("document.docid");
        docToken = spec.path("document.doctoken");
        sigId = spec.path("signatures[1].sigid");
    }

    @Test
    @Order(2)
    void signDocumentCopy() {
        String[] names = {"docid", "sigid", "methodid", "authType", "docOwner", "login", "finalizeAutoExecute", "acroFields", "sigBioData", "sigImgBase64", "signerName"};
        String[][] vals = {{String.valueOf(docId)}, {String.valueOf(sigId)}, {"3"}, {"biometric"}, {config.getUsername()}, {config.getUsername()}, {"on"}, {"[]"}, {sigBioData}, {sigImgBase64}, {config.getName() + " " + config.getName()}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, signDocPath)
                .statusCode(200)
                .assertThat()
                .body("signResult", equalTo("SIGN_OK"))
                .body(matchesJsonSchemaInClasspath(SIGN_VALID_SCHEMA_JSON));
    }

    @Test
    @Order(3)
    void downloadDoc() {
        String[] names = {"docToken", "flattened", "lock", "toFile"};
        String[][] vals = {{docToken}, {"true"}, {"false"}, {"false"}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, "/REST/downloadDoc")
                .statusCode(200)
                .assertThat()
                .body("downloadDocResult", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(DOWNLOAD_DOC_VALID_SCHEMA_JSON));
    }
}
