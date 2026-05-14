package com.skylock.ai_cartoon.adapter;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.skylock.ai_cartoon.fragment.LibraryFragment;
import com.skylock.ai_cartoon.model.AlbumModel;
import com.skylock.ai_cartoon.model.ImageModel;

import java.util.ArrayList;
import java.util.List;


/**
 * Java implementation of LibraryPagerAdapter for ViewPager2.
 * Manages the fragments representing different photo albums in the library.
 */
public final class LibraryPagerAdapter extends FragmentStateAdapter {

    private final List<AlbumModel> albumModels;
    private final String style;
    private final int positionImg;

    public LibraryPagerAdapter(@NonNull FragmentManager fragmentManager,
                               @NonNull Lifecycle lifecycle,
                               @NonNull List<AlbumModel> albumModels,
                               @NonNull String style,
                               int positionImg) {
        super(fragmentManager, lifecycle);
        this.albumModels = albumModels;
        this.style = style;
        this.positionImg = positionImg;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Validation check for empty data or out-of-bounds positions
        if (this.albumModels.isEmpty() || position < 0 || position >= this.albumModels.size()) {
            return LibraryFragment.newInstance(position, new ArrayList<>(), this.style, this.positionImg);
        }

        AlbumModel albumModel = this.albumModels.get(position);

        // Handling null album models
        if (albumModel == null) {
            return LibraryFragment.newInstance(position, new ArrayList<>(), this.style, this.positionImg);
        }

        // Extracting photos from the album model
        ArrayList<ImageModel> albumPhotos = albumModel.getAlbumPhotos();
        ArrayList<ImageModel> photosList = (albumPhotos != null) ? new ArrayList<>(albumPhotos) : new ArrayList<>();

        return LibraryFragment.newInstance(position, photosList, this.style, this.positionImg);
    }

    /**
     * Retrieves an existing fragment instance from the FragmentManager using the standard
     * ViewPager2 naming convention.
     */
    @Nullable
    public final LibraryFragment getFragmentAtPosition(@NonNull FragmentManager fragmentManager, int position) {
        // ViewPager2 uses "f" + position as the default tag for its fragments
        Fragment fragment = fragmentManager.findFragmentByTag("f" + position);
        if (fragment instanceof LibraryFragment) {
            return (LibraryFragment) fragment;
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return this.albumModels.size();
    }
}