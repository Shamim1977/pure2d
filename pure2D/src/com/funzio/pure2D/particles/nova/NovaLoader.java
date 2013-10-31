/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;

import com.funzio.pure2D.loaders.AsyncTaskExecuter;
import com.funzio.pure2D.loaders.tasks.ReadTextFileTask;
import com.funzio.pure2D.loaders.tasks.RunnableTask;
import com.funzio.pure2D.loaders.tasks.Task;
import com.funzio.pure2D.loaders.tasks.URLLoadJsonTask;
import com.funzio.pure2D.loaders.tasks.URLLoadTextTask;
import com.funzio.pure2D.loaders.tasks.WriteTextFileTask;
import com.funzio.pure2D.particles.nova.vo.NovaVO;

/**
 * @author long
 */
public class NovaLoader {
    protected final String TAG = NovaLoader.class.getSimpleName();

    protected Listener mListener;

    public NovaLoader() {
    }

    public NovaLoader(final Listener listener) {
        mListener = listener;
    }

    /**
     * Load a specific Nova file, asynchronously. Note: some old Android version required to call this on UI Thread first off.
     * 
     * @param assets
     * @param filePath
     */
    public void loadAsync(final AssetManager assets, final String filePath) {
        Log.v(TAG, "loadAsync(): " + filePath);

        final AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
        // start loading
        executer.executeOnPool(new RunnableTask(new Runnable() {

            @Override
            public void run() {
                load(assets, filePath);
            }
        }));
    }

    /**
     * Load a specific Nova file, synchronously
     * 
     * @param assets
     * @param filePath
     */
    public void load(final AssetManager assets, final String filePath) {
        Log.v(TAG, "load(): " + filePath);

        final ReadTextFileTask readTask = new ReadTextFileTask(assets, filePath);
        if (readTask.run()) {
            Log.v(TAG, "Load success: " + filePath);

            try {
                if (mListener != null) {
                    mListener.onLoad(NovaLoader.this, filePath, new NovaVO(readTask.getContent()));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Load JSON failed: " + filePath, e);

                if (mListener != null) {
                    mListener.onError(NovaLoader.this, filePath);
                }
            }

        } else {
            Log.e(TAG, "Load failed: " + filePath);

            if (mListener != null) {
                mListener.onError(NovaLoader.this, filePath);
            }
        }
    }

    public boolean loadURL(final String urlPath, final String cachePath) {
        Log.v(TAG, "loadURL(): " + urlPath + ", " + cachePath);

        // read cache first
        if (cachePath != null && cachePath.length() > 0) {
            final ReadTextFileTask readTask = new ReadTextFileTask(cachePath);
            if (readTask.run()) {
                try {
                    if (mListener != null) {
                        mListener.onLoad(NovaLoader.this, urlPath, new NovaVO(readTask.getContent()));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Load JSON failed: " + urlPath, e);

                    if (mListener != null) {
                        mListener.onError(NovaLoader.this, urlPath);
                    }
                }
                return true;
            }
        }

        // load from url
        final URLLoadTextTask urlTask = new URLLoadJsonTask(urlPath);
        if (urlTask.run()) {
            final String json = urlTask.getStringBuilder().toString();
            try {
                if (mListener != null) {
                    mListener.onLoad(NovaLoader.this, urlPath, new NovaVO(json));
                }
            } catch (JSONException e) {
                Log.e(TAG, "Load JSON failed: " + urlPath, e);

                if (mListener != null) {
                    mListener.onError(NovaLoader.this, urlPath);
                }
            }
            // cache it
            if (cachePath != null && cachePath.length() > 0) {
                final WriteTextFileTask fileTask = new WriteTextFileTask(json, cachePath, false);
                fileTask.run();
            }

            return true;
        }

        return false;
    }

    public void loadURLAsync(final String urlPath, final String cachePath) {
        Log.v(TAG, "loadURLAsync(): " + urlPath + ", " + cachePath);

        final AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
        // start loading
        executer.executeOnPool(new RunnableTask(new Runnable() {

            @Override
            public void run() {
                loadURL(urlPath, cachePath);
            }
        }));
    }

    public Listener getListener() {
        return mListener;
    }

    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public static interface Listener {
        public void onLoad(NovaLoader loader, String filePath, NovaVO vo);

        public void onError(NovaLoader loader, String filePath);
    }

}
