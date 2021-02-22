# Just Enough Debug Tools

[work in progress]

You can add the mod to your 1.16.5 workspace:

```groovy
repositories {
    maven { url "https://maven.shadew.net/" }
}

dependencies {
    // ...other dependencies...

    modImplementation ("net.shadew:jedt:0.1") {
        exclude group: "net.fabricmc"
        exclude group: "net.fabricmc.fabric-api"
    }
}
```
