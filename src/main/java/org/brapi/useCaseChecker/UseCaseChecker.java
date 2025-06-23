package org.brapi.useCaseChecker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.brapi.useCaseChecker.model.Call;
import org.brapi.useCaseChecker.model.ServiceRequired;
import org.brapi.useCaseChecker.util.JsonNodeBodyHandler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UseCaseChecker {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String serverInfoUrl;

    private final static String SERVER_INFO_PATH = "/v2/serverinfo";

    public UseCaseChecker(String serverBaseUrl) {
        httpClient = HttpClient.newHttpClient();
        objectMapper = new ObjectMapper();
        serverInfoUrl = serverBaseUrl + SERVER_INFO_PATH;
    }

    private List<Call> getServerInfoCalls() {
        HttpResponse<JsonNode> response = null;


        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(serverInfoUrl))
                    .GET()
                    .build();


            response = httpClient.send(request, new JsonNodeBodyHandler(objectMapper));
        } catch (Exception e) {
            // Add logging
            System.out.println(e.getMessage());
            return null;
        }

        JsonNode serverInfo = objectMapper.valueToTree(response.body());

        return objectMapper.convertValue(serverInfo.path("result").path("calls"),
                new TypeReference<>() {
                });
    }

    private JsonNode loadUseCases() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("useCases.json");

        JsonNode useCases = null;

        try {
            useCases = objectMapper.readTree(is);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return useCases;
    }

    public boolean allUseCasesCompliant(String brAppName) {
        List<Call> availableServiceCalls = getServerInfoCalls();

        JsonNode useCases = loadUseCases();

        var appUseCases = useCases.path(brAppName);

        if (appUseCases == null || appUseCases.isEmpty()) {
            System.out.printf("BrApp with name [%s] does not exist%n", brAppName);
            return false;
            // Return error
        }

        Iterator<JsonNode> useCasesIter = appUseCases.elements();

        while (useCasesIter.hasNext()) {
            var appUseCase = useCasesIter.next();

            List<ServiceRequired> servicesRequired = objectMapper.convertValue(appUseCase.path("servicesRequired"),
                    new TypeReference<>() {
                    });

            if (!isUseCaseValid(servicesRequired, availableServiceCalls)) {
                return false;
            }
        }

        return true;
    }

    public boolean isUseCaseCompliant(String brAppName, String useCaseName) {

        List<Call> availableServiceCalls = getServerInfoCalls();

        JsonNode useCases = loadUseCases();

        var appUseCases = useCases.path(brAppName);

        if (appUseCases == null || appUseCases.isEmpty()) {
            System.out.printf("BrApp with name [%s] does not exist%n", brAppName);
            return false;
            // Return error
        }

        var appUseCase = appUseCases.path(useCaseName);

        if (appUseCase == null || appUseCases.isEmpty()) {
            System.out.printf("Use case with name [%s] does not exist for BrApp [%s]%n", useCaseName, brAppName);
            return false;
        }

        List<ServiceRequired> servicesRequired = objectMapper.convertValue(appUseCase.path("servicesRequired"),
                new TypeReference<>() {
                });

        return isUseCaseValid(servicesRequired, availableServiceCalls);
    }

    private boolean isUseCaseValid(List<ServiceRequired> servicesRequired, List<Call> availableServiceCalls) {
        Map<String, List<Call>> callsByService = availableServiceCalls.stream()
                .collect(Collectors.groupingBy(Call::getService));

        for (ServiceRequired serviceRequired : servicesRequired) {
            List<Call> candidates = callsByService.get(serviceRequired.getServiceName());

            if (candidates == null  || candidates.isEmpty()) {
                System.out.printf("Service [%s] not found in BrAPI compatible server with serverInfo endpoint: [%s]",
                        serviceRequired.getServiceName(),
                        serverInfoUrl
                        );
                return false;
            }

            var call = candidates.getFirst();

            if (!call.getVersions().contains(serviceRequired.getVersionRequired())) {
                System.out.printf("Service [%s] did not have a compatible version in BrAPI compatible server with serverInfo endpoint: [%s]",
                        serviceRequired.getServiceName(),
                        serverInfoUrl
                );
                return false;
            }

            if (!call.getMethods().containsAll(serviceRequired.getMethodsRequired())) {
                System.out.printf("Service [%s] did not have a compatible HTTP Verb in BrAPI compatible server with serverInfo endpoint: [%s]",
                        serviceRequired.getServiceName(),
                        serverInfoUrl
                );
                return false;
            }
        }

        return true;
    }
}