import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends BaseSpec {

    @Step("Создание заказа с токенщм")
    public ValidatableResponse createOrderWithToken(String token, String ingredient) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token)
                .body(ingredient)
                .when()
                .post(EndPoints.ORDER_PATH)
                .then();
    }

    @Step("Создание заказа без токена")
    public ValidatableResponse createOrderWithoutToken(String ingredient) {
        return given()
                .spec(getBaseSpec())
                .body(ingredient)
                .when()
                .post(EndPoints.ORDER_PATH)
                .then();
    }

    @Step("Получение заказов пользователя с токеном")
    public ValidatableResponse getOrdersWithToken(String token) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(token)
                .when()
                .get(EndPoints.ORDER_PATH)
                .then();
    }

    @Step("Получение заказов пользователя без токена")
    public ValidatableResponse getOrdersWithoutToken() {
        return given()
                .spec(getBaseSpec())
                .when()
                .get(EndPoints.ORDER_PATH)
                .then();
    }
}