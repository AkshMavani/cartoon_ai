package com.skylock.ai_cartoon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * ImageModel — clean Java, fully compatible with GalleryUtils call sites:
 *
 *   18-param constructor:
 *     new ImageModel(null, title, null, albumName, filePath,
 *                    null, false, 0,
 *                    null, null, false, false,
 *                    null, null, null, null, null, null)
 *
 *   20-param bitmask constructor (from decompiled Kotlin):
 *     new ImageModel(null, title, null, albumName, filePath,
 *                    null, false, 0,
 *                    null, null, false, false,
 *                    null, null, null, null, null, null,
 *                    262117, null)
 */
public final class ImageModel implements Parcelable {

    // ------------------------------------------------------------------
    // Fields
    // ------------------------------------------------------------------

    @SerializedName("id")
    private Integer id;

    @SerializedName("title")
    private final String title;

    @SerializedName("bucketId")
    private Long bucketId;

    @SerializedName("albumName")
    private String albumName;

    @SerializedName("photoUri")
    private String photoUri;

    @SerializedName("duration")
    private final Long duration;

    @SerializedName("isSelected")
    private boolean isSelected;

    @SerializedName("position")
    private int position;

    private Integer selectNumber;
    private Integer icon;
    private boolean isCamera;
    private boolean isAddPhoto;
    private Integer width;
    private Integer height;
    private String  path;
    private String  uri;
    private String  uriDemoAfter;
    private String  uriDemoPreview;

    // ------------------------------------------------------------------
    // No-arg constructor
    // ------------------------------------------------------------------

    public ImageModel() {
        this.id             = null;
        this.title          = null;
        this.bucketId       = null;
        this.albumName      = null;
        this.photoUri       = "";
        this.duration       = null;
        this.isSelected     = false;
        this.position       = -1;
        this.selectNumber   = null;
        this.icon           = null;
        this.isCamera       = false;
        this.isAddPhoto     = false;
        this.width          = null;
        this.height         = null;
        this.path           = null;
        this.uri            = null;
        this.uriDemoAfter   = null;
        this.uriDemoPreview = null;
    }

    // ------------------------------------------------------------------
    // 18-param constructor  ← used by GalleryUtils directly
    // ------------------------------------------------------------------

    public ImageModel(
            Integer id,
            String  title,
            Long    bucketId,
            String  albumName,
            String  photoUri,
            Long    duration,
            boolean isSelected,
            int     position,
            Integer selectNumber,
            Integer icon,
            boolean isCamera,
            boolean isAddPhoto,
            Integer width,
            Integer height,
            String  path,
            String  uri,
            String  uriDemoAfter,
            String  uriDemoPreview
    ) {
        this.id             = id;
        this.title          = title;
        this.bucketId       = bucketId;
        this.albumName      = albumName;
        this.photoUri       = (photoUri != null) ? photoUri : "";
        this.duration       = duration;
        this.isSelected     = isSelected;
        this.position       = position;
        this.selectNumber   = selectNumber;
        this.icon           = icon;
        this.isCamera       = isCamera;
        this.isAddPhoto     = isAddPhoto;
        this.width          = width;
        this.height         = height;
        this.path           = path;
        this.uri            = uri;
        this.uriDemoAfter   = uriDemoAfter;
        this.uriDemoPreview = uriDemoPreview;
    }

