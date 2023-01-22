import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;

public class CreateUserTest {
    private User user;
    private UserClient userClient;
    private String accessToken;

    @Before
    public void setUp() {
        user = User.getUser();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        if(accessToken != null) {
            userClient.deleteUser(user, accessToken.substring(7));
        }
    }

    @Test
    @DisplayName("Создание пользователя с корректными данными")
    @Description("Успешное создание пользователя с указанием корректных данных. Запрос возвращает statusCode=200")
    public void  successCreatingUserTest(){
        ValidatableResponse createResponse = userClient.createUser(user).statusCode(200);
        createResponse.assertThat().body("success", equalTo(true));
    }

    @Test
    @DisplayName("Повторное создание пользователя")
    @Description("Ошибка при попытке повторно создать пользователям с уже используемыми другим пользователем данными. Запрос возвращает ответ 'User already exists' и statusCode=403")
    public void  creatingIdenticalUserTest() {
        ValidatableResponse createUserResponse = userClient.createUser(user).statusCode(200);
        ValidatableResponse createIdenticalUserResponse = userClient.createUser(user).statusCode(403);
        createIdenticalUserResponse.assertThat().body("success", equalTo(false));
        createIdenticalUserResponse.assertThat().body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Ошибка при попытке создание пользователя без указания email. Запрос возвращает ответ 'Email, password and name are required fields' и statusCode=403")
    public void createUserWithoutEmailTest(){
        user.setEmail("");
        ValidatableResponse createUserWithoutEmailResponse = userClient.createUser(user).statusCode(403);
        createUserWithoutEmailResponse.assertThat().body("success", equalTo(false));
        createUserWithoutEmailResponse.assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Ошибка при попытке создание пользователя без указания password. Запрос возвращает ответ 'Email, password and name are required fields' и statusCode=403")
    public void createUserWithoutPasswordTest(){
        user.setPassword("");
        ValidatableResponse createUserWithoutPasswordResponse = userClient.createUser(user).statusCode(403);
        createUserWithoutPasswordResponse.assertThat().body("success", equalTo(false));
        createUserWithoutPasswordResponse.assertThat().body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Ошибка при попытке создание пользователя без указания name. Запрос возвращает ответ 'Email, password and name are required fields' и statusCode=403")
    public void createUserWithoutNameTest() {
        user.setName("");
        ValidatableResponse createUserWithoutNameResponse = userClient.createUser(user).statusCode(403);
        createUserWithoutNameResponse.assertThat().body("success", equalTo(false));
        createUserWithoutNameResponse.assertThat().body("message", equalTo("Email, password and name are required fields"));
    }
}