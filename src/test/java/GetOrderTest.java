import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GetOrderTest {
    private User user;
    private UserClient userClient;
    private OrderClient orderClient;
    private String accessToken;
    String ingredient = "{\n" + "\"ingredients\": [\"61c0c5a71d1f82001bdaaa6d\"]\n" + "}\n";

    @Before
    public void setUp() {
        user = User.getUser();
        userClient = new UserClient();
        orderClient = new OrderClient();
        ValidatableResponse createUserResponse = userClient.createUser(user);
        accessToken = createUserResponse.extract().path("accessToken");
        orderClient.createOrderWithToken(accessToken.substring(7), ingredient).statusCode(200);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userClient.deleteUser(user, accessToken.substring(7));
        }
    }

    @Test
    @DisplayName("Получение заказов авторизованного пользователя")
    @Description("Успешное получение заказов авторизованного пользователя. Запрос возвращает непустой ответ и statusCode=200")
    public void getOrdersWithAuthTest() {
        ValidatableResponse getOrderResponse = orderClient.getOrdersWithToken(accessToken.substring(7)).statusCode(200);
        getOrderResponse.assertThat().body("success", equalTo(true));
        getOrderResponse.assertThat().body("orders.number", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов неавторизованного пользователя")
    @Description("Ошибка при попытке получение заказов неавторизованного пользователя. Запрос возвращает ответ 'You should be authorised' и statusCode=401")
    public void getOrdersWithoutAuthTest() {
        ValidatableResponse getOrderResponse = orderClient.getOrdersWithoutToken().statusCode(401);
        getOrderResponse.assertThat().body("success", equalTo(false));
        getOrderResponse.assertThat().body("message", equalTo("You should be authorised"));
    }

}