    // ------------------------------------------------------------------
    // 20-param bitmask constructor  ← called from decompiled Kotlin:
    //   new ImageModel(..., 262117, null)  or  new ImageModel(..., 262085, null)
    //
    // bitmask bit meanings (Kotlin compiler convention):
    //   bit 0  (0x00001) → id           defaults to null
    //   bit 1  (0x00002) → title        defaults to null
    //   bit 2  (0x00004) → bucketId     defaults to null
    //   bit 3  (0x00008) → albumName    defaults to null
    //   bit 4  (0x00010) → photoUri     defaults to ""
    //   bit 5  (0x00020) → duration     defaults to null
    //   bit 6  (0x00040) → isSelected   defaults to false
    //   bit 7  (0x00080) → position     defaults to -1
    //   bit 8  (0x00100) → selectNumber defaults to null
    //   bit 9  (0x00200) → icon         defaults to null
    //   bit 10 (0x00400) → isCamera     defaults to false
    //   bit 11 (0x00800) → isAddPhoto   defaults to false
    //   bit 12 (0x01000) → width        defaults to null
    //   bit 13 (0x02000) → height       defaults to null
    //   bit 14 (0x04000) → path         defaults to null
    //   bit 15 (0x08000) → uri          defaults to null
    //   bit 16 (0x10000) → uriDemoAfter defaults to null
    //   bit 17 (0x20000) → uriDemoPreview defaults to null
    //
    // 262117 = 0x3FFE5 → bits 0,1,2,5,6,7,8,9,10,11,12,13,14,15,16,17 set
    //                    (id, title, bucketId, duration and all nullable extras defaulted)
    // 262085 = 0x3FFC5 → same but bit 5 clear (duration is supplied)
    // ------------------------------------------------------------------

    public ImageModel(
            Integer id,
            String  title,
            Long    bucketId,
            String  albumName,
            String  photoUri,
            Long    duration,
            boolean isSelected,
            int     position,
            Integer selectNumber,
            Integer icon,
            boolean isCamera,
            boolean isAddPhoto,
            Integer width,
            Integer height,
            String  path,
            String  uri,
            String  uriDemoAfter,
            String  uriDemoPreview,
            int     bitmask,
            Object  defaultConstructorMarker   // always null — mirrors Kotlin's DefaultConstructorMarker
    ) {
        this(
                (bitmask & 0x00001) != 0 ? null  : id,
                (bitmask & 0x00002) != 0 ? null  : title,
                (bitmask & 0x00004) != 0 ? null  : bucketId,
                (bitmask & 0x00008) != 0 ? null  : albumName,
                (bitmask & 0x00010) != 0 ? ""    : photoUri,
                (bitmask & 0x00020) != 0 ? null  : duration,
                (bitmask & 0x00040) != 0 ? false : isSelected,
                (bitmask & 0x00080) != 0 ? -1    : position,
                (bitmask & 0x00100) != 0 ? null  : selectNumber,
                (bitmask & 0x00200) != 0 ? null  : icon,
                (bitmask & 0x00400) != 0 ? false : isCamera,
                (bitmask & 0x00800) != 0 ? false : isAddPhoto,
                (bitmask & 0x01000) != 0 ? null  : width,
                (bitmask & 0x02000) != 0 ? null  : height,
                (bitmask & 0x04000) != 0 ? null  : path,
                (bitmask & 0x08000) != 0 ? null  : uri,
                (bitmask & 0x10000) != 0 ? null  : uriDemoAfter,
                (bitmask & 0x20000) != 0 ? null  : uriDemoPreview
        );
    }

    // ------------------------------------------------------------------
    // copy()
    // ------------------------------------------------------------------

    public ImageModel copy(
            Integer id, String title, Long bucketId, String albumName,
            String photoUri, Long duration, boolean isSelected, int position,
            Integer selectNumber, Integer icon, boolean isCamera, boolean isAddPhoto,
            Integer width, Integer height, String path, String uri,
            String uriDemoAfter, String uriDemoPreview
    ) {
        return new ImageModel(id, title, bucketId, albumName, photoUri, duration,
                isSelected, position, selectNumber, icon, isCamera, isAddPhoto,
                width, height, path, uri, uriDemoAfter, uriDemoPreview);
    }

    // ------------------------------------------------------------------
    // Getters & Setters
    // ------------------------------------------------------------------

    public Integer getId()                        { return id; }
    public void    setId(Integer id)              { this.id = id; }

    public String  getTitle()                     { return title; }
    // title is val (final) — no setter

