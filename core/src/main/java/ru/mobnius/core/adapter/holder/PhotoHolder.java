package ru.mobnius.core.adapter.holder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import ru.mobnius.core.R;
import ru.mobnius.core.data.gallery.OnGalleryItemListener;
import ru.mobnius.core.ui.image.ImageItem;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.LocationUtil;
import ru.mobnius.core.utils.StringUtil;

public class PhotoHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
    @NonNull
    private final OnGalleryItemListener changeListener;
    @NonNull
    private final ImageView ivImage;
    @NonNull
    private final ProgressBar pbLoading;
    @NonNull
    private final TextView tvType;
    @NonNull
    private final TextView tvDate;
    @NonNull
    private final TextView tvNotice;
    @NonNull
    private final TextView tvLocation;
    @NonNull
    private final TextView tvViolation;

    @Nullable
    private ImageItem mImage;

    public PhotoHolder(@NonNull View itemView, @NonNull OnGalleryItemListener listener) {
        super(itemView);

        changeListener = listener;
        itemView.setOnClickListener(this);
        pbLoading = itemView.findViewById(R.id.item_photo_loading);
        ivImage = itemView.findViewById(R.id.item_photo_thumb);
        ivImage.setOnClickListener(v -> changeListener.onImageView(mImage));

        tvType = itemView.findViewById(R.id.item_photo_name);
        tvDate = itemView.findViewById(R.id.item_photo_date);
        tvNotice = itemView.findViewById(R.id.item_photo_notice);
        tvViolation = itemView.findViewById(R.id.item_photo_violation);

        tvLocation = itemView.findViewById(R.id.item_photo_location);
        tvLocation.setOnClickListener(v -> changeListener.onImageMap(mImage));
    }

    public void bindPhoto(ImageItem image) {
        mImage = image;

        byte[] thumbPhoto = mImage.getBytes();
        if (thumbPhoto != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(thumbPhoto, 0, thumbPhoto.length);
            ivImage.setImageBitmap(bitmap);
            ivImage.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);
        }
        if (mImage.isErrorLoading()) {
            ivImage.setImageResource(R.drawable.ic_broken_image_24);
            ivImage.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);
        }
        tvType.setText(mImage.getTypeName());
        ivImage.setContentDescription(mImage.getTypeName());
        String userDate = DateUtil.convertDateToUserString(new Date(mImage.getLocation().getTime()), "HH:mm:ss");

        tvDate.setText(userDate);
        tvLocation.setText(LocationUtil.toString(mImage.getLocation()));

        if (!mImage.getNotice().isEmpty()) {
            tvNotice.setVisibility(View.VISIBLE);
            tvNotice.setText(mImage.getNotice());
        } else {
            tvNotice.setVisibility(View.GONE);
            tvNotice.setText("");
        }

        if (!StringUtil.isEmptyOrNull(mImage.getResultTypeName())) {
            tvViolation.setVisibility(View.VISIBLE);
            tvViolation.setText(mImage.getResultTypeName());
        } else {
            tvViolation.setVisibility(View.GONE);
            tvViolation.setText("");
        }
    }

    @Override
    public void onClick(View v) {
        changeListener.onImageChangeDialog(mImage);
    }
}
