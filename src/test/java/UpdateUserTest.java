import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;
import static org.hamcrest.CoreMatchers.equalTo;

public class UpdateUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = User.getUser();
        userClient = new UserClient();
        ValidatableResponse responseCreate = userClient.createUser(user);
        accessToken = responseCreate.extract().path("accessToken");
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(user, accessToken.substring(7));
        }
    }

    @Test
    @DisplayName("Изменение всех данных пользователя с авторизацией")
    @Description("Успешное изменение всех данных пользователя при указании токена. Проверка statusCode=200, email и name в ответе на запрос, пароля при повторной авторизации")
    public void updateAllUserDataWithAuthTest() {
        user.setEmail("test" + user.getEmail());
        user.setPassword("test" + user.getPassword());
        user.setName("test" + user.getName());

        ValidatableResponse upAllResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(200);
        upAllResponse.assertThat().body("success", equalTo(true));
        upAllResponse.assertThat().body("user." + "email", equalTo(user.getEmail().toLowerCase(Locale.ROOT)));
        upAllResponse.assertThat().body("user." + "name", equalTo(user.getName()));
        userClient.loginUser(user).statusCode(200);
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацией")
    @Description("Успешное изменение email пользователя при указании токена. Проверка statusCode=200, email в ответе на запрос")
    public void updateMailWithAuthTest() {
        user.setEmail("test" + user.getEmail());

        ValidatableResponse upMailResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(200);
        upMailResponse.assertThat().body("success", equalTo(true));
        upMailResponse.assertThat().body("user." + "email", equalTo(user.getEmail().toLowerCase(Locale.ROOT)));
    }

    @Test
    @DisplayName("Изменение пароля пользователя с авторизацией")
    @Description("Успешное изменение password пользователя при указании токена. Проверка statusCode=200, пароля при повторной авторизации")
    public void updatePasswordWithAuthTest() {
        user.setPassword("test" + user.getPassword());

        ValidatableResponse upPassResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(200);
        upPassResponse.assertThat().body("success", equalTo(true));
        userClient.loginUser(user).statusCode(200);
    }

    @Test
    @DisplayName("Изменение имени пользователя с авторизацией")
    @Description("Успешное изменение name пользователя при указании токена. Проверка statusCode=200, name в ответе на запрос")
    public void updateNameWithAuthTest() {
        user.setName("test" + user.getName());

        ValidatableResponse upNameResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(200);
        upNameResponse.assertThat().body("success", equalTo(true));
        upNameResponse.assertThat().body("user." + "name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Изменение email пользователя на email другого пользователя")
    @Description("Ошибка при попытке изменение email пользователя при указании уже существующего email, принадлежащего другому пользователю. Проверка statusCode=403, message")
    public void updateExistMailTest() {
        User userExistingEmail = User.getUser();
        userClient.createUser(userExistingEmail).statusCode(200);
        user.setEmail(userExistingEmail.getEmail());

        ValidatableResponse upMailResponse = userClient.changeUserData(accessToken.substring(7), user).statusCode(403);
        upMailResponse.assertThat().body("success", equalTo(false));
        upMailResponse.assertThat().body("message", equalTo("User with such email already exists"));
    }

    @Test
    @DisplayName("Изменение всех данных пользователя без авторизации")
    @Description("Ошибка при попытке изменение всех данных пользователя без указании токена. Проверка statusCode=401, message")
    public void updateAllUserDataWithoutAuthTest() {
        user.setEmail("test" + user.getEmail());
        user.setPassword("test" + user.getPassword());
        user.setName("test" + user.getName());


        ValidatableResponse upAllWithoutAuthResponse = userClient.changeUserDataWithoutAuth(user).statusCode(401);
        upAllWithoutAuthResponse.assertThat().body("success", equalTo(false));
        upAllWithoutAuthResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение email пользователя без авторизации")
    @Description("Ошибка при попытке изменение email пользователя без указании токена. Проверка statusCode=401, message")
    public void updateMailWithoutAuthTest() {
        user.setEmail("test" + user.getEmail());

        ValidatableResponse upMailWithoutAuthResponse = userClient.changeUserDataWithoutAuth(user).statusCode(401);
        upMailWithoutAuthResponse.assertThat().body("success", equalTo(false));
        upMailWithoutAuthResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение пароля пользователя без авторизации")
    @Description("Ошибка при попытке изменение password пользователя без указании токена. Проверка statusCode=401, message")
    public void updatePasswordWithoutAuthTest() {
        user.setPassword("test" + user.getPassword());

        ValidatableResponse upPassWithoutAuthResponse = userClient.changeUserDataWithoutAuth(user).statusCode(401);
        upPassWithoutAuthResponse.assertThat().body("success", equalTo(false));
        upPassWithoutAuthResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение имени пользователя без авторизации")
    @Description("Ошибка при попытке изменение name пользователя без указании токена. Проверка statusCode=401, message")
    public void updateNameWithoutAuthTest() {
        user.setName("test" + user.getName());

        ValidatableResponse upNameWithoutAuthResponse = userClient.changeUserDataWithoutAuth(user).statusCode(401);
        upNameWithoutAuthResponse.assertThat().body("success", equalTo(false));
        upNameWithoutAuthResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

}