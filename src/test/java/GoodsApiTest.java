import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("api")
public class GoodsApiTest {

    String baseUrl = "http://localhost:8080";
    String username = "admin";
    String password = "secret123";

    @Test
    @Order(1)
    void getEmptyGoodsListTest() {
        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
        .when()
                .get("/goods/list")
        .then()
                .statusCode(200)
                .body("$", empty());
    }

    @Test
    @Order(2)
    void getEmptyGoodsListWithSpecificationTest() {
        RequestSpecification request = given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password);

        request
        .when()
                .get("/goods/list")
        .then()
                .statusCode(200)
                .body("$", empty());
    }

    @Test
    @Order(3)
    void addGoodAndCheckWithRestAssuredTest() {
        String goodName = "Test good " + System.currentTimeMillis();

        String good = "{"
                + "\"name\":\"" + goodName + "\","
                + "\"price\":100"
                + "}";

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType("application/json")
                .body(good)
        .when()
                .post("/goods/add")
        .then()
                .statusCode(200);

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
        .when()
                .get("/goods/list")
        .then()
                .statusCode(200)
                .body(containsString(goodName));
    }

    @Test
    @Order(4)
    void addGoodAndCheckWithAssertJTest() {
        String goodName = "AssertJ good " + System.currentTimeMillis();

        String good = "{"
                + "\"name\":\"" + goodName + "\","
                + "\"price\":200"
                + "}";

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType("application/json")
                .body(good)
        .when()
                .post("/goods/add")
        .then()
                .statusCode(200);

        Response response = given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
        .when()
                .get("/goods/list");

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.asString()).contains(goodName);
    }

    @Test
    @Order(5)
    void addGoodStatus200Test() {
        String goodName = "New good " + System.currentTimeMillis();

        String good = "{"
                + "\"name\":\"" + goodName + "\","
                + "\"price\":300"
                + "}";

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType("application/json")
                .body(good)
        .when()
                .post("/goods/add")
        .then()
                .statusCode(200);
    }

    @Test
    @Order(6)
    void addGoodStatus400Test() {
        String wrongGood = "{"
                + "\"price\":100"
                + "}";

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType("application/json")
                .body(wrongGood)
        .when()
                .post("/goods/add")
        .then()
                .statusCode(400);
    }

    @Test
    @Order(7)
    void getGoodByIdStatus200Test() {
        long id = addGood("Good for get", 400);

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
        .when()
                .get("/goods/" + id)
        .then()
                .statusCode(200);
    }

    @Test
    @Order(8)
    void getGoodByIdStatus404Test() {
        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
        .when()
                .get("/goods/999999999")
        .then()
                .statusCode(404);
    }

    @Test
    @Order(9)
    void updateGoodStatus200Test() {
        long id = addGood("Good for patch", 500);

        String updatedGood = "{"
                + "\"name\":\"Updated good " + System.currentTimeMillis() + "\","
                + "\"price\":600"
                + "}";

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType("application/json")
                .body(updatedGood)
        .when()
                .patch("/goods/" + id)
        .then()
                .statusCode(200);
    }

    @Test
    @Order(10)
    void updateGoodStatus400Test() {
        long id = addGood("Good for bad patch", 500);

        String wrongGood = "{"
                + "\"name\":\"Wrong price\","
                + "\"price\":-100"
                + "}";

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType("application/json")
                .body(wrongGood)
        .when()
                .patch("/goods/" + id)
        .then()
                .statusCode(400);
    }

    @Test
    @Order(11)
    void updateGoodStatus404Test() {
        String good = "{"
                + "\"name\":\"Not found good\","
                + "\"price\":100"
                + "}";

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType("application/json")
                .body(good)
        .when()
                .patch("/goods/999999999")
        .then()
                .statusCode(404);
    }

    @Test
    @Order(12)
    void deleteGoodStatus200Test() {
        long id = addGood("Good for delete", 700);

        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
        .when()
                .delete("/goods/" + id)
        .then()
                .statusCode(200);
    }

    @Test
    @Order(13)
    void deleteGoodStatus404Test() {
        given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
        .when()
                .delete("/goods/999999999")
        .then()
                .statusCode(404);
    }

    private long addGood(String name, int price) {
        String goodName = name + " " + System.currentTimeMillis();

        String good = "{"
                + "\"name\":\"" + goodName + "\","
                + "\"price\":" + price
                + "}";

        Response response = given()
                .baseUri(baseUrl)
                .auth()
                .preemptive()
                .basic(username, password)
                .contentType("application/json")
                .body(good)
        .when()
                .post("/goods/add");

        assertThat(response.statusCode()).isEqualTo(200);

        return response.jsonPath().getLong("data.id");
    }
}
