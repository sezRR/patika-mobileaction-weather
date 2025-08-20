package dev.sezrr.projects.patikaweatherproject.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig
{
    @Bean
    public LocaleResolver localeResolver()
    {
        CookieLocaleResolver localeResolver = new CookieLocaleResolver("PATIKA_MA_WEATHER_LOCALE");
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }
}
