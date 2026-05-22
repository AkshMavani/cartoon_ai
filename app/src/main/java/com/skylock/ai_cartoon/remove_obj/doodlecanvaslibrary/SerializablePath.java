package com.skylock.ai_cartoon.remove_obj.doodlecanvaslibrary;

import android.graphics.Path;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* compiled from: SerializablePath.kt */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010\u0014\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0007\b\u0016¢\u0006\u0002\u0010\u0003B\u000f\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0000¢\u0006\u0002\u0010\u0005J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\bJ\u0006\u0010\r\u001a\u00020\u000bR\u001e\u0010\u0006\u001a\u0012\u0012\u0004\u0012\u00020\b0\u0007j\b\u0012\u0004\u0012\u00020\b`\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lmobi/zeezoo/photoenhancer/feature/widget/doodlecanvaslibrary/SerializablePath;", "Landroid/graphics/Path;", "Ljava/io/Serializable;", "()V", "p", "(Lmobi/zeezoo/photoenhancer/feature/widget/doodlecanvaslibrary/SerializablePath;)V", "pathPoints", "Ljava/util/ArrayList;", "", "Lkotlin/collections/ArrayList;", "addPathPoints", "", "points", "loadPathPointsAsQuadTo", "Aiphoto_v3.6.219_v219_10.31.2025_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
/* loaded from: classes3.dex */
public final class SerializablePath extends Path implements Serializable {
    public static final int $stable = 8;
    private final ArrayList<float[]> pathPoints;

    public SerializablePath() {
        this.pathPoints = new ArrayList<>();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SerializablePath(SerializablePath p) {
        super(p);
        Intrinsics.checkNotNullParameter(p, "p");
        this.pathPoints = p.pathPoints;
    }

    public void addPathPoints(float[] points) {
        Intrinsics.checkNotNullParameter(points, "points");
        this.pathPoints.add(points);
    }

    public void loadPathPointsAsQuadTo() {
        float[] remove = this.pathPoints.remove(0);
        Intrinsics.checkNotNullExpressionValue(remove, "removeAt(...)");
        float[] fArr = remove;
        moveTo(fArr[0], fArr[1]);
        Iterator<float[]> it2 = this.pathPoints.iterator();
        while (it2.hasNext()) {
            float[] next = it2.next();
            quadTo(next[0], next[1], next[2], next[3]);
        }
    }
}
