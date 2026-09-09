package config;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

public class BaseTest {

    protected static Config config = Config.getInstance();
    protected static String token;
    protected static String baseUri = config.getServer();

    protected static String loginPath = "/restLogin";

    protected static String uploadDocPath = "/REST/uploadDocument";
    protected static final String UPLOAD_DOC_VALID_SCHEMA_JSON = "schemas/upload-doc-valid-schema.json";
    protected static final String CREATE_DOC_LINK_VALID_SCHEMA_JSON = "schemas/create-doc-link-valid-schema.json";
    protected static final String LOGIN_ERROR_SCHEMA_JSON = "schemas/login-error-schema.json";
    protected static final String UPLOAD_ERROR_SCHEMA_JSON = "schemas/upload-error-schema.json";
    protected static final String CREATE_DOC_LINK_ERROR_SCHEMA_JSON = "schemas/create-doc-link-error-schema.json";
    protected static final String LOGIN_MISSING_PARAM_ERROR_SCHEMA_JSON = "schemas/login-missing-param-error-schema.json";
    protected static final String UPLOAD_MISSING_PARAM_ERROR_SCHEMA_JSON = "schemas/upload-missing-param-error-schema.json";
    protected static final String LOGIN_VALID_SCHEMA_JSON = "schemas/login-valid-schema.json";
    protected static final String LOGIN_WRONG_SCHEMA_JSON = "schemas/login-wrong-schema.json";
    protected static final String SIGN_VALID_SCHEMA_JSON = "schemas/sign-valid-schema.json";
    protected static final String SIGN_ERROR_SCHEMA_JSON = "schemas/sign-error-schema.json";
    protected static final String SIGN_MISSING_PARAM_SCHEMA_JSON = "schemas/sign-missing-param-schema.json";
    protected static final String DOWNLOAD_DOC_VALID_SCHEMA_JSON = "schemas/download-doc-valid-schema.json";

    protected static final String UPLOAD_ATTACHMENT_SCHEMA = "schemas/upload-attachment-schema.json";

    protected static final String CREATE_CONTRACT_VALID_SCHEMA = "schemas/create-contract-valid-schema.json";

    protected static final String GET_CONTRACT_BY_TOKEN_VALID_SCHEMA = "schemas/get-contract-by-token-valid-schema.json";
    protected static String signDocPath = "/REST/signDocument";

    protected static String uploadAttachmentPath = "/REST/uploadAttachment";
    protected static String createDocLinkPath = "/REST/createDocLink";
    protected static String createContractPath = "/REST/createContract";

    protected static String getContractByTokenPath = "/REST/getContractByToken";

    protected static String deleteDocumentPath = "/REST/deleteDocument";

    private static RequestSpecification baseGiven() {
        return given().
                baseUri(baseUri)
                .log()
                .ifValidationFails();
    }

    private static RequestSpecification baseAuthenticatedGiven() {
        return baseGiven()
                .header("Authorization", "Bearer " + token);
    }

    private static ValidatableResponse baseThen(Response spec) {
        return spec
                .then()
                .log()
                .ifValidationFails();
    }

    private void authenticate() {
        if (token != null) {
            return;
        }

        String[] names = {"login", "password"};
        String[][] vals = {{config.getUsername()}, {config.getPassword()}};
        var conf = new ParamsConfig(names, vals);
        token = this.defaultRequestSpec(conf, loginPath)
                .statusCode(200)
                .assertThat()
                .body("result", equalTo("OK"))
                .body(matchesJsonSchemaInClasspath(LOGIN_VALID_SCHEMA_JSON))
                .extract()
                .path("token");
    }

    private RequestSpecification setParams(ParamsConfig params, RequestSpecification spec) {
        var names = params.names();
        var vals = params.vals();
        var len = Math.min(names.size(), vals.size());
        for (var i = 0; i < len; ++i) {
            spec = spec.param(names.get(i), vals.get(i));
        }
        return spec;
    }

    public ValidatableResponse defaultRequestSpec(ParamsConfig params, String path, String body) {
        var spec = this.setParams(params, baseGiven());


        if (body.isEmpty()) {
            return baseThen(spec.when().post(path));
        }

        return baseThen(spec
                .body(body)
                .when()
                .post(path));
    }

    public ValidatableResponse defaultRequestSpec(ParamsConfig params, String path) {
        return this.defaultRequestSpec(params, path, "");
    }

    public ValidatableResponse authenticatedRequestSpec(ParamsConfig params, String path, String body) {
        this.authenticate();

        var spec = this.setParams(params, baseAuthenticatedGiven());

        if (body.isEmpty()) {
            return baseThen(spec
                    .when()
                    .post(path));
        }

        return baseThen(spec
                .body(body)
                .when()
                .post(path));
    }

    public ValidatableResponse authenticatedRequestSpec(ParamsConfig params, String path) {
        return this.authenticatedRequestSpec(params, path, "");
    }

    protected static String getTimeStamp() {
        return String.valueOf(Instant.now().toEpochMilli());
    }

    protected ValidatableResponse uploadDocument(ParamsConfig params) {
        var res = this.authenticatedRequestSpec(params, uploadDocPath);
        String docToken = res
                .extract()
                .path("document.doctoken");
        if (docToken != null) {
            DocumentDeleter.addToken(docToken);
        }

        return res;
    }

    protected ValidatableResponse createContract(ParamsConfig params) {
        var res = this.authenticatedRequestSpec(params, createContractPath);
        String conToken = res
                .extract()
                .path("token");

        if (conToken != null) {
            DocumentDeleter.addToken(conToken);
        }

        return res;
    }
}