/*
 * Copyright (C)  Tony Green, LitePal Framework Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.litepal;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.onyx.android.sdk.data.DataManager;
import com.onyx.android.sdk.reader.ReaderBaseApp;
import com.raizlabs.android.dbflow.config.DatabaseHolder;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.litepal.exceptions.GlobalException;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class of LitePal to make things easier when developers need to use
 * context. When you need context, just use
 * <b>LitePalApplication.getContext()</b>. To make this function work, you need
 * to configure your AndroidManifest.xml. Specifying
 * <b>"org.litepal.LitePalApplication"</b> as the application name in your
 * &lt;application&gt; tag to enable LitePal get the context. Of course if you
 * need to write your own Application class, LitePal can still live with that.
 * But just remember make your own Application class inherited from
 * LitePalApplication instead of inheriting from Application directly. This can
 * make all things work without side effects. <br>
 * Besides if you don't want use the above way, you can also call the LitePal.initialize(Context)
 * method to do the same job. Just remember call this method as early as possible, in Application's onCreate()
 * method will be fine.
 *
 * @author Tony Green
 * @since 1.0
 */
public class LitePalApplication extends ReaderBaseApp {

    /**
     * Global application context.
     */
    static Context sContext;

    /**
     * Construct of LitePalApplication. Initialize application context.
     */
    public LitePalApplication() {
        sContext = this;
    }

    /**
     * Deprecated. Use {@link litepal.LitePal#initialize(Context)} instead.
     *
     * @param context Application context.
     */
    @Deprecated
    public static void initialize(Context context) {
        sContext = context;
    }

    /**
     * Get the global application context.
     *
     * @return Application context.
     * @throws litepal.exceptions.GlobalException
     */
    public static Context getContext() {
        if (sContext == null) {
            throw new GlobalException(GlobalException.APPLICATION_CONTEXT_IS_NULL);
        }
        return sContext;
    }

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex.install(this);
//    }

    @Override
    public void onCreate() {
        super.onCreate();

        DataManager.init(this, databaseHolderList());
        initContentProvider(this);
    }

    private List<Class<? extends DatabaseHolder>> databaseHolderList() {
        List<Class<? extends DatabaseHolder>> list = new ArrayList<>();
        // 此处填入其他需要初始化的 database
        return list;
    }

    static public void initContentProvider(final Context context) {
        try {
            FlowConfig.Builder builder = new FlowConfig.Builder(context);
            FlowManager.init(builder.build());
        } catch (Exception e) {
        }
    }
}