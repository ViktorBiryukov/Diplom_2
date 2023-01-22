import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.Description;

import static org.hamcrest.CoreMatchers.equalTo;

public class LoginUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = User.getUser();
        userClient = new UserClient();
        userClient.createUser(user);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(user, accessToken.substring(7));
        }
    }

    @Test
    @DisplayName("Авторизация под существующим пользователем с корректными данными")
    @Description("Успешная авторизация под существующим пользователем. Запрос возвращает statusCode=200")
    public void loginWithValidCredTest() {
        ValidatableResponse loginResponse = userClient.loginUser(user).statusCode(200);
        loginResponse.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Авторизация при указании некорректного email")
    @Description("Ошибка при попытке авторизация при указании некорректного email. Запрос возвращает ответ 'email or password are incorrect' и statusCode=401")
    public void loginIncorrectEmailTest() {
        user.setEmail(user.getEmail() + "test");
        ValidatableResponse loginIncEmailResponse = userClient.loginUser(user).statusCode(401);
        loginIncEmailResponse.assertThat().body("success", equalTo(false));
        loginIncEmailResponse.assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация при указании некорректного пароля")
    @Description("Ошибка при попытке авторизация при указании некорректного password. Запрос возвращает ответ 'email or password are incorrect' и statusCode=401")
    public void loginIncorrectPasswordTest() {
        user.setPassword(user.getPassword() + "test");
        ValidatableResponse loginIncPasswordResponse = userClient.loginUser(user).statusCode(401);
        loginIncPasswordResponse.assertThat().body("success", equalTo(false));
        loginIncPasswordResponse.assertThat().body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация при указании некорректных данных для входа")
    @Description("Ошибка при попытке авторизация при указании некорректного email и password. Запрос возвращает ответ 'email or password are incorrect' и statusCode=401")
    public void loginWithIncorrectCredTest() {
        user.setEmail(user.getEmail() + "test");
        user.setPassword(user.getPassword() + "test");
        ValidatableResponse loginIncCredResponse = userClient.loginUser(user).statusCode(401);
        loginIncCredResponse.assertThat().body("success", equalTo(false));
        loginIncCredResponse.assertThat().body("message", equalTo("email or password are incorrect"));
    }
}