package es.caib.distribucio.api.interna.config;

import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenApiCustomiser sortDeprecatedTagsLast() {
        return openApi -> {
            if (openApi.getTags() != null) {
                openApi.getTags().sort((t1, t2) -> {
                    boolean t1IsDeprecated = this.isDeprecated(t1.getName());
                    boolean t2IsDeprecated = this.isDeprecated(t2.getName());

                    if (t1IsDeprecated && !t2IsDeprecated) return 1;
                    if (!t1IsDeprecated && t2IsDeprecated) return -1;

                    return 0;
                });
            }
        };
    }

    private boolean isDeprecated(String name) {
        return name.startsWith("~") ||
                name.toLowerCase().contains("deprecated");
    }
}
