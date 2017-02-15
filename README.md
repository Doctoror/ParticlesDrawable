# ParticlesView
Android Particles Drawable and View

##Screenshots
![screenshot](/screenshots/default.gif?raw=true)

![screenshot](/screenshots/Demo.png?raw=true)

# Usage
You may use a `ParticlesDrawable`, which implements `Animatable`, or a `ParticlesView` (which is a `View`, of course).

`ParticlesDrawable` example
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

##Customization
`ParticlesDrawable` as well as `ParticlesView`, contains setters for various customzations. `ParticlesView` can also be customized from an `xml`. Here is a list of all attributes, set with default values

```xml
<com.doctoror.particlesview.ParticlesView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@android:color/black"
    app:minDotRadius="1dp"
    app:maxDotRadius="3dp"
    app:lineThickness="1dp"
    app:lineDistance="86dp"
    app:numDots="60"
    app:dotColor="@android:color/white"
    app:lineColor="@android:color/white"
    app:frameDelayMillis="10"
    app:stepMultiplier="1"/>
```
