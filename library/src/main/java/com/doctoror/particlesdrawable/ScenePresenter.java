package com.doctoror.particlesdrawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.doctoror.particlesdrawable.contract.SceneController;
import com.doctoror.particlesdrawable.contract.SceneRenderer;
import com.doctoror.particlesdrawable.contract.SceneScheduler;

import java.util.Random;

/**
 * Particles Scene Controller
 */
public final class ScenePresenter implements Runnable, SceneController {

    /**
     * Path calculation padding.
     *
     * @see #applyFreshParticleOffScreen(int)
     */
    private static final float PCC = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 18f, Resources.getSystem().getDisplayMetrics());

    private static final float STEP_PER_MS = 0.05f;
    
    private final Random mRandom = new Random();

    private final ParticlesScene scene;
    private final SceneRenderer renderer;
    private final SceneScheduler scheduler;

    private boolean mParticlesInited;

    private long mLastFrameTime;
    private long mLastDrawDuration;

    private boolean mAnimating;

    public ScenePresenter(
            @NonNull final ParticlesScene scene,
            @NonNull final SceneRenderer renderer,
            @NonNull final SceneScheduler scheduler) {
        this.scene = scene;
        this.renderer = renderer;
        this.scheduler = scheduler;
    }

    @NonNull
    private SceneScheduler getViewScheduler() {
        return scheduler;
    }

    void inflate(@NonNull final Resources r, @NonNull final AttributeSet attrs) {
        final TypedArray a = r.obtainAttributes(attrs, R.styleable.ParticlesView);
        try {
            handleAttrs(a);
        } finally {
            a.recycle();
        }
    }

    void handleAttrs(@NonNull final TypedArray a) {
        final int count = a.getIndexCount();
        float minDotRadius = Defaults.DEFAULT_MIN_DOT_RADIUS;
        float maxDotRadius = Defaults.DEFAULT_MAX_DOT_RADIUS;
        for (int i = 0; i < count; i++) {
            final int attr = a.getIndex(i);
            if (attr == R.styleable.ParticlesView_minDotRadius) {
                minDotRadius = a.getDimension(attr, Defaults.DEFAULT_MIN_DOT_RADIUS);

            } else if (attr == R.styleable.ParticlesView_maxDotRadius) {
                maxDotRadius = a.getDimension(attr, Defaults.DEFAULT_MAX_DOT_RADIUS);

            } else if (attr == R.styleable.ParticlesView_lineThickness) {
                scene.setLineThickness(a.getDimension(attr, Defaults.DEFAULT_LINE_THICKNESS));

            } else if (attr == R.styleable.ParticlesView_lineDistance) {
                scene.setLineDistance(a.getDimension(attr, Defaults.DEFAULT_LINE_DISTANCE));

            } else if (attr == R.styleable.ParticlesView_numDots) {
                scene.setNumDots(a.getInteger(attr, Defaults.DEFAULT_DOT_NUMBER));

            } else if (attr == R.styleable.ParticlesView_dotColor) {
                scene.setDotColor(a.getColor(attr, Defaults.DEFAULT_DOT_COLOR));

            } else if (attr == R.styleable.ParticlesView_lineColor) {
                scene.setLineColor(a.getColor(attr, Defaults.DEFAULT_LINE_COLOR));

            } else if (attr == R.styleable.ParticlesView_frameDelayMillis) {
                scene.setFrameDelay(a.getInteger(attr, Defaults.DEFAULT_DELAY));

            } else if (attr == R.styleable.ParticlesView_stepMultiplier) {
                scene.setStepMultiplier(a.getFloat(attr, Defaults.DEFAULT_STEP_MULTIPLIER));
            }
        }
        scene.setDotRadiusRange(minDotRadius, maxDotRadius);
    }

    private void resetLastFrameTime() {
        mLastFrameTime = 0L;
    }

    private void gotoNextFrameAndSchedule() {
        nextFrame();
        getViewScheduler()
                .scheduleNextFrame(Math.max(scene.getFrameDelay() - mLastDrawDuration, 5L));
    }

    void setAlpha(final int alpha) {
        scene.setAlpha(alpha);
    }

    int getAlpha() {
        return scene.getAlpha();
    }

    public void start() {
        if (!mAnimating) {
            mAnimating = true;
            resetLastFrameTime();
            gotoNextFrameAndSchedule();
        }
    }

    public void stop() {
        if (mAnimating) {
            mAnimating = false;
            resetLastFrameTime();
            getViewScheduler().unscheduleNextFrame();
        }
    }

    boolean isRunning() {
        return mAnimating;
    }

    public void run() {
        if (mAnimating) {
            gotoNextFrameAndSchedule();
        } else {
            resetLastFrameTime();
        }
    }

    public void draw() {
        final long startTime = SystemClock.uptimeMillis();
        renderer.drawScene(scene);
        mLastDrawDuration = SystemClock.uptimeMillis() - startTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrame() {
        final ParticlesScene model = scene;
        if (model.getWidth() != 0 && model.getHeight() != 0) {
            resetLastFrameTime();
            initParticles();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeBrandNewFrameWithPointsOffscreen() {
        final ParticlesScene model = scene;
        if (model.getWidth() != 0 && model.getHeight() != 0) {
            resetLastFrameTime();
            initParticlesOffScreen();
        }
    }

    public void setBounds(final int left, final int top, final int right, final int bottom) {
        final ParticlesScene model = scene;
        model.setWidth(right - left);
        model.setHeight(bottom - top);
        if (right - left > 0 && bottom - top > 0) {
            if (!mParticlesInited) {
                mParticlesInited = true;
                initParticles();
            }
        } else {
            if (mParticlesInited) {
                mParticlesInited = false;
            }
        }
    }

    private void initParticles() {
        initParticles(new ParticleCreationStrategy() {

            @Override
            public void addNewParticle(final int position) {
                ScenePresenter.this.addNewParticle(position, position % 2 == 0);
            }
        });
    }

    private void initParticlesOffScreen() {
        initParticles(new ParticleCreationStrategy() {

            @Override
            public void addNewParticle(final int position) {
                ScenePresenter.this.addNewParticle(position, false);
            }
        });
    }

    private void initParticles(@NonNull final ParticleCreationStrategy strategy) {
        final ParticlesScene model = scene;
        if (model.getWidth() == 0 || model.getHeight() == 0) {
            throw new IllegalStateException("Cannot init points if width or height is 0");
        }
        for (int i = 0; i < model.getNumDots(); i++) {
            strategy.addNewParticle(i);
        }
    }

    private void addNewParticle(
            final int position,
            final boolean onScreen) {
        final ParticlesScene model = scene;
        if (model.getWidth() == 0 || model.getHeight() == 0) {
            throw new IllegalStateException("Cannot make new point if width or height is 0");
        }

        if (onScreen) {
            applyFreshParticleOnScreen(position);
        } else {
            applyFreshParticleOffScreen(position);
        }
    }

    /**
     * Set new point coordinates somewhere on screen and apply new direction
     *
     * @param position the point position to apply new values to
     */
    private void applyFreshParticleOnScreen(final int position) {
        final ParticlesScene model = scene;
        final int w = model.getWidth();
        final int h = model.getHeight();
        if (w == 0 || h == 0) {
            throw new IllegalStateException("Cannot apply points if width or height is 0");
        }

        final double direction = Math.toRadians(mRandom.nextInt(360));
        final float dCos = (float) Math.cos(direction);
        final float dSin = (float) Math.sin(direction);
        final float x = mRandom.nextInt(w);
        final float y = mRandom.nextInt(h);
        final float stepMultiplier = newRandomIndividualDotStepMultiplier();
        final float radius = newRandomIndividualDotRadius();

        model.setParticleData(
                position,
                x,
                y,
                dCos,
                dSin,
                radius,
                stepMultiplier);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void nextFrame() {
        final ParticlesScene model = scene;
        final float step = mLastFrameTime == 0 ? 1f
                : (SystemClock.uptimeMillis() - mLastFrameTime) * STEP_PER_MS;

        final int particlesCount = model.getNumDots();
        for (int i = 0; i < particlesCount; i++) {
            float x = model.getParticleX(i);
            float y = model.getParticleY(i);

            final float stepMultiplier = model.getParticleStepMultiplier(i);
            final float dCos = model.getParticleDirectionCos(i);
            final float dSin = model.getParticleDirectionSin(i);

            x += step * model.getStepMultiplier() * stepMultiplier * dCos;
            y += step * model.getStepMultiplier() * stepMultiplier * dSin;

            if (pointOutOfBounds(x, y)) {
                applyFreshParticleOffScreen(i);
            } else {
                model.setParticleX(i, x);
                model.setParticleY(i, y);
            }
        }

        mLastFrameTime = SystemClock.uptimeMillis();
        getViewScheduler().invalidate();
    }

    /**
     * Generates new step multiplier for individual dot.
     * The value is in [0.5:1.5] range
     *
     * @return new step multiplier for individual dot
     */
    private float newRandomIndividualDotStepMultiplier() {
        return 1f + 0.1f * (mRandom.nextInt(11) - 5);
    }

    /**
     * Generates new individual dot radius based on min and max radius setting
     *
     * @return new dot radius
     */
    private float newRandomIndividualDotRadius() {
        final ParticlesScene model = scene;
        return model.getMinDotRadius() == model.getMaxDotRadius() ?
                model.getMinDotRadius() : model.getMinDotRadius()
                + (mRandom.nextInt(
                (int) ((model.getMaxDotRadius() - model.getMinDotRadius()) * 100f))) / 100f;
    }

    /**
     * Set new point coordinates somewhere off screen and apply new direction towards the screen
     *
     * @param position the particle position to apply new values to
     */
    private void applyFreshParticleOffScreen(final int position) {
        final ParticlesScene model = scene;
        final int w = model.getWidth();
        final int h = model.getHeight();
        if (w == 0 || h == 0) {
            throw new IllegalStateException("Cannot apply points if width or height is 0");
        }

        float x = mRandom.nextInt(w);
        float y = mRandom.nextInt(h);

        // The offset to make when creating point of out bounds
        final short offset = (short) (model.getMinDotRadius() + model.getLineDistance());

        // Point angle range
        final float startAngle;
        float endAngle;

        // Make random offset and calulate angles so that the direction of travel will always be
        // towards our View

        switch (mRandom.nextInt(4)) {
            case 0:
                // offset to left
                x = (short) -offset;
                startAngle = angleDeg(PCC, PCC, x, y);
                endAngle = angleDeg(PCC, h - PCC, x, y);
                break;

            case 1:
                // offset to top
                y = (short) -offset;
                startAngle = angleDeg(w - PCC, PCC, x, y);
                endAngle = angleDeg(PCC, PCC, x, y);
                break;

            case 2:
                // offset to right
                x = (short) (w + offset);
                startAngle = angleDeg(w - PCC, h - PCC, x, y);
                endAngle = angleDeg(w - PCC, PCC, x, y);
                break;

            case 3:
                // offset to bottom
                y = (short) (h + offset);
                startAngle = angleDeg(PCC, h - PCC, x, y);
                endAngle = angleDeg(w - PCC, h - PCC, x, y);
                break;

            default:
                throw new IllegalArgumentException("Supplied value out of range");
        }

        if (endAngle < startAngle) {
            endAngle += 360;
        }

        // Get random angle from angle range
        final float randomAngleInRange = startAngle + (mRandom
                .nextInt((int) Math.abs(endAngle - startAngle)));
        final double direction = Math.toRadians(randomAngleInRange);

        final float dCos = (float) Math.cos(direction);
        final float dSin = (float) Math.sin(direction);
        final float stepMultiplier = newRandomIndividualDotStepMultiplier();
        final float radius = newRandomIndividualDotRadius();

        model.setParticleData(
                position,
                x,
                y,
                dCos,
                dSin,
                radius,
                stepMultiplier);
    }

    /**
     * Used for checking if the point is off-screen and farther than line distance
     *
     * @param x the point x
     * @param y the point y
     * @return true if the point is off-screen and guaranteed not to be used to draw a line to the
     * closest point on-screen
     */
    private boolean pointOutOfBounds(final float x, final float y) {
        final ParticlesScene model = scene;
        final float offset = model.getMinDotRadius() + model.getLineDistance();
        return x + offset < 0 || x - offset > model.getWidth()
                || y + offset < 0 || y - offset > model.getHeight();
    }

    /**
     * Returns angle in degrees between two points
     *
     * @param ax x of the point 1
     * @param ay y of the point 1
     * @param bx x of the point 2
     * @param by y of the point 2
     * @return angle in degrees between two points
     */
    private static float angleDeg(final float ax, final float ay,
                                  final float bx, final float by) {
        final double angleRad = Math.atan2(ay - by, ax - bx);
        double angle = Math.toDegrees(angleRad);
        if (angleRad < 0) {
            angle += 360;
        }
        return (float) angle;
    }

    private interface ParticleCreationStrategy {

        void addNewParticle(int position);
    }
}
