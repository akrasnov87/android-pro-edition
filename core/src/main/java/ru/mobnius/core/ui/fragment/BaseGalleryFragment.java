package ru.mobnius.core.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ru.mobnius.core.NamesCore;
import ru.mobnius.core.R;
import ru.mobnius.core.adapter.BaseSpinnerAdapter;
import ru.mobnius.core.adapter.PhotoAdapter;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.data.gallery.BasePhotoManager;
import ru.mobnius.core.data.gallery.OnGalleryItemListener;
import ru.mobnius.core.data.gallery.OnGalleryListener;
import ru.mobnius.core.data.gallery.PhotoDataManager;
import ru.mobnius.core.ui.BaseFragment;
import ru.mobnius.core.ui.CoreFormActivity;
import ru.mobnius.core.ui.component.FooterBar;
import ru.mobnius.core.ui.image.ImageViewActivity;
import ru.mobnius.core.ui.image.ImageItem;
import ru.mobnius.core.utils.DateUtil;

public abstract class BaseGalleryFragment extends BaseFragment
        implements View.OnClickListener, OnGalleryItemListener {

    public final static String IS_CHANGED = "IS_CHANGED";

    private OnGalleryListener mListener;
    private RecyclerView rvList;
    @Nullable
    private PhotoAdapter photoAdapter;
    private TextView tvEmpty;
    private FooterBar mFooterBar;

    private CharSequence mSubTitle;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof OnGalleryListener) {
            mListener = (OnGalleryListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String resultId = requireArguments().getString(NamesCore.RESULT_ID);
        Objects.requireNonNull(getPhotoManager()).updatePictures(getPhotoDataManager().getImages(resultId));
        final List<ImageItem> imageItems = new ArrayList<>(Arrays.asList(getPhotoManager().getImages()));

        photoAdapter = new PhotoAdapter(requireContext(), this, imageItems);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);

        rvList = view.findViewById(R.id.gallery_footer_list);
        rvList.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvList.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        if (photoAdapter != null) {
            rvList.setAdapter(photoAdapter);
        }
        tvEmpty = view.findViewById(R.id.gallery_footer_empty);
        mFooterBar = view.findViewById(R.id.gallery_footer_bar);
        mFooterBar.setOnClickListener(this);

        if (getArguments() != null) {
            mFooterBar.setSaveEnabled(getArguments().getBoolean(IS_CHANGED));
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        ActionBar actionBar = Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar());
        mSubTitle = actionBar.getSubtitle();
        actionBar.setSubtitle(R.string.gallery);
    }

    @Override
    public void onStop() {
        super.onStop();
        ActionBar actionBar = Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar());
        actionBar.setSubtitle(mSubTitle);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.footer_bar_save || id == R.id.footer_bar_second_button) {
            requireActivity().getSupportFragmentManager().popBackStackImmediate();
            if (v.getId() == R.id.footer_bar_save) {
                if (mListener != null) {
                    mListener.onSaveFromGallery();
                }
            }
        }
    }

    /**
     * Обновление списка изображения
     */
    public void onAddNewPhoto(final @NonNull ImageItem imageItem) {
        if (!isAdded() || photoAdapter == null) {
            return;
        }
        photoAdapter.addNewItem(imageItem);
        tvEmpty.setVisibility(View.GONE);
        if (getPhotoManager() != null) {
            mFooterBar.setSaveEnabled(getPhotoManager().isChanged());
        }
    }

    /**
     * Обновление списка изображения
     */
    public void onUpdateGallery(final @NonNull ImageItem imageItem) {
        if (!isAdded() || photoAdapter == null) {
            return;
        }
        photoAdapter.updateImage(imageItem);
        if (getPhotoManager() != null) {
            mFooterBar.setSaveEnabled(getPhotoManager().isChanged());
        }
    }

    public void onDeletePhoto(final @NonNull ImageItem imageItem) {
        if (!isAdded() || photoAdapter == null) {
            return;
        }
        photoAdapter.removeImage(imageItem);
        if (getPhotoManager() != null) {
            mFooterBar.setSaveEnabled(getPhotoManager().isChanged());
            if (getPhotoManager().getImages().length == 0) {
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                tvEmpty.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.POINT_PHOTO;
    }

    /**
     * Вспомогательный метод
     */
    private BasePhotoManager getPhotoManager() {
        if (mListener != null) {
            return mListener.getPhotoManager();
        }

        return null;
    }

    @Override
    public void onImageChangeDialog(ImageItem image) {
        PhotoChangeDialogFragment f = new PhotoChangeDialogFragment(image, getPhotoManager(), getPhotoDataManager(), getPhotoTypeAdapter(image.isVideo()));
        f.setPhotoItemListeners(new PhotoChangeDialogFragment.OnPhotoItemListeners() {
            @Override
            public void onPhotoItemChanged(ImageItem image) {
                onUpdateGallery(image);
                if (getPhotoManager() == null) {
                    return;
                }
                getPhotoManager().updatePicture(getPhotoDataManager(), image, image.getType(), image.getNotice());
            }
        });
        f.show(requireActivity().getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onImageView(ImageItem image) {
        if (getPhotoManager() == null) {
            return;
        }
        final boolean isAttachmentNotExist = getPhotoManager().isUpdateAttachment(image.getId());
        final boolean isLoadFromUrl = getPhotoManager().isLoadFromUrl(image.getId());
        requireActivity().startActivityForResult(ImageViewActivity.getIntent(
                requireContext(),
                image.getId(),
                image.getName(),
                image.getRemoteUrl(false),
                DateUtil.convertDateToUserString(image.getDate()),
                image.getTypeName(),
                image.isVideo(),
                getPhotoManager().isTempImage(image) || isAttachmentNotExist, isLoadFromUrl),
                ImageViewActivity.IMAGE_REMOVE_REQUEST_CODE);
    }

    public abstract PhotoDataManager getPhotoDataManager();

    public abstract BaseSpinnerAdapter getPhotoTypeAdapter(boolean isVideo);

    public CoreFormActivity getFormActivity() {
        if (getActivity() instanceof CoreFormActivity) {
            return (CoreFormActivity) getActivity();
        } else return null;
    }
}

