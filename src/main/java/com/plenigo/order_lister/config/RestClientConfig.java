package com.plenigo.order_lister.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    private static final String X_PLENIGO_TOKEN = """
            eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJBY2Nlc3NHcm91cHMiOiJBQ0NFU1NfUklHSFRTO0FDVElWSVRJRVM7QU5BTFlUSUNTO0FQUFNUT1JFUztBQ0NPVU5USU5HUztDQUxMQkFDS1M7Q0hFQ0tPVVQ7Q1VTVE9NRVJTO0RPV05MT0FEUztJTVBPUlRTO0lOVk9JQ0VTO01BSUxTO09SREVSUztQUk9DRVNTRVM7UFJPRFVDVFM7U1RBVFVTO1NVQlNDUklQVElPTlM7VFJBTlNBQ1RJT05TO1ZPVUNIRVJTO1dBTExFVFM7U0VUVElOR1M7U0ZUUCIsIkFjY2Vzc1JpZ2h0cyI6IkFQSV9SRUFEIiwiQ29tcGFueUlkIjoiUlc4OFZPTTNGOTlNSjYyWEJRVE0iLCJJZCI6IjJqZUZ3WlpVNFdHVzVNQVU5amMwQmNhb1NUMiJ9.6WvcZ09LYr5FfA2QaT1FCoojVv4OH6BhuqfwTfxkErc""";

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl("https://api.plenigo-stage.com/api/v3.0")
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.set("X-plenigo-token",  X_PLENIGO_TOKEN);
                    httpHeaders.set("Content-Type", "application/json");
                })
                .build();
    }
}
