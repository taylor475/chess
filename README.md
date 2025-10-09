# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Server Architecture Overview (Phase 2)
[Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2AMQALADMABwATG4gMP7I9gAWYDoIPoYASij2SKoWckgQaJiIqKQAtAB85JQ0UABcMADaAAoA8mQAKgC6MAD0PgZQADpoAN4ARP2UaMAAtihjtWMwYwA0y7jqAO7QHAtLq8soM8BICHvLAL6YwjUwFazsXJT145NQ03PnB2MbqttQu0WyzWYyOJzOQLGVzYnG4sHuN1E9SgmWyYEoAAoMlkcpQMgBHVI5ACUmB0KOAAGsYABZbKqJAOGBvD5iREqO4I6rPWn0xn2ABiqAQHAAogAPFTYAgFa7c+H3YrmeqBJxOYbjObqYD2eb1MaiqDeOowPQcGAowlqMBQsycOUiDn3WTyJQqdT1XVgACqAwxLNmKFJLsUyjUqjulWMtSFnBgvsoIdNlmZA1ZZIp1ITsE2SDA8VTU0DMGACApHBTKHFaI0Ibd4c5lXZKHq2ZDDqookbVWoPIUZZQwAr7Sp6AlUplhRulG7SowKqcwQ1Yy1qh1euWhuN9QDcxLA6HKfklPQtvQHAzg+pAuAPgQYBTWnkegMMBABTQKHA+UKdbD6m7Zt6jQO8EA7LtSi5XsTRDF8UDgD8v0nccUGlH8OxnSDKjnMB6gAVjVZdV3XBZNyNaB6ifXR9EMd80E-b8ChgAAzcEUF2K5z0wP93QjLCe07FRgNA8CnUqHjw1qEAKXRbN-QGENg20esAPuaMFA4c0220UTDGdZT-1UKSZJQBQfHzDFb3zRTuIM3jIyMHRag080zOsnTm27GEnhNbE0TxNQ7ywby4UA+UXgmNNAyWfVgWWKz4naCATzQGLlkuDCFWw5BlRgcJCNGSKi0+GBYu+BKkpStL9k4+1PG8Px-GgdhdRiAU4FFaQ4AUGAABkICyQocIc6cTWaNoul6Ax1B-DVdxQNZfn+DgrlGhyQp5EZ5sW-Q-h2aFHlCqDBJbGAEAGxl5OKhaYCWnZSXJK9eRmpl5t0sLoPqOkXsFYUxUlVDJ0y2ccvnGBVXVQriN1UiDXIk0zQtFArRyM97U8-TXUM+oEvMQg5BQK73kDHatnu2ysfstSnPgeIv2peaSzjO6ARgY4wBAeJLypeMBhgNAIGYFEcXRFELwk1TjqRfmRIx46eW9aZzPiaAkAAL3YlC0NlNb+JwhcAEYiPDEj9S3CjmSV-NVY1ji7QvR6eZvILH20OC30Qxjfzsht+KAmWEDAuWm3CmQ3ZohD6KQn8taB3XFVB3CYAIyHNRNmGzfhyjw9fOiGMnFi2LtriJb4qWhJgCxUnezHQ14z10EoAnKvQInWSUynfajGmFHUCAiAJktlcr5L0BgXN80LYm5hr+4NpNBKMlUILMHnj6ah3KK5jWCrR6neUQZKJP8tT7ah-zFu0Fqi96t8AIvBQMfYiQBIwAfsf+vsXwsGG+WxukUUvVRTtFFN0Ho00GQFGGLvFKwM56HR5DA9Aq8EHwhDo6U650v7mUssrS+pIMbiR9h6IwKBuDojcvEXBF894dzrl3Ry9RpBkJQOic+BZICwMdtSAAchAdhI8UosV8PaUu69pYgUDrPdBG94xWxVlAdWmsAba33tBQ+uVVRGyhunDccNtyWwSjbdiaMHaZhgM7e8rtnw0Q9lHL2FN6GSxkRI2W1QIJ-yYTneCntkIqLjgfPWid8IFTTtqDOZEDFUXdnnaOTFWKnBMdfRxKky4uIrpIoO7inTwNhDyLB38l4rzXn7UOmS4EJyPvUE+9tTBeDvv4MWMRsCMmpL1NEMAADigYNC-xkTyBonTgFgPsIGaBeC95wMqPPHGEzYElPSZgtE3StTUMSrQmuRDO4kK9JQtZ+CUmGQctGWm9MBGcLHlWGs3NqQAEk0BUFLEgc0CVBHILEaUjB9RWIIAMNIgSCt5HGP+hOdC8dspVPBk4bRYS1wRP0RbHwQLFG21MYcqm5dTpVzZNkvSWynFGRgF6FZah9kbI+d3eonTWH7gQESnpNyLG3isUYbxdj87oQ+Zi4SUjg4Apgt4yOHKCixzBYEypuUU7G3CXo82Jpom2NiV7QuiTi6iOIWkgS0sGgjFGXMW50g1ibHiHmFA2ZWRrGSKASk5qSb0rmDwwMFwVguFdZ0f5MyzrLJ6UU+8KC8lZX5fUHVeqUAGqNSa2SW8bpWpADa6NaxQ2OrmM611Lh3XgvgMEvKBUQ2BnDePSNZqE2mgQNa21297UoGTSgVNbram30ahwAA7G4JwKAnAxFFMEOAHUABs8ATJdOLEUROI1Q7jQ6CMsZ8jL4aiTU6qZDwA2zJoSlNYurAw1oOgG8dXy3wmRJWS9dVaa0PXMd9BkTJQ380DP80aX0+QOCFGQkFgMxXqKCZCiG0q4WyqzqaIcSMUY2mSYQsO2zCW7OVse9AdDUnHJptS5gjNdr9yQATc0E8CyvIuYUbhvMZwCyFqiXEYt0W+25QHLJGD16AqMSi5RoKdbiohZo6Fv7TaRMRcipRaqzFPUsQ+VlNjc6+M5Rq8RGS3G0c+bI2CEdxMiv8R+24X7JWhJXLo2Gcrs6idokptAKruD8Yo84rVFc3rgdLvUaSg50RHvmvBo51N6hwCHTe7DVbSHyFOLWSTcnpahoNR61BtnD2Bl9cFVBdGTTBekBUtjYMT7DHiw2+pjVLBkPOpsGIcREhZcDhAXLAApCAjJh17n8LGyko6j57tkc0b0k0eihvGWu9AGpsBlqy1ABC50oCJvzdIVagTplhfYZfDd3XgC9f69AIb+qRv+p8g16WAArcraAj1ILQItHrlB5uDarQa89T1L38hOwlvlD7npXt+q+0VLHP0SrBj+nRMqdMAcRpaIkoHanWY1Q3MAezdvOappSrpNLUN9wHuiLDeYcPDzw4y7M-NBZIxFpQdiZnNX+3KTd0OisGN8ae2otTr2k5aM4-C3ThjlbArRVyxZm9rqbIgwS4HJKFCPNOLoU4eZLAYhm3NiAA3FthukODhhJy4B0zjTAJAzEYAGG4OAdiHsBuK4jMAXnZbkhiAI0J6x1ExP2KBszizp0Ce4ti14-TQq4loDJ4lrNkKpUfb-V9qJbKlUFwSSZtFBG0fEcx-5cjlv-Y-L+YTz6ciSe2xd5m-WUKlye64wiheB4KyV1HKlZJkfcX1GxezmzMBNuMiPSLw7YuFtXel6pSH3psBPkMKrpCGv3wDYAOQRjeoX-dNvZO5J8vUCvaAosraOp46jruU81K4o2++UBZt5ZfokLwq-ECDlgMAbA3X8YFzq+YBrAyAFAJAWA4wS7PXlIWVb2z3A8AKH3xiAhtva6pMfzvl-2A3+48QyYRYTYVLDpTeFUAlwgKZheTmXQA0CN2ZWEwVTN2FW9kgyk2txkxOjtw5zgkdy9iT1Yzdw01Ti00+0zh9303ZSd2MySVqQHxOh5Ro2wP4k9RACfygEnxKRnyHwpyS2PjVFqSAA)

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
