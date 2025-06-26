package org.brapi.useCaseChecker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.brapi.useCaseChecker.exceptions.UseCaseCheckerException;
import org.brapi.useCaseChecker.model.Call;
import org.brapi.useCaseChecker.model.useCases.App;
import org.brapi.useCaseChecker.model.useCases.ServiceRequired;
import org.brapi.useCaseChecker.model.useCases.UseCase;
import org.brapi.useCaseChecker.model.useCases.UseCases;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UseCaseChecker {

    private final OkHttpClient httpClient;
    private final String serverInfoUrl;
    private final UseCases loadedUseCases;
    private final ObjectMapper objectMapper;
    private final static String SERVER_INFO_PATH = "/serverinfo";

    UseCaseChecker(String serverBaseUrl,
                          UseCases loadedUseCases,
                          ObjectMapper objectMapper) {
        httpClient = new OkHttpClient();
        this.objectMapper = objectMapper;
        serverInfoUrl = serverBaseUrl + SERVER_INFO_PATH;
        this.loadedUseCases = loadedUseCases;
    }

    private List<Call> getServerInfoCalls() throws UseCaseCheckerException {
        Response response;

        JsonNode serverInfo = null;
        try {
            Request request = new Request.Builder()
                    .url(serverInfoUrl)
                    .get()
                    .build();

            response = httpClient.newCall(request).execute();

            if (response.body() != null) {
                serverInfo = objectMapper.readTree(response.body().string());
            }
        } catch (IOException e) {
            // Add logging
            throw new UseCaseCheckerException(e);
        }

        if (serverInfo == null) {
            return null;
        }

        return objectMapper.convertValue(serverInfo.path("result").path("calls"),
                new TypeReference<List<Call>>() {
                });
    }

    public boolean allUseCasesCompliant(String brAppName) throws UseCaseCheckerException {
        List<Call> availableServiceCalls = getServerInfoCalls();

        Optional<App> app = loadedUseCases.getApps().stream()
                .filter(a -> a.getAppName().equals(brAppName))
                .findFirst();

        if (!app.isPresent()) {
            throw new UseCaseCheckerException(String.format("BrApp with name [%s] does not exist%n", brAppName));
        }

        for (UseCase appUseCase : app.get().getUseCases()) {
            List<ServiceRequired> servicesRequired = appUseCase.getServicesRequired();

            if (!isUseCaseValid(servicesRequired, availableServiceCalls)) {
                return false;
            }
        }

        return true;
    }

    public boolean isUseCaseCompliant(String brAppName, String useCaseName) throws UseCaseCheckerException {

        List<Call> availableServiceCalls = getServerInfoCalls();

        Optional<App> app = loadedUseCases.getApps().stream()
                .filter(a -> a.getAppName().equals(brAppName))
                .findFirst();

        if (!app.isPresent()) {
            throw new UseCaseCheckerException(String.format("BrApp with name [%s] does not exist%n", brAppName));
        }

        Optional<UseCase> useCase = app.get().getUseCases().stream()
                .filter(uc -> uc.getUseCaseName().equals(useCaseName))
                .findFirst();

        if (!useCase.isPresent()) {
            throw new UseCaseCheckerException(String.format("Use case with name [%s] does not exist for BrApp [%s]%n", useCaseName, brAppName));
        }

        List<ServiceRequired> servicesRequired = useCase.get().getServicesRequired();

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

            Call call = candidates.get(0);

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