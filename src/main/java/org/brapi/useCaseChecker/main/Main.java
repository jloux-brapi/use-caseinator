package org.brapi.useCaseChecker.main;

import org.brapi.useCaseChecker.UseCaseChecker;
import org.brapi.useCaseChecker.UseCaseCheckerFactory;
import org.brapi.useCaseChecker.exceptions.UseCaseCheckerException;

public class Main {
    public static void main(String[] args) {

        String baseUrl = null;
        String brAppName = null;
        String useCaseName = null;
        boolean all = false;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--base-url":
                    if (i+ 1 < args.length) {
                        baseUrl = args[++i];
                    }
                    break;
                case "--brapp-name":
                    if (i + 1 < args.length) {
                        brAppName = args[++i];
                    }
                    break;
                case "--all":
                    all = true;
                    break;
                case "--use-case":
                    if (i + 1 < args.length) {
                        useCaseName = args[++i];
                    }
                    break;
                default:
                    System.err.println("Unknown argument: " + args[i]);
            }
        }

        if (baseUrl == null || brAppName == null || (useCaseName == null && !all)) {
            System.err.println("Usage: --base-url <Your base-url> --brapp-name <Your BrAPP Name> --use-case <Your use case name> [--all]");
            System.err.println("A use case is required if --all option is not selected.");
            System.exit(1);
        }

        UseCaseChecker useCaseChecker = UseCaseCheckerFactory.getInstance().getUseCaseChecker(baseUrl);

        try {
            if (all) {
                System.out.println(useCaseChecker.allUseCasesCompliant(brAppName));
            } else {
                System.out.println(useCaseChecker.isUseCaseCompliant(brAppName, useCaseName));
            }
        } catch (UseCaseCheckerException e) {
            System.out.println(e.getMessage());
        }
    }
}
