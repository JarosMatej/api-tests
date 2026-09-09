# API Tests

A set of API tests for Signosoft services using RestAssured and JUnit 5.

## Prerequisites

- Java 21+
- Maven 3.9+

## Setup

Copy `.env.example` to `.env` and provide the required configuration:

```bash
cp .env.example .env
```

Edit `.env` with your credentials:

```properties
SERVER=https://test.signosoft.com/api
DOC_OWNER=your_doc_owner
LOGIN=your_username
PASSWORD=your_password
SIGNER_LOGIN=your_signer_login
NAME=your_name
```

## Running Tests

### Run all tests

```bash
mvn test
```

### Run a specific test class

```bash
mvn test -Dtest=HappyPathTest
```

### Run a specific test method

```bash
mvn test -Dtest=HappyPathTest#uploadDoc
```

## Test Structure

### `src/test/java/config/` - Configuration Classes

| Class               | Purpose                                                                                                                                                                                                |
|---------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Config**          | Singleton that loads environment variables via dotenv. Provides access to server URL, credentials (username, password, signer login, name), and doc owner. Uses `.env` file for configuration.         |
| **BaseTest**        | Base class for all tests. Provides common API paths, authentication, request specifications, and helper methods for making authenticated/unauthenticated requests. Tracks document tokens for cleanup. |
| **ParamsConfig**    | Helper class for managing request parameters. Stores parameter names and values as lists, supporting both single and array values.                                                                     |
| **DocumentDeleter** | Tracks document and contract tokens created during tests. Collects tokens via `addToken()` and provides `getDocTokens()` and `serializeDocTokens()` for cleanup purposes.                              |
| **Base64Resources** | Caches and loads base64-encoded test data from resources. Provides methods like `getDocData()`, `getSigBioData()`, `getSigImgBase64()`, etc. Uses caching to avoid repeated file reads.                |

### `src/test/java/` - Test Classes

| Test Class                        | Purpose                                                                                                                                                                       |
|-----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **HappyPathTest**                 | Tests the happy path for document upload and doc link creation                                                                                                                |
| **HappyPathDocNoParamsTest**      | Happy path tests without optional parameters                                                                                                                                  |
| **UploadAndSignTest**             | Tests document upload and signing flow                                                                                                                                        |
| **UploadSignDownLoadTest**        | Tests upload, sign, and download sequence                                                                                                                                     |
| **CreateContractTest**            | Tests contract creation                                                                                                                                                       |
| **AddAttachmentTest**             | Tests attachment upload functionality                                                                                                                                         |
| **NegativePathTest**              | Tests error scenarios and negative paths                                                                                                                                      |
| **NegativePathMissingParamsTest** | Tests behavior when required parameters are missing                                                                                                                           |
| **TearDownTest**                  | **Cleanup suite** - Runs after all tests to delete documents/files created during test execution. Uses `DocumentDeleter` to collect all tokens and calls the delete endpoint. |

### `src/test/resources/` - Test Resources

#### `base64/` - Base64-encoded test data

Files containing pre-encoded data for document and signature operations:

- `docData.txt` - Document data for upload
- `docDataNegativePathMissingParams.txt` - Document data for negative path testing
- `sigBioData.txt` - Biometric signature data
- `sigImgBase64.txt` - Signature image in base64
- `dataUploadAttachment.txt` - Attachment data
- `dataUploadAttachmentInvalidData.txt` - Invalid attachment data for error testing
- `documentsCreateContract.txt` - Document data for contract creation

#### `schemas/` - JSON Schema definitions

Validation schemas for API responses. Each schema file defines the expected structure of a specific API response:

- `upload-doc-valid-schema.json` - Valid document upload response
- `upload-attachment-schema.json` - Attachment upload response
- `upload-error-schema.json` - Error response for upload operations
- `upload-missing-param-error-schema.json` - Missing parameter error for upload
- `create-contract-valid-schema.json` - Valid contract creation response
- `create-doc-link-valid-schema.json` - Valid doc link creation response
- `create-doc-link-error-schema.json` - Error response for doc link creation
- `login-valid-schema.json` - Valid login response
- `login-error-schema.json` - Login error response
- `login-wrong-schema.json` - Wrong credentials login response
- `login-missing-param-error-schema.json` - Missing parameter login error
- `sign-valid-schema.json` - Valid sign response
- `sign-error-schema.json` - Sign error response
- `sign-missing-param-schema.json` - Missing parameter sign error
- `download-doc-valid-schema.json` - Valid document download response
- `get-contract-by-token-valid-schema.json` - Valid get contract by token response

## Cleanup Mechanism

**TearDownTest** is a JUnit 5 suite that runs **after all other tests** (via `@AfterSuite`). Its purpose is to clean up
all artifacts created during test execution:

1. It collects all document and contract tokens tracked by `DocumentDeleter` during test runs
2. It serializes the tokens into the format expected by the API
3. It calls the `/REST/deleteDocument` endpoint with all collected tokens
4. This ensures no test data remains on the server after test completion

The cleanup can be used for:

- Deleting files/documents uploaded during tests
- Deleting contracts created during tests
- Any other server-side artifacts that need cleanup

**Documents and contracts are automatically subscribed for deletion** when uploaded/created using the specialized
methods in `BaseTest` (`uploadDocument()` and `createContract()`). These methods automatically extract the token from
the API response and register it with `DocumentDeleter.addToken()`, ensuring all artifacts created during testing are
tracked and will be cleaned up by `TearDownTest`.

## Dependencies

- **RestAssured** 6.0.1 - HTTP request library and response validation
- **JUnit Jupiter** 6.1.3 - Test framework
- **json-schema-validator** 6.0.1 - JSON schema validation for responses
- **dotenv-java** 3.0.0 - Environment variable loading from `.env` files
- **Jackson** 2.18.7 - JSON processing
- **commons-codec** 1.16.0 - Encoding/decoding utilities
- **commons-lang3** 3.18.0 - Apache Commons utilities
