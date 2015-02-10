package com.detroitlabs.whosiewhatsits.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.widget.ImageView;

public class PinchScaleImageView extends ImageView implements OnScaleGestureListener{

    private final float MIN_SCALE = 0.6f;
    private final float MAX_SCALE = 8f;

    // These matrices will be used to move and zoom image
    private Matrix matrix      = new Matrix();
    private Matrix startMatrix = new Matrix();

    private float minScale = MIN_SCALE;
    private float maxScale = MAX_SCALE;

    private int previousPointerCount = 1;

    private float scaleBy    = 1f;
    private float startScale = 1f;

    private final RectF bounds = new RectF();

    private float[] mvalues     = new float[9];
    private float[] startValues = new float[9];

    private boolean allowTranslate       = true;
    private boolean allowScale           = true;
    private boolean handleAllTouchEvents = false;

    // Remember some things for zooming
    private PointF last = new PointF(0, 0);

    private ScaleGestureDetector scaleDetector;

    public PinchScaleImageView(Context context){
        super(context);
        init(context);
    }

    public PinchScaleImageView(Context context, AttributeSet attrs){
        super(context, attrs);
        init(context);
    }

    public PinchScaleImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        scaleDetector = new ScaleGestureDetector(getContext(), this);
    }

    public void setMinScale(final float minScale){
        if (minScale < maxScale && minScale >= 0){
            this.minScale = minScale;
        }
    }

    public void setMaxScale(final float maxScale){
        if (maxScale > minScale){
            this.maxScale = maxScale;
        }
    }

    public boolean isAllowTranslate(){
        return allowTranslate;
    }

    public void setAllowTranslate(boolean allowTranslate){
        this.allowTranslate = allowTranslate;
    }

    public boolean isAllowScale(){
        return allowScale;
    }

    public void setAllowScale(boolean allowScale){
        this.allowScale = allowScale;
    }

    public void setHandleAllTouchEvents(boolean handleAllTouchEvents) {
        this.handleAllTouchEvents = handleAllTouchEvents;
    }

    /**
     * Update the bounds of the displayed image based on the current matrix.
     * @param values the image's current matrix values.
     */
    private void updateBounds(final float[] values){
        if (getDrawable() != null){
            bounds.set(values[Matrix.MTRANS_X],
                    values[Matrix.MTRANS_Y],
                    getDrawable().getIntrinsicWidth() * values[Matrix.MSCALE_X] + values[Matrix.MTRANS_X],
                    getDrawable().getIntrinsicHeight() * values[Matrix.MSCALE_Y] + values[Matrix.MTRANS_Y]);
        }
    }

    /**
     * Get the width of the displayed image.
     * @return the current width of the image as displayed (not the width of the {@link ImageView} itself.
     */
    private float getCurrentWidth(){
        if (getDrawable() != null)
            return getDrawable().getIntrinsicWidth() * mvalues[Matrix.MSCALE_X];
        else
            return 0;
    }

    /**
     * Get the height of the displayed image.
     * @return the current height of the image as displayed (not the height of the {@link ImageView} itself.
     */
    private float getCurrentHeight(){
        if (getDrawable() != null)
            return getDrawable().getIntrinsicHeight() * mvalues[Matrix.MSCALE_Y];
        else
            return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getScaleType() != ScaleType.MATRIX){
            setScaleType(ScaleType.MATRIX);
            startMatrix = new Matrix(getImageMatrix());
            startMatrix.getValues(startValues);
            minScale = MIN_SCALE * startValues[Matrix.MSCALE_X];
            maxScale = MAX_SCALE * startValues[Matrix.MSCALE_X];
        }

        final boolean wasHandled = handleAllTouchEvents ? false : super.onTouchEvent(event);

        if (!wasHandled){
            //get the current state of the image matrix, its values, and the bounds of the drawn bitmap
            matrix.set(getImageMatrix());
            matrix.getValues(mvalues);
            updateBounds(mvalues);

            scaleDetector.onTouchEvent(event);

            /* if the event is a down touch, or if the number of touch points changed,
            * we should reset our start point, as event origins have likely shifted to a
            * different part of the screen*/
            if (event.getAction() == MotionEvent.ACTION_DOWN ||
                    event.getPointerCount() != previousPointerCount){
                last.set(scaleDetector.getFocusX(), scaleDetector.getFocusY());
            }
            else if (event.getAction() == MotionEvent.ACTION_MOVE){
                if (allowTranslate){
                    //calculate the distance for translation
                    float xdistance = getXDistance(scaleDetector.getFocusX(), last.x);
                    float ydistance = getYDistance(scaleDetector.getFocusY(), last.y);
                    matrix.postTranslate(xdistance, ydistance);
                }

                if (allowScale){
                    matrix.postScale(scaleBy, scaleBy, scaleDetector.getFocusX(), scaleDetector.getFocusY());
                }

                setImageMatrix(matrix);

                last.set(scaleDetector.getFocusX(), scaleDetector.getFocusY());
            }

            if (event.getAction() == MotionEvent.ACTION_UP){
                scaleBy = 1f;
                if (mvalues[Matrix.MSCALE_X] <= startValues[Matrix.MSCALE_X]){
                    animateToStartMatrix();
                }
                else if (mvalues[Matrix.MSCALE_X] > startValues[Matrix.MSCALE_X]){
                    animateTranslationX();
                    animateTranslationY();
                }
            }

            //this tracks whether they have changed the number of fingers down
            previousPointerCount = event.getPointerCount();
        }


        return !wasHandled;
    }

    /**
     * Animate the matrix back to its original position after the user stopped interacting with it.
     */
    private void animateToStartMatrix(){
        //active matrix will change with the animation, begin matrix will not
        final Matrix activeMatrix = new Matrix(getImageMatrix());
        final Matrix beginMatrix = new Matrix(getImageMatrix());

        //difference in current and start values
        final float xsdiff = startValues[Matrix.MSCALE_X] - mvalues[Matrix.MSCALE_X];
        final float ysdiff = startValues[Matrix.MSCALE_Y] - mvalues[Matrix.MSCALE_Y];
        final float xtdiff = startValues[Matrix.MTRANS_X] - mvalues[Matrix.MTRANS_X];
        final float ytdiff = startValues[Matrix.MTRANS_Y] - mvalues[Matrix.MTRANS_Y];

        ValueAnimator anim = ValueAnimator.ofFloat(0, 1f);
        anim.addUpdateListener(new AnimatorUpdateListener(){

            final float[] values = new float[9];

            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                float val = (Float)animation.getAnimatedValue();
                activeMatrix.set(beginMatrix);
                activeMatrix.getValues(values);
                values[Matrix.MTRANS_X] = values[Matrix.MTRANS_X] + xtdiff*val;
                values[Matrix.MTRANS_Y] = values[Matrix.MTRANS_Y] + ytdiff*val;
                values[Matrix.MSCALE_X] = values[Matrix.MSCALE_X] + xsdiff*val;
                values[Matrix.MSCALE_Y] = values[Matrix.MSCALE_Y] + ysdiff*val;
                activeMatrix.setValues(values);
                setImageMatrix(activeMatrix);
            }
        });
        anim.setDuration(200);
        anim.start();
    }

    private void animateTranslationX(){
        if (getCurrentWidth() > getWidth()){
            //the left edge is too far to the interior
            if (bounds.left > 0){
                animateMatrixIndex(Matrix.MTRANS_X, 0);
            }
            //the right edge is too far to the interior
            else if (bounds.right < getWidth()){
                animateMatrixIndex(Matrix.MTRANS_X, bounds.left + getWidth() - bounds.right);
            }
        }
        else{
            //left edge needs to be pulled in, and should be considered before the right edge
            if (bounds.left < 0){
                animateMatrixIndex(Matrix.MTRANS_X, 0);
            }
            //right edge needs to be pulled in
            else if (bounds.right > getWidth()){
                animateMatrixIndex(Matrix.MTRANS_X, bounds.left + getWidth() - bounds.right);
            }
        }
    }

    private void animateTranslationY(){
        if (getCurrentHeight() > getHeight()){
            //the top edge is too far to the interior
            if (bounds.top > 0){
                animateMatrixIndex(Matrix.MTRANS_Y, 0);
            }
            //the bottom edge is too far to the interior
            else if (bounds.bottom < getHeight()){
                animateMatrixIndex(Matrix.MTRANS_Y, bounds.top + getHeight() - bounds.bottom);
            }
        }
        else{
            //top needs to be pulled in, and needs to be considered before the bottom edge
            if (bounds.top < 0){
                animateMatrixIndex(Matrix.MTRANS_Y, 0);
            }
            //bottom edge needs to be pulled in
            else if (bounds.bottom > getHeight()){
                animateMatrixIndex(Matrix.MTRANS_Y, bounds.top + getHeight() - bounds.bottom);
            }
        }
    }

    private void animateMatrixIndex(final int index, final float to){
        ValueAnimator animator = ValueAnimator.ofFloat(mvalues[index], to);
        animator.addUpdateListener(new AnimatorUpdateListener(){

            final float[] values = new float[9];
            Matrix current = new Matrix();

            @Override
            public void onAnimationUpdate(ValueAnimator animation){
                current.set(getImageMatrix());
                current.getValues(values);
                values[index] = (Float)animation.getAnimatedValue();
                current.setValues(values);
                setImageMatrix(current);
            }
        });
        animator.setDuration(200);
        animator.start();
    }

    /**
     * Get the x distance to translate the current image.
     * @param toX the current x location of touch focus
     * @param fromX the last x location of touch focus
     * @return the distance to move the image,
     * will restrict the translation to keep the image on screen.
     */
    private float getXDistance(final float toX, final float fromX){
        float xdistance = toX - fromX;

        return xdistance;
    }

    //todo: incorporate this method into a "restrict bounds" mode
    private float getRestrictedXDistance(final float xdistance){
        float restrictedXDistance = 0;

        if (getCurrentWidth() >= getWidth()){
            if (bounds.left + xdistance > 0 && !scaleDetector.isInProgress()){
                restrictedXDistance = -bounds.left;
            }
            else if (bounds.right + xdistance < getWidth() && !scaleDetector.isInProgress()){
                restrictedXDistance = getWidth() - bounds.right;
            }
        }
        else if (!scaleDetector.isInProgress()){
            if (bounds.left + xdistance < 0){
                restrictedXDistance = -bounds.left;
            }
            else if (bounds.right + xdistance > getWidth()){
                restrictedXDistance = getWidth() - bounds.right;
            }
        }

        return restrictedXDistance;
    }

    /**
     * Get the y distance to translate the current image.
     * @param toY the current y location of touch focus
     * @param fromY the last y location of touch focus
     * @return the distance to move the image,
     * will restrict the translation to keep the image on screen.
     */
    private float getYDistance(final float toY, final float fromY){
        float ydistance = toY - fromY;

        return ydistance;
    }

    //todo: incorporate this method into a "restrict bounds" mode
    private float getRestrictedYDistance(final float ydistance){
        float restrictedYDistance = 0;

        if (getCurrentHeight() >= getHeight()){
            if (bounds.top + ydistance > 0 && !scaleDetector.isInProgress()){
                restrictedYDistance = -bounds.top;
            }
            else if (bounds.bottom + ydistance < getHeight() && !scaleDetector.isInProgress()){
                restrictedYDistance = getHeight() - bounds.bottom;
            }
        }
        else if (!scaleDetector.isInProgress()){
            if (bounds.top + ydistance < 0){
                restrictedYDistance = -bounds.top;
            }
            else if (bounds.bottom + ydistance > getHeight()){
                restrictedYDistance = getHeight() - bounds.bottom;
            }
        }

        return restrictedYDistance;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        //calculate value we should scale by, ultimately the scale will be startScale*scaleFactor
        scaleBy = (startScale*detector.getScaleFactor())/mvalues[Matrix.MSCALE_X];

        //what the scaling should end up at after the transformation
        final float projectedScale = scaleBy * mvalues[Matrix.MSCALE_X];

        //clamp to the min/max if it's going over
        if (projectedScale < minScale){
            scaleBy = minScale / mvalues[Matrix.MSCALE_X];
        }
        else if (projectedScale > maxScale){
            scaleBy = maxScale / mvalues[Matrix.MSCALE_X];
        }

        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        startScale = mvalues[Matrix.MSCALE_X];
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        scaleBy = 1f;
    }
}
