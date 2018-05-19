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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GlParticlesView extends GLSurfaceView
        implements SceneRenderer, SceneScheduler, ParticlesScene, GLSurfaceView.Renderer {

    private final SceneController sceneController = new SceneController(this, this);
    private final GlSceneRenderer sceneRenderer = new GlSceneRenderer();

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
                sceneController.handleAttrs(a);
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
        sceneController.nextFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrame() {
        sceneController.makeBrandNewFrame();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrameWithPointsOffscreen() {
        sceneController.makeBrandNewFrameWithPointsOffscreen();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFrameDelay(@IntRange(from = 0) final int delay) {
        sceneController.setFrameDelay(delay);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getFrameDelay() {
        return sceneController.getFrameDelay();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStepMultiplier(@FloatRange(from = 0) final float stepMultiplier) {
        sceneController.setStepMultiplier(stepMultiplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getStepMultiplier() {
        return sceneController.getStepMultiplier();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotRadiusRange(@FloatRange(from = 0.5f) final float minRadius,
                                  @FloatRange(from = 0.5f) final float maxRadius) {
        sceneController.setDotRadiusRange(minRadius, maxRadius);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMinDotRadius() {
        return sceneController.getMinDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getMaxDotRadius() {
        return sceneController.getMaxDotRadius();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineThickness(@FloatRange(from = 1) final float lineThickness) {
        sceneController.setLineThickness(lineThickness);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineThickness() {
        return sceneController.getLineThickness();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineDistance(@FloatRange(from = 0) final float lineDistance) {
        sceneController.setLineDistance(lineDistance);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public float getLineDistance() {
        return sceneController.getLineDistance();
    }

    /**
     * {@inheritDoc}
     */
    public void setNumDots(@IntRange(from = 0) final int newNum) {
        sceneController.setNumDots(newNum);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNumDots() {
        return sceneController.getNumDots();
    }

    /**
     * {@inheritDoc}
     */
    public void setDotColor(@ColorInt final int dotColor) {
        sceneController.setDotColor(dotColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDotColor() {
        return sceneController.getDotColor();
    }

    /**
     * {@inheritDoc}
     */
    public void setLineColor(@ColorInt final int lineColor) {
        sceneController.setLineColor(lineColor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLineColor() {
        return sceneController.getLineColor();
    }

    @Override
    protected void onSizeChanged(final int w, final int h, final int oldw, final int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        sceneController.setBounds(0, 0, w, h);
    }

    @Override
    public void drawLine(final float startX, final float startY, final float stopX,
                         final float stopY, final float strokeWidth, @ColorInt final int color) {
        sceneRenderer.drawLine(startX, startY, stopX, stopY, strokeWidth, color);
    }

    @Override
    public void fillCircle(final float cx, final float cy, final float radius,
                           @ColorInt final int color) {
        sceneRenderer.fillCircle(cx, cy, radius, color);
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
            sceneController.start();
        }
    }

    @VisibleForTesting
    void stopInternal() {
        sceneController.stop();
    }

    @VisibleForTesting
    boolean isRunning() {
        return sceneController.isRunning();
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    void setEmulateOnAttachToWindow(final boolean emulateOnAttachToWindow) {
        mEmulateOnAttachToWindow = emulateOnAttachToWindow;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glViewport(0, 0, getWidth(), getHeight());
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glEnable(GL10.GL_LINE_SMOOTH);
        gl.glHint(GL10.GL_LINE_SMOOTH_HINT, GL10.GL_NICEST);

        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
        gl.glOrthof(0, getWidth(), 0, getHeight(), 1, -1);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, getWidth(), getHeight());
    }

    @Override
    public void onDrawFrame(@NonNull final GL10 gl) {
        gl.glClearColor(
                Color.red(mBackgroundColor) / 255f,
                Color.green(mBackgroundColor) / 255f,
                Color.blue(mBackgroundColor) / 255f, 0f);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLineWidth(sceneController.getLineThickness());

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        sceneRenderer.setGl(gl);
        sceneRenderer.beginTransaction(sceneController.getNumDots());

        sceneController.draw();
        sceneController.run();

        sceneRenderer.commit();
        sceneRenderer.setGl(null);

        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
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
