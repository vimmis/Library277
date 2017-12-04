package edu.sjsu.vimmi_swami.library277;

/**
 * Created by VimmiRao on 12/1/2017.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by androidtutorialpoint on 5/11/16.
 */
public class AppSingleton {
    private static AppSingleton mNetworkSingleton;
    private static RequestQueue mRequestQueue;
    private Context mContext;

    private AppSingleton(Context context){
        this.mContext = context;

    }

    public static AppSingleton get(Context c){
        if(mNetworkSingleton == null  ){
            mNetworkSingleton = new AppSingleton(c.getApplicationContext());
        }
        return  mNetworkSingleton;
    }
    public  RequestQueue getVolleyRequestQueue()
    {
        if (mRequestQueue == null)
        {
            mRequestQueue = Volley.newRequestQueue
                    (mContext, new OkHttpStack(new OkHttpClient()));
        }

        return mRequestQueue;
    }

    public static void addRequest
            (@NonNull final Request<?> request, @NonNull final String tag)
    {
        request.setTag(tag);
        addRequest(request);
    }

    private static void addRequest(@NonNull final Request<?> request)
    {
        mNetworkSingleton.getVolleyRequestQueue().add(request);
    }

    public static void cancelAllRequests(@NonNull final String tag)
    {
        mNetworkSingleton.getVolleyRequestQueue().cancelAll(tag);
    }


//    mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
//        private final LruCache<String, Bitmap>
//                cache = new LruCache<String, Bitmap>(20);
//
//        @Override
//        public Bitmap getBitmap(String url) {
//            return cache.get(url);
//        }
//
//        @Override
//        public void putBitmap(String url, Bitmap bitmap) {
//            cache.put(url, bitmap);
//        }
//    });
//    private ImageLoader mImageLoader;
//
//
//    public ImageLoader getImageLoader() {
//        return mImageLoader;
//    }

}

