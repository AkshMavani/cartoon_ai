package com.skylock.ai_cartoon.remove_obj.doodlecanvaslibrary;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

public final class PathPojo implements Serializable, Parcelable {
    /* renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    public static final int $stable = 8;
    private static final Creator<PathPojo> CREATOR = new Creator<PathPojo>() { // from class: mobi.zeezoo.photoenhancer.feature.widget.doodlecanvaslibrary.PathPojo$Companion$CREATOR$1
        @Override // android.os.Parcelable.Creator
        public PathPojo createFromParcel(Parcel source) {
            Intrinsics.checkNotNullParameter(source, "source");
            return new PathPojo(source);
        }

        @Override // android.os.Parcelable.Creator
        public PathPojo[] newArray(int size) {
            return new PathPojo[size];
        }
    };
    private int color;
    private SerializablePath path;
    private float strokeWidth;

    /* JADX INFO: Access modifiers changed from: protected */
    public PathPojo(Parcel incoming) {
        Intrinsics.checkNotNullParameter(incoming, "incoming");
        Serializable readSerializable = incoming.readSerializable();
        Intrinsics.checkNotNull(readSerializable, "null cannot be cast to non-null type mobi.zeezoo.photoenhancer.feature.widget.doodlecanvaslibrary.SerializablePath");
        this.path = (SerializablePath) readSerializable;
        this.color = incoming.readInt();
        this.strokeWidth = incoming.readFloat();
    }

    public PathPojo(SerializablePath path, int i, float f) {
        Intrinsics.checkNotNullParameter(path, "path");
        this.path = path;
        this.color = i;
        this.strokeWidth = f;
    }

    public PathPojo() {
        this.path = new SerializablePath();
        this.color = 0;
        this.strokeWidth = 10.0f;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SerializablePath getPath() {
        return this.path;
    }

    public void setPath(SerializablePath serializablePath) {
        Intrinsics.checkNotNullParameter(serializablePath, "<set-?>");
        this.path = serializablePath;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int i) {
        this.color = i;
    }

    public float getStrokeWidth() {
        return this.strokeWidth;
    }

    public void setStrokeWidth(float f) {
        this.strokeWidth = f;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        Intrinsics.checkNotNullParameter(dest, "dest");
        dest.writeSerializable(this.path);
        dest.writeInt(this.color);
        dest.writeFloat(this.strokeWidth);
    }

    public String toString() {
        return "PathPojo(path=" + this.path + ", color=" + this.color + ", strokeWidth=" + this.strokeWidth;
    }

    /* compiled from: PathPojo.kt */
    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002R\u0017\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"}, d2 = {"Lmobi/zeezoo/photoenhancer/feature/widget/doodlecanvaslibrary/PathPojo$Companion;", "", "()V", "CREATOR", "Landroid/os/Parcelable$Creator;", "Lmobi/zeezoo/photoenhancer/feature/widget/doodlecanvaslibrary/PathPojo;", "getCREATOR", "()Landroid/os/Parcelable$Creator;", "Aiphoto_v3.6.219_v219_10.31.2025_release"}, k = 1, mv = {1, 9, 0}, xi = 48)
    /* loaded from: classes.dex */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public Creator<PathPojo> getCREATOR() {
            return PathPojo.CREATOR;
        }
    }
}
