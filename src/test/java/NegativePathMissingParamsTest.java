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

    private static int docId;
    private static String docOwner;
    private static String docToken;

    private static final String docData = "JVBERi0xLjQKJfbk/N8KMSAwIG9iago8PAovVHlwZSAvQ2F0YWxvZwovVmVyc2lvbiAvMS40Ci9QYWdlcyAyIDAgUgo+PgplbmRvYmoKMiAwIG9iago8PAovVHlwZSAvUGFnZXMKL0tpZHMgWzMgMCBSXQovQ291bnQgMQo+PgplbmRvYmoKMyAwIG9iago8PAovVHlwZSAvUGFnZQovTWVkaWFCb3ggWzAuMCAwLjAgMTYwMC4wIDIyMDAuMF0KL1BhcmVudCAyIDAgUgovQ29udGVudHMgNCAwIFIKL1Jlc291cmNlcyA1IDAgUgo+PgplbmRvYmoKNCAwIG9iago8PAovTGVuZ3RoIDk2NgovRmlsdGVyIC9GbGF0ZURlY29kZQo+PgpzdHJlYW0NCnicrVddb9owFL1S33iYWgEFBpUidVM3rRpJSuyg7amFAi2CQvjoqvapa6XtcZX2+3fixCHEAZwMRYlNfM/1Pff62KTZefn76/ll2rs0nt8K5lfL4swxYi1rcct4ey40V4ZX3kZD76rQvLYM2zZmr4XLWeHCNGyrZRqzn4VPdEZTuqZ7uqM+TWhJBzRHz8PbOS0/G7Pfhe5MwK1WHG6F8HPq0QlAFSpSSZqHRqbrBEYWdYR/35CJvkdj/C7TEXo9GiSBLPT+BcYLuhXA7zRESE9UxVwelejxkd4JH/fUJhf3B3pA1Hj9jUbUwisXBk36iKEy7BeIsI/pu3QMHw2MdsikGzKSk1+EUTNko4M7iNtB20Gs8H+Al1M8F4johiYSb37ljt1uGckWdbJNw2pzx7AcEzM4xp+Xwuu2ugahWG2ZwLMgkFikFo+Pn5MXG7Pb8TEXcfZpHhtvrY1zn5K35py11ifvi0wMabyeKqvthHUqI7f+fYg8+88yrgPxPEG2y0rcK5y/bqqwalAdvSJ6XUxVwnVCA/SKIv09LNGyWGkNqolfvt8arrroddB2lQzIWa5RrYl4zoENYqzB3yFwHg2VzEicK5bpVJS9Tz0lQaFdMim2o5OUYyUpElcBySNhV0dbQqjHoq1SXaEoMQsEOcaNUnbA9BbPqVifc9ynWKV3QQHhyM/DncJZOrL9pbBa0xHXcDzJ1dRaACpXicvCVWLWuJ4i2idMuATTewwtNzKU8E0MzdRqujxfNSNcBoYRJh/DCL6BoRxPMtQTcUVh6GSvYYTJydDZXkM3XZGuniKrCsMcinT3pUh3hyLddEW6eoqsKVxzKNJNVWQertIRC07UtN02svHP7gaoTGmU4M71tFpPcucrrRYRYgNtkIEa2kqSNV+pVKX6gOcEx8UUvQUID+kHyGygzROCRYzd4P/FWLgZJXMQAQaYDg5h2Ze5TeZCT9VqLlaq1snFSs//m4sd0ubp0uZ60lZp5pA2t/dWeDtr4W39wuttAGpGcmwA3Nxb+Xeczjz9dGZ6im8kybJNin8ftEmibG+aZ1k1z/Q1z/Q0r2Yjx0nO9qb8yNOWzT+yqYmPhLTNn+nsBJW1b9SAvMT51j79GugeiV/lsK0q5CVGHHgjEfdpWM+B+GR42Mh2x7HO0o91pqPqNHY5VM32dayzHaJm6aJ2dERdUb8qI1yGSkaY7JWMoFvWbWTjr9uSkO5cyHie5Kwj3TTOEpeFs8Tk4KyhVSeu1a2c9RSr/FV1cijWya/YCLqNs72rzv8Aunb6BA0";

    @Test
    @Order(1)
    void loginWith400() {
        String[] names = {"login"};
        String[][] vals = {{config.getUsername()}};
        var conf = new ParamsConfig(names, vals);
        this.defaultRequestSpec(conf, "/restServerLogin").statusCode(400).assertThat().body("error", equalTo("MISSING_PARAM")).body(matchesJsonSchemaInClasspath(LOGIN_MISSING_PARAM_ERROR_SCHEMA_JSON));
    }

    @Test
    @Order(2)
    void uploadDocumentMissingDocName() {
        String parametersStr = "type=signature|subtype=biometric|name=sig2|page=1|positionX=20|positionY=20|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true;type=signature|subtype=biometric|name=sig3|page=1|positionX=40|positionY=40|width=20|height=7|required=true|signerLogin=" + config.getSignerLogin() + "|signerfirstname=" + config.getName() + "|signersecondname=" + config.getName() + "|inperson=true|required=true";
        String[] names = {"docOwner", "applicationMode", "parameters", "docData"};
        String[][] vals = {{config.getSignerLogin()}, {"EDIT"}, {parametersStr}, {docData}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, uploadDocPath).statusCode(400).assertThat().body("error", equalTo("MISSING_PARAM")).body(matchesJsonSchemaInClasspath(UPLOAD_MISSING_PARAM_ERROR_SCHEMA_JSON));
    }

    @Test
    @Order(3)
    void createDocLink() {
        String[] names = {"docId", "docOwner", "doctoken", "showPanelRight", "showPanelTop", "showEmailsInSig", "callback", "finalizeType", "finalizeUrl", "finalizeAutoExecute", "finalizeEnablePlugin", "showPRFinalize", "showPREmail", "showPRDownload", "finalizeWebhookUrl"};
        String[][] vals = {{String.valueOf(docId)}, {docOwner}, {docToken}, {"true"}, {"true"}, {"false"}, {""}, {"redirect"}, {"http://signosoft.ais-service.cz:8080/SigndocIntegrationService/resultinfo.jsp"}, {"confirm"}, {"true"}, {"true"}, {"false"}, {"false"}, {"-"}};
        var conf = new ParamsConfig(names, vals);
        this.authenticatedRequestSpec(conf, "/REST/createDocLink").statusCode(200).assertThat().body("createDocLinkResult", equalTo("WRONG_DOC_TOKEN")).body(matchesJsonSchemaInClasspath(CREATE_DOC_LINK_ERROR_SCHEMA_JSON));
    }
}
