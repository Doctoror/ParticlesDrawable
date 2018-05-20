package com.doctoror.particlesdrawable;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;

import com.doctoror.particlesdrawable.contract.SceneConfiguration;
import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneScheduler;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GlParticlesView extends GLSurfaceView implements
        SceneController,
        SceneConfiguration,
        SceneScheduler,
        GLSurfaceView.Renderer {

    private final GlSceneRenderer renderer = new GlSceneRenderer();
    private final ScenePresenter presenter = new ScenePresenter(renderer, this);

    /**
     * Whether explicitly stopped by user. This means it will not start automatically on visibility
     * change or when attached to window.
     */
    @VisibleForTesting
    boolean mExplicitlyStopped;

    private boolean mAttachedToWindow;
    private boolean mEmulateOnAttachToWindow;

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

        renderer.generateParticleTexture(presenter.getMaxDotRadius());
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
        presenter.setFrameDelay(delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFrameDelay() {
        return presenter.getFrameDelay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStepMultiplier(@FloatRange(from = 0) final float stepMultiplier) {
        presenter.setStepMultiplier(stepMultiplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getStepMultiplier() {
        return presenter.getStepMultiplier();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotRadiusRange(@FloatRange(from = 0.5f) final float minRadius,
                                  @FloatRange(from = 0.5f) final float maxRadius) {
        presenter.setDotRadiusRange(minRadius, maxRadius);
        renderer.generateParticleTexture(maxRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMinDotRadius() {
        return presenter.getMinDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMaxDotRadius() {
        return presenter.getMaxDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        presenter.setLineThickness(lineThickness);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineThickness() {
        return presenter.getLineThickness();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineDistance(@FloatRange(from = 0) final float lineDistance) {
        presenter.setLineDistance(lineDistance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineDistance() {
        return presenter.getLineDistance();
    }

    /**
     * {@inheritDoc}
     */
    public void setNumDots(@IntRange(from = 0) final int newNum) {
        presenter.setNumDots(newNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumDots() {
        return presenter.getNumDots();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotColor(@ColorInt final int dotColor) {
        presenter.setDotColor(dotColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDotColor() {
        return presenter.getDotColor();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineColor(@ColorInt final int lineColor) {
        presenter.setLineColor(lineColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineColor() {
        return presenter.getLineColor();
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

    @Override
    protected void onVisibilityChanged(@NonNull final View changedView, final int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != View.VISIBLE) {
            stopInternal();
        } else {
            startInternal();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mAttachedToWindow = true;
        startInternal();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mAttachedToWindow = false;
        stopInternal();
    }

    /**
     * Start animating. This will clear the explicit control flag if set by {@link #stop()}.
     * Note that if this View's visibility is not {@link #VISIBLE} or it's not attached to window,
     * this will not start animating until the state changes to meet the requirements above.
     */
    @Keep
    public void start() {
        mExplicitlyStopped = false;
        startInternal();
        onResume();
    }

    /**
     * Explicilty stop animating. This will stop animating and no animations will start
     * automatically until you call {@link #start()}.
     */
    @Keep
    public void stop() {
        mExplicitlyStopped = true;
        stopInternal();
        onPause();
    }

    @VisibleForTesting
    void startInternal() {
        if (!mExplicitlyStopped && isVisibleWithAllParents(this) && isAttachedToWindowCompat()) {
            presenter.start();
        }
    }

    @VisibleForTesting
    void stopInternal() {
        presenter.stop();
    }

    @VisibleForTesting
    boolean isRunning() {
        return presenter.isRunning();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    void setEmulateOnAttachToWindow(final boolean emulateOnAttachToWindow) {
        mEmulateOnAttachToWindow = emulateOnAttachToWindow;
    }

    @Override
    public void onSurfaceCreated(@NonNull final GL10 gl, @NonNull final EGLConfig config) {
        renderer.setupGl(gl, getWidth(), getHeight());
    }

    @Override
    public void onSurfaceChanged(@NonNull final GL10 gl, final int width, final int height) {
        renderer.setupViewport(gl, getWidth(), getHeight());
    }

    @Override
    public void onDrawFrame(@NonNull final GL10 gl) {
        renderer.setClearColor(gl, mBackgroundColor);

        renderer.setGl(gl);
        presenter.draw();
        renderer.setGl(null);

        presenter.run();
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean isAttachedToWindowCompat() {
        if (mEmulateOnAttachToWindow) {
            return mAttachedToWindow;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return isAttachedToWindow();
        }
        return mAttachedToWindow;
    }

    private boolean isVisibleWithAllParents(@NonNull final View view) {
        if (view.getVisibility() != VISIBLE) {
            return false;
        }

        final ViewParent parent = view.getParent();
        if (parent instanceof View) {
            return isVisibleWithAllParents((View) parent);
        }

        return true;
    }

    private final Runnable requestRenderRunnable = new Runnable() {
        @Override
        public void run() {
            requestRender();
        }
    };
}
