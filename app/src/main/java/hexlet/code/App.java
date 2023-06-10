package hexlet.code;

import io.javalin.Javalin;

public class App {

    private static boolean isProd() {
        String profile = System.getenv().getOrDefault("APP_ENV", "development");
        return profile.equals("productions");
    }

    public static Javalin getApp() {
        Javalin app = Javalin.create();
        return app;
    }

    public static void main(String[] args) {
        getApp().start();
    }
}
