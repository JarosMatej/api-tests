package config;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import java.time.Instant;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BaseTest {

    protected static Config config = Config.getInstance();
    protected static String token;
    protected static String baseUri = config.getServer();

    protected static String loginPath = "/restLogin";

    protected static String uploadDocPath = "/REST/uploadDocument";
    protected static final String UPLOAD_DOC_VALID_SCHEMA_JSON = "schemas/upload-doc-valid-schema.json";
    protected static final String CREATE_DOC_LINK_VALID_SCHEMA_JSON = "schemas/create-doc-link-valid-schema.json";


    private static RequestSpecification baseGiven() {
        return given().baseUri(baseUri).log().all();
    }

    private static RequestSpecification baseAuthenticatedGiven() {
        return baseGiven().header("Authorization", "Bearer " + token);
    }

    private static ValidatableResponse baseThen(Response spec) {
        return spec.then().log().all();
    }

    private void authenticate() {
        if (token != null) {
            return;
        }

        String[] names = {"login", "password"};
        String[][] vals = {{config.getUsername()}, {config.getPassword()}};
        var conf = new ParamsConfig(names, vals);
        token = this.defaultRequestSpec(conf, loginPath).statusCode(200).assertThat().body("result", equalTo("OK")).extract().path("token");
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

        return baseThen(spec.body(body).when().post(path));
    }

    public ValidatableResponse defaultRequestSpec(ParamsConfig params, String path) {
        return this.defaultRequestSpec(params, path, "");
    }

    public ValidatableResponse authenticatedRequestSpec(ParamsConfig params, String path, String body) {
        this.authenticate();

        var spec = this.setParams(params, baseAuthenticatedGiven());

        if (body.isEmpty()) {
            return baseThen(spec.when().post(path));
        }

        return baseThen(spec.body(body).when().post(path));
    }

    public ValidatableResponse authenticatedRequestSpec(ParamsConfig params, String path) {
        return this.authenticatedRequestSpec(params, path, "");
    }

    protected static String getTimeStamp() {
        return String.valueOf(Instant.now().toEpochMilli());
    }

    protected ValidatableResponse uploadDocument(ParamsConfig params) {
        var res = this.authenticatedRequestSpec(params, uploadDocPath);
        String docToken = res.extract().path("document.doctoken");
        DocumentDeleter.addToken(docToken);

        return res;
    }
}