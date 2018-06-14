package com.yougy.common.protocol.callback;

import android.content.ContentValues;
import android.content.Context;

import com.yougy.common.utils.AliyunUtil;
import com.yougy.common.utils.LogUtils;
import com.yougy.common.utils.StringUtils;
import com.yougy.home.bean.CacheJsonInfo;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.List;

/**
 * Created by jiangliang on 2017/1/9.
 */

public abstract class CacheInfoBack<T> extends BaseCallBack<T> {

    String mJson;

    public CacheInfoBack(Context context) {
        super(context);
    }

    void operateCacheInfo(int id) {
        if (!StringUtils.isEmpty(mJson)) {
            List<CacheJsonInfo> infos = DataSupport.where("cacheID = ? ", id+"").find(CacheJsonInfo.class);
//            YoungyApplicationManager.closeDb();
            File dbFile = mWeakReference.get().getDatabasePath(AliyunUtil.DATABASE_NAME);
            if (infos != null && infos.size() > 0 && dbFile.exists()) {
                LogUtils.e("当前 有缓存的JSON ，需要更新");
                LogUtils.e("aa+" + infos.get(0).getCacheJSON());
                ContentValues values = new ContentValues();
                values.put("cacheJSON", mJson);
                DataSupport.updateAll(CacheJsonInfo.class, values, "cacheID = ? ", infos.get(0).getCacheID());
            } else {
                LogUtils.e("当前 没有缓存的JSON，需要存储");
                CacheJsonInfo info = new CacheJsonInfo();
                info.setCacheID(id+"");
                info.setCacheJSON(mJson);
                info.save();
            }
        }
    }

}
