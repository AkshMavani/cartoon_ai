package com.skylock.ai_cartoon.model;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class DemoLibraryModel implements Parcelable {

    @SerializedName("feature")
    private final String feature;

    @SerializedName("before")
    private final String before;

    @SerializedName("after")
    private final String after;

    @SerializedName("preview")
    private final String preview;

    @SerializedName("width")
    private final Integer width;

    @SerializedName("height")
    private final Integer height;

    // ─── Constructors ────────────────────────────────────────────────────────────

    public DemoLibraryModel() {
        this(null, null, null, null, null, null);
    }

    public DemoLibraryModel(String feature, String before, String after,
                            String preview, Integer width, Integer height) {
        this.feature = feature;
        this.before  = before;
        this.after   = after;
        this.preview = preview;
        this.width   = width;
        this.height  = height;
    }

    // ─── Getters ─────────────────────────────────────────────────────────────────

    public String getFeature() { return feature; }
    public String getBefore()  { return before;  }
    public String getAfter()   { return after;   }
    public String getPreview() { return preview; }
    public Integer getWidth()  { return width;   }
    public Integer getHeight() { return height;  }

    // ─── Copy (equivalent to Kotlin data class copy()) ───────────────────────────

    public DemoLibraryModel copy(String feature, String before, String after,
                                 String preview, Integer width, Integer height) {
        return new DemoLibraryModel(feature, before, after, preview, width, height);
    }

    // ─── equals() ────────────────────────────────────────────────────────────────

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof DemoLibraryModel)) return false;
        DemoLibraryModel that = (DemoLibraryModel) other;
        return Objects.equals(feature, that.feature)
                && Objects.equals(before,  that.before)
                && Objects.equals(after,   that.after)
                && Objects.equals(preview, that.preview)
                && Objects.equals(width,   that.width)
                && Objects.equals(height,  that.height);
    }

    // ─── hashCode() ──────────────────────────────────────────────────────────────

    @Override
    public int hashCode() {
        int result = Objects.hashCode(feature);
        result = 31 * result + Objects.hashCode(before);
        result = 31 * result + Objects.hashCode(after);
        result = 31 * result + Objects.hashCode(preview);
        result = 31 * result + Objects.hashCode(width);
        result = 31 * result + Objects.hashCode(height);
        return result;
    }

    // ─── toString() ──────────────────────────────────────────────────────────────

    @Override
    public String toString() {
        return "DemoLibraryModel(" +
                "feature=" + feature +
                ", before=" + before +
                ", after=" + after +
                ", preview=" + preview +
                ", width=" + width +
                ", height=" + height +
                ")";
    }

    // ─── Parcelable ──────────────────────────────────────────────────────────────

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(feature);
        parcel.writeString(before);
        parcel.writeString(after);
        parcel.writeString(preview);

        if (width == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            parcel.writeInt(width);
        }

        if (height == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            parcel.writeInt(height);
        }
    }

    public static final Parcelable.Creator<DemoLibraryModel> CREATOR =
            new Parcelable.Creator<DemoLibraryModel>() {

                @Override
                public DemoLibraryModel createFromParcel(Parcel parcel) {
                    String feature = parcel.readString();
                    String before  = parcel.readString();
                    String after   = parcel.readString();
                    String preview = parcel.readString();

                    Integer width  = parcel.readInt() == 0 ? null : parcel.readInt();
                    Integer height = parcel.readInt() == 0 ? null : parcel.readInt();

                    return new DemoLibraryModel(feature, before, after, preview, width, height);
                }

                @Override
                public DemoLibraryModel[] newArray(int size) {
                    return new DemoLibraryModel[size];
                }
            };
}