# Developing OptiGUI

## Prepare OptiGUI

After cloning the repository (and before opening in an IDE or doing anything), make sure to run `./gradlew jar -PnoModLocalRuntime`. This will generate the `modLocalRuntime` configuration of the selected OptiGlue library so Gradle won't cry. Otherwies, you'll encounter this sync error (that file doesn't yet exist):

```
A problem occurred configuring project ':OptiGUI'.
> Failed to read metadata from .../OptiGlue/1.19.4/build/devlibs/optiglue-2.1.0-beta.3-mc.1.19.4-dev.jar
```

## Build OptiGUI

To build OptiGUI, run `./gradlew build` or run the task from your IDE.

## Generate documentation

To generate documentation, run `./gradlew dokkaHtml`.

To generate documentation with Java syntax, run `./gradlew dokkaHtml -PjavaSyntax`.
