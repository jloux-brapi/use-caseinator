package org.brapi.useCaseChecker.model.useCases;

import org.brapi.useCaseChecker.exceptions.UseCaseBuilderException;

import java.util.List;

public class App {

    public App() {
    }

    private App(Builder builder) {
        this.appName = builder.appName;
        this.useCases = builder.useCases;
    }

    List<UseCase> useCases;
    String appName;

    public List<UseCase> getUseCases() {
        return useCases;
    }

    public void setUseCases(List<UseCase> useCases) {
        this.useCases = useCases;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public static class Builder {
        private String appName;
        private List<UseCase> useCases;

        public Builder appName(String appName) {
            this.appName = appName;
            return this;
        }

        public Builder useCases(List<UseCase> useCases) {
            this.useCases = useCases;
            return this;
        }

        public App build() {

            if (this.appName == null || this.appName.isEmpty()) {
                throw new UseCaseBuilderException("No appName provided to builder");
            }

            if (this.useCases == null || this.useCases.isEmpty()) {
                throw new UseCaseBuilderException("No useCases provided to builder");
            }

            return new App(this);
        }
    }
}
