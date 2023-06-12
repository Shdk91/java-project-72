import hexlet.code.App;
import io.ebean.DB;
import io.ebean.Database;
import io.javalin.Javalin;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class BaseTest {

    protected static Javalin app;
    protected static String baseUrl;
    protected static Database database;
    protected static MockWebServer mockServer;

    private static String readMockPage(String fileName) throws Exception {
        Path path = Paths.get("src", "test", "resources", "template", fileName);
        return Files.readString(path);
    }

    @BeforeAll
    public static void beforeAll() throws Exception {
        app = App.getApp();
        app.start();
        int port = app.port();
        baseUrl = "http://localhost:" + port;
        database = DB.getDefault();

        mockServer = new MockWebServer();
        MockResponse mockedResponse = new MockResponse()
                .setBody(readMockPage("index.html"));
        mockServer.enqueue(mockedResponse);
        mockServer.start();
    }

    @AfterAll
    public static void afterAll() throws Exception {
        app.stop();
        mockServer.shutdown();
    }

    @AfterEach
    public final void afterEach() {
        database.script().run("/truncate.sql");
        database.script().run("/seed.sql");
    }
}
