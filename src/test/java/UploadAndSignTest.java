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
public class UploadAndSignTest extends BaseTest {

    private static int docId;
    private static int sigid0;
    private static int sigid1;
    private static int sigid2;

    private static final String docData = Base64Resources.getDocData();
    private static final String sigBioData = Base64Resources.getSigBioData();
    private static final String sigImgBase64 = Base64Resources.getSigImgBase64();


    @Test
    @Order(1)
    void uploadDocument() {
        String[] names = {"docData", "docOwner", "documentName", "applicationMode", "parameters"};
        String name = config.getName();
        String username = config.getUsername();
        String parameters = "type=signature|subtype=biometric|name=sig0|page=1|positionX=20|positionY=20|width=20|height=7|required=true|signerLogin=" + username + "|signerfirstname=" + name + "|signersecondname=" + name + "|inperson=true|required=true;" +
                "type=signature|subtype=biometric|name=sig1|page=1|positionX=20|positionY=30|width=20|height=7|required=true|signerLogin=" + username + "|signerfirstname=" + name + "|signersecondname=" + name + "|inperson=true|required=true;" +
                "type=signature|subtype=biometric|name=sig2|page=1|positionX=20|positionY=40|width=20|height=7|required=true|signerLogin=" + username + "|signerfirstname=" + name + "|signersecondname=" + name + "|inperson=true|required=true;" +
                "type=signature|subtype=biometric|name=sig3|page=1|positionX=20|positionY=50|width=20|height=7|required=true|signerLogin=" + username + "|signerfirstname=" + name + "|signersecondname=" + name + "|inperson=true|required=true;";
        String[][] vals = {{docData}, {username}, {"Integration_test" + getTimeStamp()}, {"SIGN"}, {parameters}};
        var conf = new ParamsConfig(names, vals);
        var spec = this.uploadDocument(conf)
                .statusCode(200)
                .assertThat().body("uploadResult", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_DOC_VALID_SCHEMA_JSON))
                .extract();
        docId = spec.path("document.docid");
        sigid0 = spec.path("signatures[0].sigid");
        sigid1 = spec.path("signatures[1].sigid");
        sigid2 = spec.path("signatures[2].sigid");
    }

    @Test
    @Order(2)
    void signDocumentSignImgEmpty() {

        String[] names = {"docid", "sigid", "methodid", "authType", "docOwner", "login", "finalizeAutoExecute", "acroFields", "sigBioData", "signerName"};
        String username = config.getUsername();
        String[][] vals = {{String.valueOf(docId)}, {String.valueOf(sigid0)}, {"3"}, {"biometric"}, {username}, {username}, {"on"}, {"[]"}, {sigBioData}, {config.getName() + " " + config.getName()}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, signDocPath)
                .statusCode(200)
                .assertThat()
                .body("signResult", equalTo("SIGN_IMG_EMPTY"))
                .body(matchesJsonSchemaInClasspath(SIGN_ERROR_SCHEMA_JSON));
    }

    @Test
    @Order(3)
    void signDocument() {

        String[] names = {"docid", "sigid", "methodid", "authType", "docOwner", "login", "finalizeAutoExecute", "acroFields", "sigBioData", "sigImgBase64", "signerName"};
        String username = config.getUsername();
        String[][] vals = {{String.valueOf(docId)}, {String.valueOf(sigid1)}, {"3"}, {"biometric"}, {username}, {username}, {"on"}, {"[]"}, {sigBioData}, {sigImgBase64}, {config.getName() + " " + config.getName()}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, signDocPath)
                .statusCode(200)
                .assertThat()
                .body("signResult", equalTo("SIGN_OK"))
                .body(matchesJsonSchemaInClasspath(SIGN_VALID_SCHEMA_JSON));
    }

    @Test
    @Order(4)
    void signDocumentCopySignErrorNoBioData() {
        String[] names = {"docid", "sigid", "methodid", "authType", "docOwner", "login", "finalizeAutoExecute", "acroFields", "sigImgBase64", "signerName"};
        String username = config.getUsername();
        String[][] vals = {{String.valueOf(docId)}, {String.valueOf(sigid2)}, {"3"}, {"biometric"}, {username}, {username}, {"on"}, {"[]"}, {sigImgBase64}, {config.getName() + " " + config.getName()}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, signDocPath)
                .statusCode(200)
                .assertThat()
                .body("signResult", equalTo("SIGN_ERROR"))
                .body(matchesJsonSchemaInClasspath(SIGN_ERROR_SCHEMA_JSON));
    }

    @Test
    @Order(5)
    void signDocumentMissingParam() {
        String[] names = {"docid", "methodid", "authType", "docOwner", "login", "finalizeAutoExecute", "acroFields", "sigBioData", "sigImgBase64", "signerName"};
        String username = config.getUsername();
        String[][] vals = {{String.valueOf(docId)}, {"3"}, {"biometric"}, {username}, {username}, {"on"}, {"[]"}, {sigBioData}, {sigImgBase64}, {config.getName() + " " + config.getName()}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, signDocPath)
                .statusCode(400)
                .assertThat()
                .body("error", equalTo("MISSING_PARAM"))
                .body(matchesJsonSchemaInClasspath(SIGN_MISSING_PARAM_SCHEMA_JSON));
    }

    @Test
    @Order(6)
    void signDocumentSignatureIsAlreadySigned() {
        String[] names = {"docid", "sigid", "methodid", "authType", "docOwner", "login", "finalizeAutoExecute", "acroFields", "sigBioData", "sigImgBase64", "signerName"};
        String username = config.getUsername();
        String[][] vals = {{String.valueOf(docId)}, {String.valueOf(sigid1)}, {"3"}, {"biometric"}, {username}, {username}, {"on"}, {"[]"}, {sigBioData}, {sigImgBase64}, {config.getName() + " " + config.getName()}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, signDocPath)
                .statusCode(200)
                .assertThat()
                .body("signResult", equalTo("SIGNATURE_IS_ALREADY_SIGNED"))
                .body(matchesJsonSchemaInClasspath(SIGN_VALID_SCHEMA_JSON));
    }
}
