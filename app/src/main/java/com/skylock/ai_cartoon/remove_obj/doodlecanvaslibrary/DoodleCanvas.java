package com.skylock.ai_cartoon.remove_obj.doodlecanvaslibrary;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

import kotlin.jvm.internal.Intrinsics;


public final class DoodleCanvas extends View {
    public static final int $stable = 8;
    private final ArrayList<PathPojo> backupPathList;
    private boolean canDraw;
    private final Paint lastPaintStroke;
    private OnTouchEventListener listener;
    private Paint mPaint;
    private SerializablePath mPath;
    private final ArrayList<PathPojo> pathList;
    private final ArrayList<PathPojo> pathRedoList;

    public DoodleCanvas(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.canDraw = true;
        this.mPaint = new Paint();
        this.lastPaintStroke = new Paint();
        this.pathList = new ArrayList<>();
        this.pathRedoList = new ArrayList<>();
        this.backupPathList = new ArrayList<>();
        this.mPaint.setColor(-65536);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeWidth(10.0f);
        this.mPath = new SerializablePath();
    }

    public void setOnListener(OnTouchEventListener listener) {
        this.listener = listener;
    }

    public boolean isChange() {
        return !this.pathList.isEmpty();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Intrinsics.checkNotNullParameter(canvas, "canvas");
        super.onDraw(canvas);
        Iterator<PathPojo> it2 = this.pathList.iterator();
        while (it2.hasNext()) {
            PathPojo next = it2.next();
            Log.d("PathVal", next.toString());
            SerializablePath path = next.getPath();
            this.mPaint.setColor(next.getColor());
            this.mPaint.setStrokeWidth(next.getStrokeWidth());
            canvas.drawPath(path, this.mPaint);
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        Intrinsics.checkNotNullParameter(event, "event");
        int action = event.getAction();
        if (action == 0) {
            OnTouchEventListener onTouchEventListener = this.listener;
            if (onTouchEventListener != null) {
                onTouchEventListener.onTouchEvent(event);
            }
            if (!this.canDraw) {
                return false;
            }
            SerializablePath serializablePath = new SerializablePath();
            this.mPath = serializablePath;
            serializablePath.moveTo(event.getX(), event.getY());
            this.pathList.add(new PathPojo(this.mPath, this.mPaint.getColor(), this.mPaint.getStrokeWidth()));
            invalidate();
            return true;
        }
        if (action != 1) {
            if (action != 2 || !this.canDraw) {
                return false;
            }
            this.mPath.lineTo(event.getX(), event.getY());
            invalidate();
            return true;
        }
        OnTouchEventListener onTouchEventListener2 = this.listener;
        if (onTouchEventListener2 == null) {
            return false;
        }
        onTouchEventListener2.onUpdateChange();
        return false;
    }

    public void setStrokeColor(int color) {
        this.mPaint.setColor(color);
    }

    public void setStrokeWidth(float strokeWidth) {
        this.mPaint.setStrokeWidth(strokeWidth);
    }

    public void undoMove() {
        if (pathList == null || pathList.isEmpty()) {
            return;
        }

        int lastIndex = pathList.size() - 1;
        PathPojo lastPath = pathList.remove(lastIndex);
        pathRedoList.add(lastPath);

        if (listener != null) {
            listener.onUpdateChange();
        }

        invalidate();
    }


    public void redoMove() {
        if (pathRedoList == null || pathRedoList.isEmpty()) {
            return;
        }

        int lastIndex = pathRedoList.size() - 1;
        PathPojo lastRedoPath = pathRedoList.remove(lastIndex);
        pathList.add(lastRedoPath);

        if (listener != null) {
            listener.onUpdateChange();
        }

        invalidate();
    }


    public void enableErasing() {
        storeLastPaintStroke(this.mPaint);
        this.mPaint.setColor(-1);
    }

    private void storeLastPaintStroke(Paint lastPaint) {
        this.lastPaintStroke.setStrokeWidth(lastPaint.getStrokeWidth());
        this.lastPaintStroke.setColor(lastPaint.getColor());
    }

    public void enablePainting() {
        this.mPaint = this.lastPaintStroke;
    }

    public void clearCanvas() {
        this.backupPathList.clear();
        this.backupPathList.addAll(this.pathList);
        this.pathList.clear();
        invalidate();
        OnTouchEventListener onTouchEventListener = this.listener;
        if (onTouchEventListener != null) {
            onTouchEventListener.onUpdateChange();
        }
    }

    public void updateCanvas(ArrayList<PathPojo> newPathList) {
        Intrinsics.checkNotNullParameter(newPathList, "newPathList");
        this.pathList.clear();
        this.pathList.addAll(newPathList);
        invalidate();
    }

    public void canUserDraw(boolean canDraw) {
        this.canDraw = canDraw;
    }
}
