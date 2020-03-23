# Quarkus qr code reader project

This project uses Quarkus, the Supersonic Subatomic Java Framework.
If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

This application can accept multiple bar and matrix codes, QR and Aztec is being targeted.

**Limitations**:
The library used for reading the codes can only handle aligned inputs. 
Additionally due to a graalvm limitation, 
as of version 19, the ImageIO classes cannot be used in native mode, 
therefore native compilation is a possibility but will yield with an unusable application.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
mvn quarkus:dev
```

## Packaging and running the application

The application is packageable using `mvn package`.
It produces the executable `rest-json-1.0-SNAPSHOT-runner.jar` file in `/target` directory.

The application is now runnable using `java -jar target/rest-json-1.0-SNAPSHOT-runner.jar`.

## Creating a native executable

You can create a native executable using: `mvn package -Pnative`.

Or you can use Docker to build the native executable using: `mvn package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your binary: `./target/rest-json-1.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image-guide .