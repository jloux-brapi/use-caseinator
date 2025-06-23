package org.brapi.useCaseChecker.model;

import java.util.List;

public class ServiceRequired {
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
}
