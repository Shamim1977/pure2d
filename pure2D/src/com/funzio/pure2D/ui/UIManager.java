/**
 * 
 */
package com.funzio.pure2D.ui;

import android.content.res.AssetManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.ui.xml.UIConfig;
import com.funzio.pure2D.ui.xml.UILoader;

/**
 * @author long.ngo
 */
public class UIManager {
    protected static final String TAG = UIManager.class.getSimpleName();

    // singleton
    private static UIManager sInstance;

    private UITextureManager mTextureManager;
    private UIConfig mConfig;
    private UILoader mLoader;

    private UIManager() {
        mConfig = new UIConfig();
        mLoader = new UILoader(this);
    }

    public static UIManager getInstance() {
        if (sInstance == null) {
            sInstance = new UIManager();
        }

        return sInstance;
    }

    public void reset(final AssetManager assets) {
        Log.w(TAG, "reset()");

        mConfig.reset(assets);
        mTextureManager = null;
    }

    public boolean loadConfig(final XmlPullParser parser) {
        Log.v(TAG, "loadConfig()");

        final boolean success = mConfig.load(parser);

        // apply the config
        if (mTextureManager != null) {
            mTextureManager.setUIConfig(mConfig);
        }

        return success;
    }

    public UILoader getLoader() {
        return mLoader;
    }

    public void setTextureManager(final UITextureManager textureManager) {
        Log.v(TAG, "setTextureManager(): " + textureManager);

        mTextureManager = textureManager;

        // apply the config
        if (mTextureManager != null) {
            mTextureManager.setUIConfig(mConfig);
        }
    }

    public UITextureManager getTextureManager() {
        return mTextureManager;
    }

}