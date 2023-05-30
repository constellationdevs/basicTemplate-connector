# Getting Started: Basic Connector Project

## Setting up your developer environment: 
- IDE - Visual Studio Code: [Install Visual Studio Code](https://code.visualstudio.com)
    - Visual Studio Code Plugins: 
        - [Docker - Docker Plugin](https://marketplace.visualstudio.com/items?itemName=PeterJausovec.vscode-docker)
            - Requires: [Docker Install](https://docs.docker.com/install/)
            - Tutorial: [Vs Docker Tutorial](https://code.visualstudio.com/docs/java/java-container)
        - Java Tools - Can all be installed via the [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
          - [Maven - Maven for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-maven)
          - [Java - Language Support for Java toolkit](https://marketplace.visualstudio.com/items?itemName=redhat.java)
          - [Java - Java Debugging](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-debug)
        - SpringBoot Support [Springboot Extension Pack](https://marketplace.visualstudio.com/items?itemName=Pivotal.vscode-boot-dev-pack)
- Docker: [Install Docker](https://docs.docker.com/install/)
- Java: [Java Install](https://www.java.com/en/download/help/download_options.xml)
- Maven: [Install Maven](https://maven.apache.org/download.cgi)


## Running connector locally
- Build Connector - 
  - Download and install the "ServiceCommon" project from the developers portal documents and resources.
   - Running connector locally (cdp.wiki/en/Connectors/Dependencies)
  - Open Terminal/Command Prompt: 
  - Navigate to the root of your connector project(directory with the pom.xml and src folder)
  - Enter the following commands: 
    ``` 
        "mvn -version" -- Confirms maven is installed 
        "mvn install" -- Builds project, installs dependencies to local repo

    ```

## Spring profiles
Spring Profiles are used to activate different implementations of ConnectorLogging Beans. If you choose to use VS Code as your IDE, a lauch.json file is included that will make use of the "local" profile to print logs to the IDE output console.
