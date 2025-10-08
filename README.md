# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Server Architecture Overview (Phase 2)
[Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACU12qojulVk8iUKnU9XsKDAAFUBhi3h8UKTqYplGpVJSjDpagAxJCcGCsyg8mA6SwwDmzMQ6FHAADWkoGME2SDA8QVA05MGACFVHHlKAAHmiNDzafy7gjySp6lKoDyySIVI7KjdnjAFKaUMBze11egAKKWlTYAgFT23Ur3YrmeqBJzBYbjObqYCMhbLCNQbx1A1TJXGoMh+XyNXoKFmTiYO189Q+qpelD1NA+BAIBMU+4tumqWogVXot3sgY87nae1t+7GWoKDgcTXS7QD71D+et0fj4PohQ+PUY4Cn+Kz5t7keC5er9cnvUexE7+4wp6l7FovFqXtYJ+cLtn6pavIaSpLPU+wgheertBAdZoFByyXAmlDtimGD1OEThOFmEwQZ8MDQcCyxwfECFISh+xXOgHCmF4vgBNA7CMjEIpwBG0hwAoMAADIQFkhRYcwTrUP6zRtF0vQGOo+RoFmipzGsvz-BwVygYKQH+iMykoKp+h-Ds0KPMB4lUEiMAIEJ4rTuWKkwGpOykm+hi7jS+71BR5iEHIKD2e8SqGVsLk3p5d5LsK8DxCgIAavpxoSs5AIwMcYAgPE4W8pFFlWRYqTbu5VK3vyDLoJQ-lUeggWcnOEUOlF9QKOoEBEP5xqXjAkBIdqur6vpRXtjppYURkqgAZgI0gdU-r6WsFHVYUWlJpUok4XhSlEQZnXwYh6B0U2njeH4-heCg6AxHEiTnZdgn2L4WCiYKoH1A00gRvxEbtBG3Q9HJqgKcMi37cts3wpUI3eZeS1TWZGF5S61lCY954w6DrnOsVMilfSRgoNwx6Xmje1IfVOWNZUy4yATTKGBR3Wg9lC4CqtHaWUjPZ9kNbNQ8jD2nuNk3TWzr0wFz-YrcmyCpjAuFOI2DHHcx-gouu-jYOKGr8WiMAAOJKhoz0WZJevfX99hKsD6NIehEMPLC-og7bIu+lj9TIDkBs5iTlEYzzJUNXjjJgN7ai+0t5Ms-e0V60ylYIDAls5sz+4zZ2b0jMnKAAJLSGsmzxLqKBupyazJKAaql8FSdKgAckqFwrC4LedDzH7w6Wnuh4bQsIIBnfpzUmfZ3nBdF5O23lwglfV452cN3MTcty4bdS2tMvYXLm1oA0WdKmP2oTyXU+yjP8VzztC+N83reK4xJ0BBwADsbhOCgTgxBGwRwFxABs8AJyGDDjAIom8xJuwkqWKSHQLZW2mDbdAWZr5LzttpTu0NSboDWPvOYi8UCmUdvbNy9RDz+TDhHUGawUFcgDjjIOo4k5MmfPEShZNU6RSprHeOiUjJtSQP5dcOo9S7X1D1dAHDGqIy7GWIKcw6HDjKjAMh6IKH6SjmnJqgCjyGGzn1EReiOBMnBLaXGrNpEMgPtIdukMMHKKAWHPuA8iFD39KPax694DgI2vhBwVj77K1OpYAmNlNhXSQAkMAwS+wQDCQAKQgOKfWFZ-AV3imAkoED2aSSaMyGSPRs7WywYpUY2AZ7BKgHACANkoDUKsZpcG6CiGYL9khHBZTgAVKqTUupcw86EK-C9d2MAABWiS0AUOdtgpy5TKDdOgL03O0hMadkFIo4OzDiZTLQBozhQp6hx2YLw1q7V0RCP6qIxmtt1nmMgRzGRg03JrLMeVHucwMTuN2ZTfZ+t47igAGbQHSgpY0OhHowAMNwcAKB1zZ0kYuQOFM8bMmwFodEELabQvXCAap0AADkApEpJL0QCiA8LWZcNdKijqkK4pnOUbiqABLZGcnJUPKyEsbEOy-PUMZ4onFwxcaLcG3ZeyS0aWzda28Fb0QfirLwnTwmRIVfKRAwZYDAGwGUvycY0CgONncySH0vo-T+sYe+QA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
