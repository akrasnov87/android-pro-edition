package com.mobwal.android.library;

import androidx.fragment.app.Fragment;

public abstract class FragmentRunnable
        implements Runnable {

    private final Fragment mFragment;

    public FragmentRunnable(Fragment fragment) {
        mFragment = fragment;
    }

    @Override
    public void run() {
        if(!mFragment.isAdded()) {
            return;
        }

        inRun();
    }

    public abstract void inRun();
}
