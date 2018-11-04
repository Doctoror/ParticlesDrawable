# ParticlesDrawable
Draws random flying particles in space forming constellations.

Min API level 9.

Contains:
- `ParticlesDrawable`, which is an `Animatable` `Drawable`.
- `ParticlesView`, which is a `View`.

Both have the same public methods for customization and may be inflated using the same customization xml attributes.

# Add to your project

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.doctoror.particlesdrawable/library/badge.png?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.github.doctoror.particlesdrawable/library)

```groovy
dependencies {
    compile 'com.github.doctoror.particlesdrawable:library:[version]'
}
```

## Screenshots
![screenshot](/screenshots/demo.gif?raw=true)

# Usage
`ParticlesDrawable` usage example
```java
private final ParticlesDrawable mDrawable = new ParticlesDrawable();

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_demo);
    findViewById(R.id.view).setBackground(mDrawable);
}

@Override
protected void onStart() {
    super.onStart();
    mDrawable.start();
}

@Override
protected void onStop() {
    super.onStop();
    mDrawable.stop();
}
```

## Customization
Here is a list of all attributes, set with default values

```xml
<com.doctoror.particlesdrawable.ParticlesView
    app:density="60"
    app:frameDelayMillis="10"
    app:lineColor="@android:color/white"
    app:lineLength="86dp"
    app:lineThickness="1dp"
    app:particleColor="@android:color/white"
    app:particleRadiusMax="3dp"
    app:particleRadiusMin="1dp"
    app:speedFactor="1" />
```
The conventional getters and setters are also available.

Since API 24, you may also customize the Drawable in `xml`. For example, create
`drawable-v24/particles_density_120.xml`
```xml
<?xml version="1.0" encoding="utf-8"?>
<!-- Some versions of Android Studio may show false warning,
"Element drawable must be declared", but it works fine when
compiling and running -->
<drawable
    class="com.doctoror.particlesdrawable.ParticlesDrawable"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    app:density="120"/>
```
And inflate, like
```java
mDrawable = (ParticlesDrawable) ContextCompat
        .getDrawable(this, R.drawable.particles_density_120);
```

## Configuration Demo
[Particle Constellations Live Wallpaper](https://github.com/Doctoror/ParticleConstellationsLiveWallpaper) has a great configuration screen which allows you to quickly see through what can you do with the library:

[![Video](https://github.com/Doctoror/ParticlesWallpaper/raw/master/screenshots/video.png)](https://www.youtube.com/watch?v=Q7qvmCMUN20)


## License
```
Copyright 2017 Yaroslav Mytkalyk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

```
