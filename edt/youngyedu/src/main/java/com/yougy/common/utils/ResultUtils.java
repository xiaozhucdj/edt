package com.yougy.common.utils;

import com.google.gson.Gson;
import com.yougy.common.bean.Result;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by jiangliang on 2017/2/20.
 */

public class ResultUtils {

    public static <T> Result<T> fromJsonObject(String json, Class<T> clazz) {
        Type type = new ParameterizedTypeImpl(Result.class, new Class[]{clazz});
        return new Gson().fromJson(json, type);
    }

    public static <T> Result<List<T>> fromJsonArray(String json, Class<T> clazz) {
        Type listType = new ParameterizedTypeImpl(List.class, new Class[]{clazz});
        Type resultType = new ParameterizedTypeImpl(Result.class, new Type[]{listType});
        return new Gson().fromJson(json, resultType);
    }


    private static class ParameterizedTypeImpl implements ParameterizedType {

        private final Class raw;

        private final Type[] args;

        public ParameterizedTypeImpl(Class raw, Type[] args) {
            this.raw = raw;
            this.args = args != null ? args : new Type[0];
        }

        @Override
        public Type[] getActualTypeArguments() {
            return args;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }

        @Override
        public Type getRawType() {
            return raw;
        }
    }
}
