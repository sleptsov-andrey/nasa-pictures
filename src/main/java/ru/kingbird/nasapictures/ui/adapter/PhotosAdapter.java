package ru.kingbird.nasapictures.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ru.kingbird.nasapictures.R;
import ru.kingbird.nasapictures.data.Photo;
import ru.kingbird.nasapictures.data.local.PhotoRepository;
import ru.kingbird.nasapictures.ui.SecondActivity;

public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoViewHolder>{

    private RequestManager glideRequestManager;
    private final List<Photo> items = new ArrayList<>();
    private Context context;

    public PhotosAdapter(RequestManager glideRequestManager, Context context) {
        this.glideRequestManager = glideRequestManager;
        this.context=context.getApplicationContext();
    }

    @NonNull
    @Override
    public PhotoViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item, viewGroup, false);
        return new PhotoViewHolder (view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoViewHolder photoViewHolder, int position) {
        final Photo photo = items.get(position);
        photoViewHolder.bindItem(photo);
    }

    public void replaceItems(@NonNull List<Photo> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public class PhotoViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivPhoto;
        private ProgressBar progressBar;
        private TextView tvCameraName;

        private PhotoViewHolder(@NonNull View itemView) {
            super(itemView);
            findViews(itemView);
        }


        public void bindItem(@NonNull final Photo photo) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = SecondActivity.newIntent(context, photo.getImgSrc());
                    context.startActivity(intent);
                }
            });
            itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.add(getAdapterPosition(), v.getId(), 0, "Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            removeAt(getAdapterPosition());
                            photo.setDeleted(true);
                            Disposable disposable = new PhotoRepository(context).setPhotoDeleted(photo)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Action() {

                                        @Override
                                        public void run() throws Exception {
                                        }
                                    }, new Consumer<Throwable>(

                                    ) {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {

                                        }
                                    });
                            return true;
                        }
                    });
                }
            });

            progressBar.setVisibility(View.VISIBLE);
            tvCameraName.setText(photo.getCamera().getFullName());

            glideRequestManager.load(photo.getImgSrc())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource,
                                                       Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA))
                    .thumbnail(0.3f)
                    .into(ivPhoto);

        }


        private void findViews(@NonNull View itemView) {
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            progressBar = itemView.findViewById(R.id.progress_bar);
            tvCameraName = itemView.findViewById(R.id.tvCameraName);
        }
    }
}
