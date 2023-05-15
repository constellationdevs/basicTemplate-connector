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
  - Open Terminal/Command Prompt: 
  - Navigate to the root of your connector project(directory with the pom.xml and src folder)
  - Enter the following commands: 
    ``` 
        "mvn -version" -- Confirms maven is installed 
        "mvn install" -- Builds project, installs dependencies to local repo

    ```
    ![picture](Reference/image.png)
   ```
    {
	    "method": "POST",
	    "url": "http://docker.prodadp.cdp-web.com:8080/externalConnector/demoatmfinder/1.0/getatmlist",
	    "json": true,
	    "headers": {
	        "content-type": "application/json"
	    },
	    "body": {
	        "externalServicePayload": {
	            "requestType": {
	                "connector": "demoatmfinder",
	                "version": "1.0",
	                "method": "getatmlist"
	            },
	            "payload": {
	                "valuePair": [{
	                    "name": "httpMethod",
	                    "value": "GET"
	                }, {
	                    "name": "resourceUrl",
	                    "value": "/locator/proximitysearch"
	                }]
	            }
	        },
	        "connectorParametersResponse": {
	            "parameters": {
	                "valuePair": [{
	                    "name": "org",
	                    "value": "73876E4A-2C06-4612-8BC5-12887895AB87"
	                }, {
	                    "name": "userId",
	                    "value": ""
	                }, {
	                    "name": "tileCode",
	                    "value": "8B1E042E-CAC0-434B-991C-FC25C1E07BAC"
	                }, {
	                    "name": "latitude",
	                    "value": "35.83253357394019"
	                }, {
	                    "name": "longitude",
	                    "value": "-78.62223171161654"
	                }, {
	                    "name": "maxRadius",
	                    "value": "20"
	                }, {
	                    "name": "connectorName",
	                    "value": "demoatmfinder"
	                }, {
	                    "name": "connectorVersion",
	                    "value": "1.0"
	                }, {
	                    "name": "connectorMethod",
	                    "value": "getatmlist"
	                }, {
	                    "name": "url",
	                    "value": "https://api.co-opfs.org"
	                }, {
	                    "name": "authorization",
	                    "value": "hurY2KqGC3d8BRb"
	                }, {
	                    "name": "demo",
	                    "value": "false"
	                }, {
	                    "name": "version",
	                    "value": "1"
	                }]
	            },
	            "method": {
	                "parameters": {
	                    "valuePair": []
	                },
	                "isValid": true
	            },
	            "connectorController": ""
	        },
	        "response": "",
	        "responseStatus": {
	            "statusCode": "",
	            "statusDescription": "",
	            "status": "",
	            "statusReason": "",
	            "requiredFields": []
	        }
	    }
	}
    ```