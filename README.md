# Br-API Use-Caseinator
A tool to check if Br-API compatible servers have implemented methods required for Br-App use cases.

# Usages
You can utilize this tool in the following ways:
* Executable jar with arguments
* Import java library via Maven

# Prerequisites
* Java 8+

# Basic Functionality
The program loads static use cases from `src/main/resources/useCases.json`

We recommend the community creates MRs to add their use cases to this static resource in the library, but more on that later.

Users of the program submit a single base URL for the server, including the version, if applicable, like:

`http://localhost:8083/brapi/v2`

Users can then provide the app name and the use case name that they want to test, or test all the use cases of an app.

Upon testing the program will return true or false if the server at the base url provided is compatible with the use case(s) tested.

It does this by checking if the use cases loaded into the program are implemented and documented on the server's `/serverinfo` url.

This url **must** be up-to-date with the implemented methods for the server you are testing, and it must appear in a format with this schema:

```json
{
  "calls" : [
    {
      "service" : "serviceExample1",
      "versions" : ["2.1", "2.0"],
      "methods" : ["GET", "PUT"]
    },
    {
      "service": "serviceExample2",
      "versions": [
        "2.1",
        "2.0"
      ],
      "methods": [
        "GET",
        "PUT"
      ]
    }
  ]
}
```

The program checks if the service exists, if the methods exist required for that service, and if the versions required by the use case are supported.

## How to use
### Executable jar
An executable jar file is provided on release to this GitHub repo [here]()

Once you have downloaded it, run:
`java  -jar <nameOfLatestJar.jar> --base-url "<url>" --brapp-name "<app name>" --use-case "<use case name>"`

This will either return a true or false, provided the base url is valid, and the app name and use case name of the app is loaded
and exists in the program.

Additionally, there is a `--all` option to check all use cases for a given app at once.

For this usage, try something like:

`java  -jar <nameOfLatestJar.jar> --base-url "<url>" --brapp-name "<app name>" --all`

### Import into Java Application
This program has been loaded into Maven Central, you can grab it by adding the following dependency to your `pom.xml`:

```
Example here
```

The library and `UseCaseChecker` is managed through the `UseCaseCheckerFactory` class.


#### Example Usage
Here is some example usage with some existing example data found in the `useCases.json` file:

You will need to supply your own server base url that has the implemented methods, or this will result in exceptions
or always return `false`

```
        UseCaseChecker useCaseChecker = UseCaseCheckerFactory.getInstance().getUseCaseChecker("http://localhost:8083/brapi/v2");

        try {
            System.out.println(useCaseChecker.isUseCaseCompliant("BrApp Example Name", "Import Trials"));
        } catch (UseCaseCheckerException e) {
            System.out.println(e.getMessage());
        }
```

Additionally, like the executable, you can test if all use cases at once are implemented for the app:

```
        UseCaseChecker useCaseChecker = UseCaseCheckerFactory.getInstance().getUseCaseChecker("http://localhost:8083/brapi/v2");

        try {
            System.out.println(useCaseChecker.allUseCasesCompliant("BrApp Example Name"));
        } catch (UseCaseCheckerException e) {
            System.out.println(e.getMessage());
        }
```

Additionally, check out the `Main` class for another example usage.

#### Testing New Use Cases
Additional functionality exists to test use cases that have not been submitted for peer review to be added to the
statically loaded `useCases.json` resource the library has access to.

We recommend utilizing this functionality to build the json you need to create an MR for review, if you would like your
use cases to be added.

The `UseCaseCheckerFactory` comes equipped with two methods to add additional use cases to the program at runtime:
* `addUseCasesForApp(String appName, List<UseCase> useCasesToAdd)`:  Adds a list of use cases to an already existing app that has been loaded, either via the static file or:
* `addAppWithUseCases(App app)`: Adds an app with a list of use cases to the list of available apps that can have their use cases checked.

Utilize the data model builders to create new use cases and apps to add to the available apps and use cases to test.

Once you are satisfied with your testing, there is also an export method inside of `UseCaseCheckerFactory` which you can utilize
to get a full JSON export of the all the currently loaded apps and use cases, `exportLoadedUseCasesAsJsonString()`

When you create an issue to add your use cases you should utilize this method to get the new JSON that should be placed into `src/main/resources/useCases.json` 

There is an example class, `LoadAdditionalUseCasesExample` that details the full usages of all of these features.

Happy compliance testing! 