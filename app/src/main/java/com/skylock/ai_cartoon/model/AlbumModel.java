package com.skylock.ai_cartoon.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Objects;

/**
 * AlbumModel — fully compatible with all GalleryUtils constructor call sites:
 *
 *   3-param:   new AlbumModel(photoList, coverUri, "All Photo")
 *   5-param:   new AlbumModel(null, img.getPhotoUri(), albumName, 1, null)
 *
 * The 5-param constructor matches the original Kotlin data class:
 *   AlbumModel(albumPhotos, coverUri, name, count, extra)
 * where count and extra are ignored extras from the decompiled Kotlin.
 */
public class AlbumModel implements Parcelable {

    @SerializedName("albumPhotos")
    private ArrayList<ImageModel> albumPhotos;

    @SerializedName("coverUri")
    private String coverUri;

    @SerializedName("name")
    private String name;

    // ------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------

    /** No-arg constructor. */
    public AlbumModel() {
        this.albumPhotos = new ArrayList<>();
        this.coverUri    = null;
        this.name        = null;
    }

    /**
     * 3-param constructor — used by GalleryUtils "All Photo" album:
     *   new AlbumModel(allImages, firstPhotoUri, "All Photo")
     *   new AlbumModel(new ArrayList<>(), "", "All Photo")
     */
    public AlbumModel(ArrayList<ImageModel> albumPhotos, String coverUri, String name) {
        this.albumPhotos = (albumPhotos != null) ? albumPhotos : new ArrayList<>();
        this.coverUri    = coverUri;
        this.name        = name;
    }

    /**
     * 5-param constructor — used by GalleryUtils per-folder album creation:
     *   new AlbumModel(null, img.getPhotoUri(), albumName, 1, null)
     *
     * @param albumPhotos  initial photos list (null → empty list auto-created)
     * @param coverUri     cover image URI
     * @param name         album / folder name
     * @param count        ignored (Kotlin data class extra field, kept for source compat)
     * @param extra        ignored (always null in all call sites)
     */
    public AlbumModel(
            ArrayList<ImageModel> albumPhotos,
            String coverUri,
            String name,
            int count,          // kept for call-site compatibility — not stored
            Object extra        // kept for call-site compatibility — not stored
    ) {
        this.albumPhotos = (albumPhotos != null) ? albumPhotos : new ArrayList<>();
        this.coverUri    = coverUri;
        this.name        = name;
    }

    // ------------------------------------------------------------------
    // Parcelable constructor
    // ------------------------------------------------------------------

    protected AlbumModel(Parcel in) {
        albumPhotos = in.createTypedArrayList(ImageModel.CREATOR);
        coverUri    = in.readString();
        name        = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(albumPhotos);
        dest.writeString(coverUri);
        dest.writeString(name);
    }

    @Override
    public int describeContents() { return 0; }

    public static final Creator<AlbumModel> CREATOR = new Creator<AlbumModel>() {
        @Override
        public AlbumModel createFromParcel(Parcel in) { return new AlbumModel(in); }
        @Override
        public AlbumModel[] newArray(int size)        { return new AlbumModel[size]; }
    };

    // ------------------------------------------------------------------
    // Getters & Setters
    // ------------------------------------------------------------------

    public ArrayList<ImageModel> getAlbumPhotos()                          { return albumPhotos; }
    public void                  setAlbumPhotos(ArrayList<ImageModel> list){ this.albumPhotos = list; }

    public String getCoverUri()                 { return coverUri; }
    public void   setCoverUri(String coverUri)  { this.coverUri = coverUri; }

    public String getName()                     { return name; }
    public void   setName(String name)          { this.name = name; }

    // ------------------------------------------------------------------
    // Utility
    // ------------------------------------------------------------------

    /** Returns a shallow copy of this album (photos list items not deep-copied). */
    public AlbumModel deepCopy() {
        ArrayList<ImageModel> copy = new ArrayList<>();
        if (albumPhotos != null) copy.addAll(albumPhotos);
        return new AlbumModel(copy, coverUri, name);
    }

    // ------------------------------------------------------------------
    // equals / hashCode / toString
    // ------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlbumModel)) return false;
        AlbumModel that = (AlbumModel) o;
        return Objects.equals(albumPhotos, that.albumPhotos)
                && Objects.equals(coverUri, that.coverUri)
                && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(albumPhotos, coverUri, name);
    }

    @Override
    public String toString() {
        return "AlbumModel{"
                + "albumPhotos=" + albumPhotos
                + ", coverUri='" + coverUri + '\''
                + ", name='" + name + '\''
                + '}';
    }
}