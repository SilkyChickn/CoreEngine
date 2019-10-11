# CoreEngine (Java/LWJGL/OpenGL)
[![maven central][maven-central-img]][maven-central-url]
![v1.1.0][progress-img]
[![lwjgl][lwjgl-img]][lwjgl-url]
[![jbullet][jbullet-img]][jbullet-url]
[![vecmath][vecmath-img]][vecmath-url]
[![license][license-img]](LICENSE)

CoreEngine is a LightWeight Java Game Library (LWJGL) framework. Its a simple to use OpenGL game engine with several features. You can use this engine to create your own games (see [license](LICENSE)), but it's mostly a support, for people who want to write their own game engine. I would be glad, if someone gets inspired by my implementations.

## Getting Started

### Installation

CoreEngine is available at maven central repository.  So the easiest way ist to add the dependency into the project build file.

#### Maven
```xml
<dependency>
	<groupId>io.github.suuirad</groupId>
	<artifactId>coreengine</artifactId>
	<version>1.1.0</version>
</dependency>
```

#### Gradle
```gradle
compile group: 'io.github.suuirad', name: 'coreengine', version: '1.1.0'
```

#### Manual

For manual installation, you can download the jar files [here](http://central.maven.org/maven2/io/github/suuirad/coreengine/1.1.0). Just download, what you need (javadoc/sources) and add it to your project.

### Simple Game

The following code creates a simple game with an empty scene. 
If you run this code you shuold see a black 800 by 600 pixel sized window.

```java
Game.init(800, 600, "My first CoreEngine Game", false);

Scene scene = new Scene();
Game.registerScene(scene);

while(Window.keepAlive()){
    Game.tick();
}

Game.exit(0);
```

Now you are ready to go, to create any game you want!

## Links

> Example Code: [https://github.com/Suuirad/CoreEngine/tree/master/examples](https://github.com/Suuirad/CoreEngine/tree/master/examples)

> JavaDoc: [https://suuirad.github.io/CoreEngine/](https://suuirad.github.io/CoreEngine/)

## License

**BSD 2-Clause License**

Copyright (c) 2019, Suuirad<br>
All rights reserved.

<!-- Shields Links -->
[lwjgl-img]: https://img.shields.io/badge/lwjgl-v.3.2.3-green.svg?style=flat-square
[lwjgl-url]: https://mvnrepository.com/artifact/org.lwjgl/lwjgl/3.2.3
[jbullet-img]: https://img.shields.io/badge/jbullet-v.20101010_1-green.svg?style=flat-square
[jbullet-url]: https://mvnrepository.com/artifact/cz.advel.jbullet/jbullet/20101010-1
[vecmath-img]: https://img.shields.io/badge/vecmath-v.1.5.2-green.svg?style=flat-square
[vecmath-url]: https://mvnrepository.com/artifact/javax.vecmath/vecmath/1.5.2
[license-img]: https://img.shields.io/badge/license-BSD-blue.svg?style=flat-square
[maven-central-img]: https://img.shields.io/badge/maven--central-v.1.1.0-red.svg?style=flat-square
[maven-central-url]: https://mvnrepository.com/artifact/io.github.suuirad/coreengine/1.1.0
[progress-img]: https://img.shields.io/badge/v1.1.0-100%25-red.svg?style=flat-square