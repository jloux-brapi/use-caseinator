package org.brapi.useCaseChecker.model.useCases;

import org.brapi.useCaseChecker.exceptions.UseCaseBuilderException;

import java.util.List;

public class ServiceRequired {

    public ServiceRequired() {
    }

    private ServiceRequired(Builder builder) {
        this.serviceName = builder.serviceName;
        this.methodsRequired = builder.methodsRequired;
        this.versionRequired = builder.versionRequired;
    }

    private String serviceName;
    private List<String> methodsRequired;
    private String versionRequired;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<String> getMethodsRequired() {
        return methodsRequired;
    }

    public void setMethodsRequired(List<String> methodsRequired) {
        this.methodsRequired = methodsRequired;
    }

    public String getVersionRequired() {
        return versionRequired;
    }

    public void setVersionRequired(String versionRequired) {
        this.versionRequired = versionRequired;
    }

    public static class Builder {
        private String serviceName;
        private List<String> methodsRequired;
        private String versionRequired;

        public Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Builder methodsRequired(List<String> methodsRequired) {
            this.methodsRequired = methodsRequired;
            return this;
        }

        public Builder versionRequired(String versionRequired) {
            this.versionRequired = versionRequired;
            return this;
        }

        public ServiceRequired build() {

            if (this.versionRequired == null || this.versionRequired.isEmpty()) {
                throw new UseCaseBuilderException("No versionRequired provided to builder.");
            }

            if (this.serviceName == null || this.serviceName.isEmpty()) {
                throw new UseCaseBuilderException("No serviceName provided to builder.");
            }

            if (this.methodsRequired == null || this.methodsRequired.isEmpty()) {
                throw new UseCaseBuilderException("No methodsRequired provided to builder.");
            }

            return new ServiceRequired(this);
        }
    }
}
