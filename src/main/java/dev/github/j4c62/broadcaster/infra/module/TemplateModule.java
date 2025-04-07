package dev.github.j4c62.broadcaster.infra.module;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jknack.handlebars.Handlebars;
import com.github.mustachejava.DefaultMustacheFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import org.apache.velocity.VelocityContext;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

public class TemplateModule extends AbstractModule {

  private static Map<String, Object> getVariables(JsonNode variables) {
    return new ObjectMapper().convertValue(variables, new TypeReference<>() {});
  }

  @Provides
  @Singleton
  @Named("TemplateEngines")
  @SuppressWarnings("unused")
  public Map<String, NotificationTemplateEngine> provideTemplateEngines(
      @Named("Thymeleaf") NotificationTemplateEngine thymeleafEngine,
      @Named("Handlebars") NotificationTemplateEngine handlebarsEngine,
      @Named("Freemarker") NotificationTemplateEngine freemarkerEngine,
      @Named("Mustache") NotificationTemplateEngine mustacheEngine,
      @Named("Velocity") NotificationTemplateEngine velocityEngine) {

    return Map.of(
        "Thymeleaf", thymeleafEngine,
        "Handlebars", handlebarsEngine,
        "Freemarker", freemarkerEngine,
        "Mustache", mustacheEngine,
        "Velocity", velocityEngine);
  }

  @Provides
  @Named("Thymeleaf")
  @SuppressWarnings("unused")
  public NotificationTemplateEngine thymeleafEngine() {

    StringTemplateResolver templateResolver = new StringTemplateResolver();
    templateResolver.setCacheable(true);
    TemplateEngine templateEngine = new TemplateEngine();
    templateEngine.setTemplateResolver(templateResolver);

    return (template, variables, locale) ->
        new TemplateEngine().process(template, new Context(locale, getVariables(variables)));
  }

  @Provides
  @Named("Handlebars")
  @SuppressWarnings("unused")
  public NotificationTemplateEngine handlebarsEngine() {
    return (template, variables, locale) ->
        new Handlebars().compileInline(template).apply(getVariables(variables));
  }

  @Provides
  @Named("Freemarker")
  @SuppressWarnings("unused")
  public NotificationTemplateEngine freemarkerEngine() {
    return (template, variables, locale) -> {
      var cfg = new Configuration(Configuration.VERSION_2_3_31);
      var templateLoader = new StringTemplateLoader();
      templateLoader.putTemplate("inlineTemplate", template);
      cfg.setTemplateLoader(templateLoader);
      var freemarkerTemplate = cfg.getTemplate("inlineTemplate");
      var writer = new StringWriter();
      freemarkerTemplate.process(variables, writer);
      return writer.toString();
    };
  }

  @Provides
  @Named("Mustache")
  @SuppressWarnings("unused")
  public NotificationTemplateEngine mustacheEngine() {
    return (template, variables, locale) -> {
      var mustacheFactory = new DefaultMustacheFactory();
      var mustacheTemplate = mustacheFactory.compile(new StringReader(template), "inlineTemplate");
      StringWriter writer = new StringWriter();
      mustacheTemplate.execute(writer, getVariables(variables)).flush();
      return writer.toString();
    };
  }

  @Provides
  @Named("Velocity")
  @SuppressWarnings("unused")
  public NotificationTemplateEngine velocityEngine() {
    return (template, variables, locale) -> {
      var ve = new org.apache.velocity.app.VelocityEngine();
      ve.init();
      var context = new VelocityContext(getVariables(variables));
      var writer = new StringWriter();
      ve.evaluate(context, writer, "inlineTemplate", template);
      return writer.toString();
    };
  }

  public interface NotificationTemplateEngine {
    String process(String template, JsonNode variables, Locale locale)
        throws IOException, TemplateException;
  }
}
