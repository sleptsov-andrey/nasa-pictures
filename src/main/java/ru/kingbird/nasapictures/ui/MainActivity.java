package ru.kingbird.nasapictures.ui;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import ru.kingbird.nasapictures.R;
import ru.kingbird.nasapictures.data.Photo;
import ru.kingbird.nasapictures.data.local.PhotoRepository;
import ru.kingbird.nasapictures.data.local.PhotosDatabase;
import ru.kingbird.nasapictures.data.remote.RestApi;
import ru.kingbird.nasapictures.data.remote.dto.LatestPhotos;
import ru.kingbird.nasapictures.ui.adapter.PhotosAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvPhotos;
    private PhotosAdapter photosAdapter;
    private Button btnTryAgain;
    private View viewError;
    private View viewLoading;
    private View viewNoData;
    private TextView tvError;
    private Call loadPhotosRequest;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private PhotoRepository photoRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUi();
        photoRepository = new PhotoRepository(this.getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupUx();
        loadData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindUx();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private void unbindUx() {
        btnTryAgain.setOnClickListener(null);
        cancelCurrentRequestIfNeeded();
    }

    private void setupUx() {
        btnTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickTryAgain();
            }
        });
    }

    private void setupUi() {
        findViews();
        setupRecyclerViews();
    }

    public void loadData() {
        showState(State.Loading);
        if(isOnline()){
            loadFromNetwork();
            return;
        }
            loadFromDb();

    }

    public void loadFromDb(){
        Disposable disposable = photoRepository.loadNotDeletedPhotos()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Photo>>() {
                    @Override
                    public void accept(List<Photo> photos) throws Exception {
                        checkListAndShowState(photos);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

        compositeDisposable.add(disposable);
    }

    public void loadFromNetwork(){
        cancelCurrentRequestIfNeeded();

        loadPhotosRequest = RestApi.getInstance()
                .loadLatestPhotos();

        loadPhotosRequest.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        handleError(e);
                    }
                });
            }

            @Override
            public void onResponse(@NonNull Call call,
                                   @NonNull final Response response) {

                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showState(State.ServerError);
                        }
                    });
                    return;
                }

                Gson gson = new Gson();
                List<Photo> latestPhotosList=null;
                LatestPhotos lp;

                try {
                    String responseData = response.body().string();
                    lp = gson.fromJson(responseData, LatestPhotos.class);
                    latestPhotosList=lp.getLatestPhotos();
                } catch (IOException | IllegalStateException | JsonSyntaxException |NullPointerException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showState(State.ServerError);
                        }
                    });
                }

                Collections.sort(latestPhotosList, new Comparator<Photo>() {
                    public int compare(Photo o1, Photo o2) {
                        return o1.getPhotoId()-o2.getPhotoId();
                    }
                });
                
                if(latestPhotosList.size() > 20){
                    latestPhotosList = latestPhotosList.subList(Math.max(latestPhotosList.size() - 20, 0), latestPhotosList.size());
                }

                PhotosDatabase db = PhotosDatabase.getAppDatabase(MainActivity.this);
                List<Photo> deletedPhotos = db.photoDao().loadDeletedPhotos();
                latestPhotosList.removeAll(deletedPhotos);

                final List<Photo> finalLatestPhotosList = latestPhotosList;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        checkListAndShowState(finalLatestPhotosList);
                        saveToDb(finalLatestPhotosList);
                    }
                });
            }
        });
    }

    public void saveToDb(List<Photo> list){
        if (list == null) {
            return;
        }

        if (list.isEmpty()) {
            return;
        }

        Disposable disposable = photoRepository.saveData(list)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {

                    @Override
                    public void run() throws Exception { }
                }, new Consumer<Throwable>(

                ) {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
        compositeDisposable.add(disposable);

    }

    private void onClickTryAgain() {
        loadData();
    }

    private void cancelCurrentRequestIfNeeded() {
        if (loadPhotosRequest == null) {
            return;
        }

        if (loadPhotosRequest.isCanceled()) {
            loadPhotosRequest = null;
            return;
        }

        if (loadPhotosRequest.isExecuted()) {
            loadPhotosRequest.cancel();
            loadPhotosRequest = null;
        }
    }

    private void handleError(Throwable throwable) {
        if (throwable instanceof IOException) {
            showState(State.NetworkError);
            return;
        }
        showState(State.ServerError);
    }

    private void checkListAndShowState(final List<Photo> latestPhotosList) {

         if (latestPhotosList == null) {
            showState(State.HasNoData);
            return;
         }

        if (latestPhotosList.isEmpty()) {
            showState(State.HasNoData);
            return;
        }

        for(Photo photo:latestPhotosList){
            Glide.with(this)
                    .load(photo.getImgSrc())
                    .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.DATA))
                    .preload();
        }

        photosAdapter.replaceItems(latestPhotosList);
        showState(State.HasData);

    }

    public void showState(@NonNull State state) {

        switch (state) {
            case HasData:
                viewError.setVisibility(View.GONE);
                viewLoading.setVisibility(View.GONE);
                viewNoData.setVisibility(View.GONE);

                rvPhotos.setVisibility(View.VISIBLE);
                break;

            case HasNoData:
                rvPhotos.setVisibility(View.GONE);
                viewLoading.setVisibility(View.GONE);

                viewError.setVisibility(View.VISIBLE);
                viewNoData.setVisibility(View.VISIBLE);
                break;
            case NetworkError:
                rvPhotos.setVisibility(View.GONE);
                viewLoading.setVisibility(View.GONE);
                viewNoData.setVisibility(View.GONE);

                tvError.setText(getText(R.string.error_network));
                viewError.setVisibility(View.VISIBLE);
                break;

            case ServerError:
                rvPhotos.setVisibility(View.GONE);
                viewLoading.setVisibility(View.GONE);
                viewNoData.setVisibility(View.GONE);

                tvError.setText(getText(R.string.error_server));
                viewError.setVisibility(View.VISIBLE);
                break;
            case Loading:
                viewError.setVisibility(View.GONE);
                rvPhotos.setVisibility(View.GONE);
                viewNoData.setVisibility(View.GONE);

                viewLoading.setVisibility(View.VISIBLE);
                break;


            default:
                throw new IllegalArgumentException("Unknown state: " + state);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    private void setupRecyclerViews() {
        photosAdapter = new PhotosAdapter(Glide.with(this),this);
        rvPhotos.setLayoutManager(new LinearLayoutManager(this));
        rvPhotos.setAdapter(photosAdapter);
    }

    private void findViews() {
        rvPhotos = findViewById(R.id.rv_photos);
        btnTryAgain = findViewById(R.id.btn_try_again);
        viewError = findViewById(R.id.lt_error);
        viewLoading = findViewById(R.id.lt_loading);
        viewNoData = findViewById(R.id.lt_no_data);
        tvError = findViewById(R.id.tv_error);
    }
}
