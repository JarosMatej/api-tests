import config.Base64Resources;
import config.BaseTest;
import config.ParamsConfig;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class AddAttachmentTest extends BaseTest {
    private static String docToken;

    private static final String docData = Base64Resources.getDocData();
    private static final String attachmentDataInvalidData = Base64Resources.getDataUploadAttachmentInvalidData();
    private static final String attachmentData = Base64Resources.getDataUploadAttachment();

    @Test
    @Order(1)
    void restLoginWrongLogin() {
        String[] names = {"login", "password"};
        String[][] vals = {{"xd" + config.getUsername()}, {config.getPassword()}};
        var conf = new ParamsConfig(names, vals);
        this.defaultRequestSpec(conf, loginPath)
                .statusCode(200)
                .assertThat()
                .body("result", equalTo("ERROR"))
                .body("message", equalTo("User does not exist."))
                .body(matchesJsonSchemaInClasspath(LOGIN_WRONG_SCHEMA_JSON));
    }

    @Test
    @Order(2)
    void uploadDocumentComplete() {
        String[] names = {"docData", "documentName", "applicationMode", "docOwner"};
        String[][] vals = {{docData}, {"Integration_test" + getTimeStamp()}, {"SIGN"}, {config.getSignerLogin()}};
        var conf = new ParamsConfig(names, vals);
        var spec = this.uploadDocument(conf)
                .statusCode(200)
                .assertThat()
                .body("uploadResult", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_DOC_VALID_SCHEMA_JSON))
                .extract();
        docToken = spec.path("document.doctoken");
    }

    @Test
    @Order(3)
    void uploadAttachmentInvalidData() {
        String[] names = {"token", "metadata", "data", "internal"};
        String[][] vals = {{String.valueOf(docToken)}, {"\"name\":\"Integration_test" + getTimeStamp() + "\",\"type\":\"png\""}, {attachmentDataInvalidData}, {"true"}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, uploadAttachmentPath)
                .statusCode(200)
                .assertThat()
                .body("uploadResult", equalTo("OK"))
                .body("internal", equalTo("false"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_ATTACHMENT_SCHEMA));
    }

    @Test
    @Order(4)
    void uploadAttachment() {
        String[] names = {"token", "metadata", "data", "internal"};
        String[][] vals = {{String.valueOf(docToken)}, {"\"name\":\"Integration_test" + getTimeStamp() + "\",\"type\":\"png\""}, {attachmentData}, {"true"}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, uploadAttachmentPath)
                .statusCode(200)
                .assertThat()
                .body("uploadResult", equalTo("OK"))
                .body("internal", equalTo("true"))
                .body(matchesJsonSchemaInClasspath(UPLOAD_ATTACHMENT_SCHEMA));
    }

}
