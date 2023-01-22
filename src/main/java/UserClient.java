import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;

public class UserClient extends BaseSpec {
    @Step("Создание пользователя")
    public ValidatableResponse createUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(EndPoints.CREATE_USER_PATH)
                .then();
    }

    @Step("Удаление пользователя")
    public void deleteUser(User user, String accessToken) {
        given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .when()
                .delete(EndPoints.USER_PATH)
                .then()
                .assertThat()
                .statusCode(SC_ACCEPTED);
    }

    @Step("Логин пользователя")
    public ValidatableResponse loginUser(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .post(EndPoints.LOGIN_PATH)
                .then();
    }

    @Step("Изменение данных пользователя с отправкой токена")
    public ValidatableResponse changeUserData(String accessToken, User user) {
        return given()
                .spec(getBaseSpec())
                .auth().oauth2(accessToken)
                .and()
                .body(user)
                .when()
                .patch(EndPoints.USER_PATH)
                .then();
    }

    @Step("Изменение данных пользователя без отправки токена")
    public ValidatableResponse changeUserDataWithoutAuth(User user) {
        return given()
                .spec(getBaseSpec())
                .body(user)
                .when()
                .patch(EndPoints.USER_PATH)
                .then();
    }
}