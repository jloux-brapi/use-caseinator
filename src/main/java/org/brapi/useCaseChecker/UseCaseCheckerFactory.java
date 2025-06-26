package org.brapi.useCaseChecker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.brapi.useCaseChecker.exceptions.UseCaseAdditionException;
import org.brapi.useCaseChecker.model.useCases.App;
import org.brapi.useCaseChecker.model.useCases.UseCase;
import org.brapi.useCaseChecker.model.useCases.UseCases;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

public class UseCaseCheckerFactory {

    private static UseCaseCheckerFactory instance;
    private final ObjectMapper objectMapper;
    private UseCases loadedUseCases;

    private UseCaseCheckerFactory() {
        objectMapper = new ObjectMapper();
        loadedUseCases = loadUseCases();
    }

    private UseCases loadUseCases() {
        InputStream is = getClass().getClassLoader().getResourceAsStream("useCases.json");

        UseCases useCases = null;

        try {
            useCases = objectMapper.readValue(is, UseCases.class);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return useCases;
    }

    public static UseCaseCheckerFactory getInstance() {
        if (instance == null) {
            instance = new UseCaseCheckerFactory();
        }
        return instance;
    }

    public UseCaseChecker getUseCaseChecker(String baseUrl) {
        return new UseCaseChecker(baseUrl, loadedUseCases, objectMapper);
    }

    public void addUseCasesForApp(String appName, List<UseCase> useCasesToAdd) throws UseCaseAdditionException {
        Optional<App> app = loadedUseCases.getApps().stream().filter(a -> a.getAppName().equals(appName)).findFirst();

        if (!app.isPresent()) {
            throw new UseCaseAdditionException(String.format("Unable to add use case to app with name [%s] because no such app exists with that name", appName));
        }

        app.get().getUseCases().addAll(useCasesToAdd);
    }

    public void addAppWithUseCases(App app) throws UseCaseAdditionException {
        if (app.getUseCases() == null || app.getUseCases().isEmpty()) {
            throw new UseCaseAdditionException("No use cases added to submitted app");
        }

        if (app.getAppName() == null || app.getAppName().isEmpty()) {
            throw new UseCaseAdditionException("No app name in submitted app");
        }

        loadedUseCases.getApps().add(app);
    }

    public String exportLoadedUseCasesAsJsonString() throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(loadedUseCases);
    }

}
