package mpc.utexas.edu.warble2.ui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yosef on 3/7/2018.
 */

public class CanvasView extends View {

    private Paint mPathPaint;
    private Paint mLightPaint;
    private Paint mDevicePaint;
    private Paint mDeviceScopePaint;

    private List<Point> pathPoints = new ArrayList<>();
    private List<Point> lightPoints = new ArrayList<>();
    private Point devicePoint=null;
    private int deviceScope;

    private int lightPointSize = 10;
    private int devicePointSize = 10;
    private boolean flipY = true;

    public CanvasView(Context context) {
        super(context);
        init(null);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CanvasView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.lightPointSize = this.getHeight() / 30;
        this.devicePointSize = this.getHeight() / 35;

        canvas.drawColor(Color.DKGRAY);

        Point fromPoint=null;
        if (flipY) {
            for (Point pathPoint: pathPoints) {
                if (fromPoint == null) {
                    fromPoint = pathPoint;
                    continue;
                }
                canvas.drawLine(fromPoint.x, this.getHeight() - fromPoint.y, pathPoint.x, this.getHeight() - pathPoint.y, this.mPathPaint);
                fromPoint = pathPoint;
            }

            for (Point lightPoint: lightPoints) {
                canvas.drawOval(
                        lightPoint.x - lightPointSize,
                        this.getHeight() - lightPoint.y - lightPointSize,
                        lightPoint.x + lightPointSize,
                        this.getHeight() - lightPoint.y + lightPointSize,
                        this.mLightPaint);
            }

            if (!(devicePoint == null)) {
                canvas.drawRect(
                        devicePoint.x - devicePointSize,
                        this.getHeight() - devicePoint.y - devicePointSize,
                        devicePoint.x + devicePointSize,
                        this.getHeight() - devicePoint.y + devicePointSize,
                        this.mDevicePaint);
                canvas.drawOval(
                        devicePoint.x - deviceScope,
                        this.getHeight() - devicePoint.y - deviceScope,
                        devicePoint.x + deviceScope,
                        this.getHeight() - devicePoint.y + deviceScope,
                        this.mDeviceScopePaint);
            }
        } else {
            for (Point pathPoint: pathPoints) {
                if (fromPoint == null) {
                    fromPoint = pathPoint;
                    continue;
                }
                canvas.drawLine(fromPoint.x, fromPoint.y, pathPoint.x, pathPoint.y, this.mPathPaint);
                fromPoint = pathPoint;
            }

            for (Point lightPoint: lightPoints) {
                canvas.drawOval(lightPoint.x - lightPointSize,
                        lightPoint.y - lightPointSize,
                        lightPoint.x + lightPointSize,
                        lightPoint.y + lightPointSize,
                        this.mLightPaint);
            }

            if (!(devicePoint == null)) {
                canvas.drawRect(devicePoint.x - devicePointSize,
                        devicePoint.y - devicePointSize,
                        devicePoint.x + devicePointSize,
                        devicePoint.y + devicePointSize,
                        this.mDevicePaint);
                canvas.drawOval(devicePoint.x - deviceScope,
                        devicePoint.y - deviceScope,
                        devicePoint.x + deviceScope,
                        devicePoint.y + deviceScope,
                        this.mDeviceScopePaint);
            }
        }
    }

    public void init(@Nullable AttributeSet attrs) {
        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(Color.RED);

        mLightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLightPaint.setColor(Color.GREEN);

        mDevicePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDevicePaint.setColor(Color.GRAY);

        mDeviceScopePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDeviceScopePaint.setColor(Color.GRAY);
        mDeviceScopePaint.setStyle(Paint.Style.STROKE);
    }

    public void setPathPoints(List<Point> pathPoints) {
        this.pathPoints = pathPoints;
        invalidate();
    }

    public void setLightPoints(List<Point> lightPoints) {
        this.lightPoints = lightPoints;
        invalidate();
    }

    public void setDevicePoint(@Nullable Point devicePoint) {
        this.devicePoint = devicePoint;
        invalidate();
    }

    public void setDeviceScope(int scope) {
        this.deviceScope = scope;
    }

    public void setFlipY(Boolean flipY) {
        this.flipY = flipY;
    }

    public void setLightPointSize(int lightPointSize) {
        this.lightPointSize = lightPointSize;
    }

    public void setDevicePointSize(int devicePointSize) {
        this.devicePointSize = devicePointSize;
    }
}
