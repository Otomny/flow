# Flow

Flow is a Spigot Framework to boost your productivity while coding minecraft plugin.

**Flow is not recommended for production use as of today**

## Content

- API, usable everywhere
- Spigot, for spigot development
- Velocity (upcoming) for proxy development

## Features

- Aspect Oriented
  - Auto wiring
  - Auto registering
  - Join points
- Automatic Registering of Listeners, Commands and Runnable
- Command Components (SubCommand, Arguments, etc...)
- Multipaper compatible by definition with topic listening (with Redis)

## Gradle

```groovy
repositories{
  mavenCentral()
   // FLOW
  maven { url 'https://jitpack.io' }
  // Paper API 1.19.4 (ONLY IF USING flow:spigot)
  maven {
      url = uri("https://repo.papermc.io/repository/maven-public/")
  }
  // ProtocolLib API (ONLY IF USING flow:spigot)
  maven { url "https://repo.dmulloy2.net/repository/public/" }
}


dependencies{
  implementation 'com.github.Otomny.flow:api:main-SNAPSHOT'
  // IF SPIGOT
  implementation 'com.github.Otomny.flow:spigot:main-SNAPSHOT'
  // ELSE IF VELOCITY
  implementation 'com.github.Otomny.flow:velocity:main-SNAPSHOT'  
  
  // IF SPIGOT
  compileOnly 'com.comphenix.protocol:ProtocolLib:5.0.0-SNAPSHOT'
  compileOnly 'io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT'
}
```