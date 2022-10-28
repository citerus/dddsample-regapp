package se.citerus.registerapp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import se.citerus.registerapp.service.HandlingReportService;

@Configuration
public class RegisterAppConfiguration {

    @Value("${sampleapp.hostname}")
    private String hostname;

    @Value("${sampleapp.port}")
    private String port;

    @Value("${debugUI}")
    private Boolean debugUI;

    @Bean
    public RegisterApp registerApp(HandlingReportService handlingReportService) {
        return new RegisterApp(handlingReportService, debugUI);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public HandlingReportService handlingReportService(RestTemplate restTemplate) {
        return new HandlingReportService(restTemplate, hostname, port);
    }
}
