package com.doctoror.particlesdrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;

import com.doctoror.particlesdrawable.contract.SceneConfiguration;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneScheduler;
import com.doctoror.particlesdrawable.renderer.GlSceneRenderer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GlParticlesView extends GLSurfaceView implements
        SceneController,
        SceneConfiguration,
        SceneScheduler,
        GLSurfaceView.Renderer {

    private final ParticlesScene scene = new ParticlesScene();
    private final GlSceneRenderer renderer = new GlSceneRenderer();
    private final ScenePresenter presenter = new ScenePresenter(scene, renderer, this);

    private GL10 mGl;

    @ColorInt
    private int mBackgroundColor = Color.DKGRAY;

    public GlParticlesView(Context context) {
        super(context);
        init(context, null);
    }

    public GlParticlesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(@NonNull final Context context, @Nullable final AttributeSet attrs) {
        setRenderer(this);
        setRenderMode(RENDERMODE_WHEN_DIRTY);
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") final TypedArray a = context
                    .obtainStyledAttributes(attrs, R.styleable.ParticlesView);
            try {
                presenter.handleAttrs(a);
            } finally {
                a.recycle();
            }
        }

        final TypedArray array = context.getTheme()
                .obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        try {
            mBackgroundColor = array.getColor(0, Color.DKGRAY);
        } catch (Exception e) {
            Log.w("GlParticlesView", "Failed to obtain windowBackground", e);
        } finally {
            array.recycle();
        }

        renderer.markTextureDirty();
    }

    /**
     * Sets the background color for this View.
     * Default is windowBackground.
     *
     * @param color the background of this View.
     */
    @Override
    public void setBackgroundColor(@ColorInt final int color) {
        mBackgroundColor = color;
    }

    @Override
    public void setBackgroundDrawable(final Drawable background) {
        throw new UnsupportedOperationException(
                "Background drawables are not supported. Use setBackgroundColor instead");
    }

    @Override
    public void setBackground(final Drawable background) {
        throw new UnsupportedOperationException(
                "Background drawables are not supported. Use setBackgroundColor instead");
    }

    @Override
    public void setBackgroundResource(final int resid) {
        throw new UnsupportedOperationException(
                "Background resources are not supported. Use setBackgroundColor instead");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        presenter.nextFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrame() {
        presenter.makeBrandNewFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrameWithPointsOffscreen() {
        presenter.makeBrandNewFrameWithPointsOffscreen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFrameDelay(@IntRange(from = 0) final int delay) {
        scene.setFrameDelay(delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFrameDelay() {
        return scene.getFrameDelay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStepMultiplier(@FloatRange(from = 0) final float stepMultiplier) {
        scene.setStepMultiplier(stepMultiplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getStepMultiplier() {
        return scene.getStepMultiplier();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotRadiusRange(@FloatRange(from = 0.5f) final float minRadius,
                                  @FloatRange(from = 0.5f) final float maxRadius) {
        scene.setDotRadiusRange(minRadius, maxRadius);
        renderer.markTextureDirty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMinDotRadius() {
        return scene.getMinDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMaxDotRadius() {
        return scene.getMaxDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        scene.setLineThickness(lineThickness);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineThickness() {
        return scene.getLineThickness();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineDistance(@FloatRange(from = 0) final float lineDistance) {
        scene.setLineDistance(lineDistance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineDistance() {
        return scene.getLineDistance();
    }

    /**
     * {@inheritDoc}
     */
    public void setNumDots(@IntRange(from = 0) final int newNum) {
        scene.setNumDots(newNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumDots() {
        return scene.getNumDots();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotColor(@ColorInt final int dotColor) {
        scene.setDotColor(dotColor);
        renderer.markTextureDirty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDotColor() {
        return scene.getDotColor();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineColor(@ColorInt final int lineColor) {
        scene.setLineColor(lineColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineColor() {
        return scene.getLineColor();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        presenter.setBounds(0, 0, w, h);
    }

    @Override
    public void scheduleNextFrame(final long delay) {
        if (delay == 0) {
            requestRender();
        } else {
            postDelayed(requestRenderRunnable, delay);
        }
    }

    @Override
    public void unscheduleNextFrame() {

    }

    /**
     * Start animating. This will clear the explicit control flag if set by {@link #stop()}.
     * Note that if this View's visibility is not {@link #VISIBLE} or it's not attached to window,
     * this will not start animating until the state changes to meet the requirements above.
     */
    @Keep
    public void start() {
        onResume();
        presenter.start();
    }

    /**
     * Explicilty stop animating. This will stop animating and no animations will start
     * automatically until you call {@link #start()}.
     */
    @Keep
    public void stop() {
        presenter.stop();
        onPause();
        recycle();
    }

    private void recycle() {
        if (mGl != null) {
            renderer.recycle(mGl);
            mGl = null;
        }
    }

    @Override
    public void onSurfaceCreated(@NonNull final GL10 gl, @NonNull final EGLConfig config) {
        recycle();
        mGl = gl;
        renderer.setupGl(gl);
    }

    @Override
    public void onSurfaceChanged(@NonNull final GL10 gl, final int width, final int height) {
        renderer.setDimensions(gl, width, height);
    }

    @Override
    public void onDrawFrame(@NonNull final GL10 gl) {
        renderer.setClearColor(gl, mBackgroundColor);

        renderer.setGl(gl);
        presenter.draw();
        renderer.setGl(null);

        presenter.run();
    }

    private final Runnable requestRenderRunnable = new Runnable() {
        @Override
        public void run() {
            requestRender();
        }
    };
}