    public Long    getBucketId()                  { return bucketId; }
    public void    setBucketId(Long bucketId)     { this.bucketId = bucketId; }

    public String  getAlbumName()                 { return albumName; }
    public void    setAlbumName(String albumName) { this.albumName = albumName; }

    public String  getPhotoUri()                  { return photoUri; }
    public void    setPhotoUri(String photoUri) {
        if (photoUri == null) throw new NullPointerException("photoUri must not be null");
        this.photoUri = photoUri;
    }

    public Long    getDuration()                  { return duration; }
    // duration is val (final) — no setter

    public boolean isSelected()                   { return isSelected; }
    public void    setSelected(boolean isSelected){ this.isSelected = isSelected; }

    public int     getPosition()                  { return position; }
    public void    setPosition(int position)      { this.position = position; }

    public Integer getSelectNumber()              { return selectNumber; }
    public void    setSelectNumber(Integer n)     { this.selectNumber = n; }

    public Integer getIcon()                      { return icon; }
    public void    setIcon(Integer icon)          { this.icon = icon; }

    public boolean isCamera()                     { return isCamera; }
    public void    setCamera(boolean isCamera)    { this.isCamera = isCamera; }

    public boolean isAddPhoto()                   { return isAddPhoto; }
    public void    setAddPhoto(boolean isAddPhoto){ this.isAddPhoto = isAddPhoto; }

    public Integer getWidth()                     { return width; }
    public void    setWidth(Integer width)        { this.width = width; }

    public Integer getHeight()                    { return height; }
    public void    setHeight(Integer height)      { this.height = height; }

    public String  getPath()                      { return path; }
    public void    setPath(String path)           { this.path = path; }

    public String  getUri()                       { return uri; }
    public void    setUri(String uri)             { this.uri = uri; }

    public String  getUriDemoAfter()              { return uriDemoAfter; }
    public void    setUriDemoAfter(String v)      { this.uriDemoAfter = v; }

    public String  getUriDemoPreview()            { return uriDemoPreview; }
    public void    setUriDemoPreview(String v)    { this.uriDemoPreview = v; }

