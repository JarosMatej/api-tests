import config.Base64Resources;
import config.BaseTest;
import config.ParamsConfig;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class CreateContractTest extends BaseTest {

    private static int docId;
    private static String docOwner;
    private static String docToken;
    private static int docId2;
    private static String docOwner2;
    private static String docToken2;
    private static int docId3;
    private static String docOwner3;
    private static String docToken3;
    private static String contractToken;
    private static int sigid;

    private static final String docData = Base64Resources.getDocData();
    private static final String contractDocuments = Base64Resources.getDocumentsCreateContract();
    private static final String sigBioData = Base64Resources.getSigBioData();
    private static final String sigImgBase64 = Base64Resources.getSigImgBase64();

    @Test
    @Order(1)
    void createContract() {
        String[] names = {"owner", "contractName", "applicationMode", "documents"};
        String[][] vals = {{config.getDocOwner()}, {"Integration_test" + getTimeStamp()}, {"SIGN"}, {contractDocuments}};
        var conf = new ParamsConfig(names, vals);
        var spec = this.authenticatedRequestSpec(conf, createContractPath)
                .statusCode(200)
                .assertThat()
                .body("result", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(CREATE_CONTRACT_VALID_SCHEMA))
                .extract();
        contractToken = spec.path("contoken");
    }

    @Test
    @Order(2)
    void getContractByToken() {
        String[] names = {"token"};
        String[][] vals = {{contractToken}};
        var conf = new ParamsConfig(names, vals);
        var spec = this.authenticatedRequestSpec(conf, getContractByTokenPath)
                .statusCode(200)
                .assertThat()
                .body("getDocResult", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(GET_CONTRACT_BY_TOKEN_VALID_SCHEMA))
                .extract();

        sigid = spec.path("docObject.signatures[0].sigid");
    }

    @Test
    @Order(3)
    void signDocumentCopyForContract() {
        String[] names = {"docid", "sigid", "methodid", "authType", "docOwner", "login", "finalizeAutoExecute", "acroFields", "sigBioData", "sigImgBase64", "signerName"};
        String[][] vals = {{String.valueOf(docId)}, {String.valueOf(sigid)}, {"3"}, {"biometric"}, {config.getUsername()}, {config.getUsername()}, {"on"}, {"[]"}, {sigBioData}, {sigImgBase64}, {config.getName() + " " + config.getName()}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, signDocPath)
                .statusCode(200)
                .assertThat()
                .body("signResult", equalTo("SIGN_OK"))
                .body(matchesJsonSchemaInClasspath(SIGN_VALID_SCHEMA_JSON));
    }
}
