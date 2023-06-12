import hexlet.code.entities.Url;
import hexlet.code.entities.UrlCheck;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestMethodOrder(OrderAnnotation.class)
public class UrlControllerTest extends BaseTest {

    @Test
    @Order(1)
    void testListUrls() {
        HttpResponse<String> response = Unirest
                .get(BaseTest.baseUrl + "/urls")
                .asString();
        assertEquals(200, response.getStatus());
    }

    @Test
    void testShowUrl() {
        HttpResponse<String> response = Unirest
                .get(BaseTest.baseUrl + "/urls/" + 1)
                .asString();
        assertEquals(200, response.getStatus());
    }

    @Test
    void testCreateUrl() {
        String inputUrl = "https://github.com";
        HttpResponse responsePost = Unirest
                .post(baseUrl + "/urls")
                .field("url", inputUrl)
                .asEmpty();

        assertEquals(302, responsePost.getStatus());

        Url actualUrl = getUrlByName(inputUrl);
        assertEquals(inputUrl, actualUrl.getName());
    }

    @Test
    void testCheckUrl() {
        String inputUrl = mockServer.url("/").toString().replaceAll("/$", "");

        Unirest.post(baseUrl + "/urls").field("url", inputUrl).asEmpty();

        Url actualUrl = getUrlByName(inputUrl);
        assertEquals(inputUrl, actualUrl.getName());

        Unirest.post(baseUrl + "/urls/" + actualUrl.getId() + "/checks").asEmpty();

        UrlCheck actualCheckUrl = getUrlCheckByUrl(actualUrl.getId());

        assertEquals(200, actualCheckUrl.getStatusCode());
        assertEquals("Test page", actualCheckUrl.getTitle());
        assertEquals("test test test", actualCheckUrl.getH1());
        assertEquals("test test", actualCheckUrl.getDescription());
    }

    //Почему-то в тестах не работает обычный способ выполнения запросов сыпет NPE, думаю из-за проксей,
    //Мб что-то в окружении не дает корректно отработать
    //Но как исправить не нашел, поэтому тут нативные запросы
    private static Url getUrlByName(String name) {
        String sql = "select id, name, created_at  from url where name = ?";
        return database.findNative(Url.class, sql).setParameter(1, name).findOne();
    }

    private static UrlCheck getUrlCheckByUrl(Long urlId) {
        String sql = "select id, status_code, title, h1, description, url_id,"
                + " created_at from url_check where url_id = ?";
        return database.findNative(UrlCheck.class, sql).setParameter(1, urlId).findOne();
    }
}