    // ------------------------------------------------------------------
    // equals / hashCode / toString
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof ImageModel)) return false;
        ImageModel o = (ImageModel) other;
        return Objects.equals(id, o.id)
                && Objects.equals(title, o.title)
                && Objects.equals(bucketId, o.bucketId)
                && Objects.equals(albumName, o.albumName)
                && Objects.equals(photoUri, o.photoUri)
                && Objects.equals(duration, o.duration)
                && isSelected == o.isSelected
                && position   == o.position
                && Objects.equals(selectNumber, o.selectNumber)
                && Objects.equals(icon, o.icon)
                && isCamera   == o.isCamera
                && isAddPhoto == o.isAddPhoto
                && Objects.equals(width, o.width)
                && Objects.equals(height, o.height)
                && Objects.equals(path, o.path)
                && Objects.equals(uri, o.uri)
                && Objects.equals(uriDemoAfter, o.uriDemoAfter)
                && Objects.equals(uriDemoPreview, o.uriDemoPreview);
    }

    @Override
    public int hashCode() {
        int r = Objects.hashCode(id);
        r = 31 * r + Objects.hashCode(title);
        r = 31 * r + Objects.hashCode(bucketId);
        r = 31 * r + Objects.hashCode(albumName);
        r = 31 * r + Objects.hashCode(photoUri);
        r = 31 * r + Objects.hashCode(duration);
        r = 31 * r + (isSelected ? 1 : 0);
        r = 31 * r + Integer.hashCode(position);
        r = 31 * r + Objects.hashCode(selectNumber);
        r = 31 * r + Objects.hashCode(icon);
        r = 31 * r + (isCamera   ? 1 : 0);
        r = 31 * r + (isAddPhoto ? 1 : 0);
        r = 31 * r + Objects.hashCode(width);
        r = 31 * r + Objects.hashCode(height);
        r = 31 * r + Objects.hashCode(path);
        r = 31 * r + Objects.hashCode(uri);
        r = 31 * r + Objects.hashCode(uriDemoAfter);
        r = 31 * r + Objects.hashCode(uriDemoPreview);
        return r;
    }

    @Override
    public String toString() {
        return "ImageModel(id=" + id
                + ", title=" + title
                + ", bucketId=" + bucketId
                + ", albumName=" + albumName
                + ", photoUri=" + photoUri
                + ", duration=" + duration
                + ", isSelected=" + isSelected
                + ", position=" + position
                + ", selectNumber=" + selectNumber
                + ", icon=" + icon
                + ", isCamera=" + isCamera
                + ", isAddPhoto=" + isAddPhoto
                + ", width=" + width
                + ", height=" + height
                + ", path=" + path
                + ", uri=" + uri
                + ", uriDemoAfter=" + uriDemoAfter
                + ", uriDemoPreview=" + uriDemoPreview + ")";
    }

    // ------------------------------------------------------------------
    // Parcelable
    // ------------------------------------------------------------------

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        if (id == null)           { parcel.writeInt(0); }
        else                      { parcel.writeInt(1); parcel.writeInt(id); }
        parcel.writeString(title);
        if (bucketId == null)     { parcel.writeInt(0); }
        else                      { parcel.writeInt(1); parcel.writeLong(bucketId); }
        parcel.writeString(albumName);
        parcel.writeString(photoUri);
        if (duration == null)     { parcel.writeInt(0); }
        else                      { parcel.writeInt(1); parcel.writeLong(duration); }
        parcel.writeInt(isSelected  ? 1 : 0);
        parcel.writeInt(position);
        if (selectNumber == null) { parcel.writeInt(0); }
        else                      { parcel.writeInt(1); parcel.writeInt(selectNumber); }
        if (icon == null)         { parcel.writeInt(0); }
        else                      { parcel.writeInt(1); parcel.writeInt(icon); }
        parcel.writeInt(isCamera   ? 1 : 0);
        parcel.writeInt(isAddPhoto ? 1 : 0);
        if (width == null)        { parcel.writeInt(0); }
        else                      { parcel.writeInt(1); parcel.writeInt(width); }
        if (height == null)       { parcel.writeInt(0); }
        else                      { parcel.writeInt(1); parcel.writeInt(height); }
        parcel.writeString(path);
        parcel.writeString(uri);
        parcel.writeString(uriDemoAfter);
        parcel.writeString(uriDemoPreview);
    }

    public static final Parcelable.Creator<ImageModel> CREATOR = new Parcelable.Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel p) {
            Integer id          = (p.readInt() == 0) ? null : p.readInt();
            String  title       = p.readString();
            Long    bucketId    = (p.readInt() == 0) ? null : p.readLong();
            String  albumName   = p.readString();
            String  photoUri    = p.readString();
            Long    duration    = (p.readInt() == 0) ? null : p.readLong();
            boolean isSelected  = p.readInt() != 0;
            int     position    = p.readInt();
            Integer selectNum   = (p.readInt() == 0) ? null : p.readInt();
            Integer icon        = (p.readInt() == 0) ? null : p.readInt();
            boolean isCamera    = p.readInt() != 0;
            boolean isAddPhoto  = p.readInt() != 0;
            Integer width       = (p.readInt() == 0) ? null : p.readInt();
            Integer height      = (p.readInt() == 0) ? null : p.readInt();
            String  path        = p.readString();
            String  uri         = p.readString();
            String  demoAfter   = p.readString();
            String  demoPreview = p.readString();
            return new ImageModel(id, title, bucketId, albumName, photoUri, duration,
                    isSelected, position, selectNum, icon, isCamera, isAddPhoto,
                    width, height, path, uri, demoAfter, demoPreview);
        }
        @Override
        public ImageModel[] newArray(int size) { return new ImageModel[size]; }
    };
}