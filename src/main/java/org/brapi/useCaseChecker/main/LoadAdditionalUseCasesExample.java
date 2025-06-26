package org.brapi.useCaseChecker.main;

import org.brapi.useCaseChecker.UseCaseCheckerFactory;
import org.brapi.useCaseChecker.model.useCases.App;
import org.brapi.useCaseChecker.model.useCases.ServiceRequired;
import org.brapi.useCaseChecker.model.useCases.UseCase;

import java.util.Arrays;
import java.util.List;

public class LoadAdditionalUseCasesExample {
    public static void main(String[] args) {

        UseCaseCheckerFactory useCaseCheckerFactory = UseCaseCheckerFactory.getInstance();

        List<ServiceRequired> servicesRequired = Arrays.asList(
                new ServiceRequired.Builder()
                        .methodsRequired(Arrays.asList("GET"))
                        .serviceName("variables")
                        .versionRequired("2.1")
                        .build(),
                new ServiceRequired.Builder()
                        .methodsRequired(Arrays.asList("GET"))
                        .serviceName("observations")
                        .versionRequired("2.1")
                        .build()
        );

        List<UseCase> useCases = Arrays.asList(new UseCase.Builder()
                .useCaseName("Sync Observations")
                .servicesRequired(servicesRequired)
                .builder());

        App app = new App.Builder()
                .appName("Field Book")
                .useCases(useCases)
                .build();

        try {
            System.out.println(useCaseCheckerFactory.exportLoadedUseCasesAsJsonString());

            useCaseCheckerFactory.addAppWithUseCases(app);

            System.out.println(useCaseCheckerFactory.exportLoadedUseCasesAsJsonString());

            useCaseCheckerFactory.addUseCasesForApp("BrApp Example 2 Name", useCases);

            System.out.println(useCaseCheckerFactory.exportLoadedUseCasesAsJsonString());


        } catch (Exception e) {
            // Do whatever you want here
        }
    }
}
