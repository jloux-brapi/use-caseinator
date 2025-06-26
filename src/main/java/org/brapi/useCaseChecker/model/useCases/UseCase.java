package org.brapi.useCaseChecker.model.useCases;

import org.brapi.useCaseChecker.exceptions.UseCaseBuilderException;

import java.util.List;

public class UseCase {

    public UseCase() {

    }

    private UseCase(Builder builder) {
        this.useCaseName = builder.useCaseName;
        this.servicesRequired = builder.servicesRequired;
    }

    private String useCaseName;
    private List<ServiceRequired> servicesRequired;

    public String getUseCaseName() {
        return useCaseName;
    }

    public void setUseCaseName(String useCaseName) {
        this.useCaseName = useCaseName;
    }

    public List<ServiceRequired> getServicesRequired() {
        return servicesRequired;
    }

    public void setServicesRequired(List<ServiceRequired> servicesRequired) {
        this.servicesRequired = servicesRequired;
    }

    public static class Builder {
        private String useCaseName;
        private List<ServiceRequired> servicesRequired;

        public Builder useCaseName(String useCaseName) {
            this.useCaseName = useCaseName;
            return this;
        }

        public Builder servicesRequired(List<ServiceRequired> servicesRequired) {
            this.servicesRequired = servicesRequired;
            return this;
        }

        public UseCase builder() {

            if (this.useCaseName == null || this.useCaseName.isEmpty()) {
                throw new UseCaseBuilderException("No useCaseName provided to builder.");
            }

            if (this.servicesRequired == null || this.servicesRequired.isEmpty()) {
                throw new UseCaseBuilderException("No servicesRequired provided to builder.");
            }

            return new UseCase(this);
        }
    }
}
