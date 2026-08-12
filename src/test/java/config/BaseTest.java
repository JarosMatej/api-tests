package config;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class BaseTest {
    protected String token;
    protected static String baseUri = "https://test.signosoft.com/api";

    protected static String loginPath = "/restServerLogin";

    public ValidatableResponse defaultRequestSpec(String body, String path) {
        return given()
                .baseUri(baseUri).log().all().body(body).when().post(path)
                .then().log().all();
    }

    public ValidatableResponse authenticatedRequestSpec(String body, String path) {
        return given()
                .baseUri(baseUri).header("Authorization", "Bearer " + this.token).log().all().body(body).when().post(path)
                .then().log().all();
    }
}