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

import mpc.utexas.edu.warble2.features.Location;

/**
 * Created by yosef on 3/7/2018.
 */

public class CanvasView extends View{

    private Paint mPathPaint;
    private Paint mLightPaint;
    private Paint mDevicePaint;
    private Paint mDeviceScopePaint;

    private List<Location> pathPoints = new ArrayList<>();
    private List<Location> lightPoints = new ArrayList<>();
    private Location devicePoint=null;
    private Location deviceScope=null;

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

        canvas.drawColor(Color.BLUE);

        Location fromPoint=null;
        for (Location pathPoint: pathPoints) {
            if (fromPoint == null) {
                fromPoint = pathPoint;
                continue;
            }
            canvas.drawLine(fromPoint.getxCoordinate(), fromPoint.getyCoordinate(), pathPoint.getxCoordinate(), pathPoint.getyCoordinate(), this.mPathPaint);
            fromPoint = pathPoint;
        }

        for (Location lightPoint: lightPoints) {
            canvas.drawOval(lightPoint.getxCoordinate() - 5,
                    lightPoint.getyCoordinate() - 5,
                    lightPoint.getxCoordinate() + 5,
                    lightPoint.getyCoordinate() + 5,
                    this.mLightPaint);
        }

        if (!(devicePoint == null)) {
            canvas.drawRect(devicePoint.getxCoordinate() - 5,
                    devicePoint.getyCoordinate() - 5,
                    devicePoint.getxCoordinate() + 5,
                    devicePoint.getyCoordinate() + 5,
                    this.mDevicePaint);
            canvas.drawOval(devicePoint.getxCoordinate() - 100 / 2,
                    devicePoint.getyCoordinate() - 100 / 2,
                    devicePoint.getxCoordinate() + 100 / 2,
                    devicePoint.getyCoordinate() + 100 / 2,
                    this.mDeviceScopePaint);
        }
    }

    public void init(@Nullable AttributeSet attrs) {
        mPathPaint = new Paint();
        mPathPaint.setColor(Color.RED);

        mLightPaint = new Paint();
        mLightPaint.setColor(Color.GREEN);

        mDevicePaint = new Paint();
        mDevicePaint.setColor(Color.GRAY);

        mDeviceScopePaint = new Paint();
        mDeviceScopePaint.setColor(Color.GRAY);
        mDeviceScopePaint.setStyle(Paint.Style.STROKE);
    }

    public void setPathPoints(List<Location> pathPoints) {
        this.pathPoints = pathPoints;
        invalidate();
    }

    public void setLightPoints(List<Location> lightPoints) {
        this.lightPoints = lightPoints;
        invalidate();
    }

    public void setDevicePoint(@Nullable Location devicePoint) {
        this.devicePoint = devicePoint;
        invalidate();
    }
}
