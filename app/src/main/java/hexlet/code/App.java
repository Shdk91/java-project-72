package hexlet.code;

import io.javalin.Javalin;
import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import io.javalin.rendering.template.JavalinThymeleaf;

public class App {

    private static boolean isProd() {
        String profile = System.getenv().getOrDefault("APP_ENV", "development");
        return profile.equals("productions");
    }

    private static TemplateEngine getTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/template/");
        templateEngine.addTemplateResolver(templateResolver);
        templateEngine.addDialect(new LayoutDialect());
        templateEngine.addDialect(new Java8TimeDialect());

        return templateEngine;
    }


    public static Javalin getApp() {
        Javalin app = Javalin.create(config -> {
//            if (!isProd()) {
//                config.plugins.enableDevLogging();
//            }
            JavalinThymeleaf.init(getTemplateEngine());
        });

        addRoutes(app);

        app.before(ctx -> {
            ctx.attribute("ctx", ctx);
        });

        return app;

    }

    public static void main(String[] args) {
        getApp().start();
    }
}
