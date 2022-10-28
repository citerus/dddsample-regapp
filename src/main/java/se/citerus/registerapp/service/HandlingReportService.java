package se.citerus.registerapp.service;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

public class HandlingReportService {
    private final RestTemplate restTemplate;
    private final String hostname;
    private final String port;

    public HandlingReportService(RestTemplate restTemplate, String hostname, String port) {
        this.restTemplate = restTemplate;
        this.hostname = hostname;
        this.port = port;
    }

    public void submitReport(HandlingReport report) {
        String json = report.toJson();
        String url = String.format("http://%s:%s/dddsample/handlingReport", hostname, port);
        HttpHeaders headers = new HttpHeaders();
        headers.put("Content-Type", Collections.singletonList("application/json"));
        HttpEntity<String> request = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        if (response.getStatusCodeValue() == 400) {
            throw new IllegalStateException("HandlingReport returned an error: " + response.getBody());
        }
    }
}
