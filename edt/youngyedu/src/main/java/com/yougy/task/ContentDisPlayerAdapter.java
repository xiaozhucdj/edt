package com.yougy.task;

import android.text.TextUtils;

import java.util.HashMap;

public abstract class ContentDisPlayerAdapter <T> {

    //双向绑定的ContentDisplayer
    private ContentDisPlayer mContentDisplayer;
    //本地数据存储
    private HashMap<String, String> dataMap = new HashMap<>();

    private String format = "TEXT";

    public String getFormat() {
        return format;
    }

    /**
     * 设置绑定的ContentDisplayer,双向绑定用
     */
    protected void setContentDisplayer(ContentDisPlayer mContentDisplayer) {
        this.mContentDisplayer = mContentDisplayer;
    }

    /**
     * 更新本地数据存储中的数据,会导致afterPageCountChanged被触发
     * @param typeKey 要更新的数据需要存放的typeKey
     * @param content 数据
     */
    public void updateDataList(String typeKey, String content, String format) {
        this.format = format;
        dataMap.put(typeKey, content);
        ContentDisPlayer.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                afterPageCountChanged(typeKey);
            }
        });
    }

    /**
     * 删除本地数据存储中的数据,会导致afterPageCountChanged被触发
     * @param typeKey 要删除的本地数据所在的typeKey
     */
    public void deleteDataList(String typeKey) {
        dataMap.remove(typeKey);
        ContentDisPlayer.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                afterPageCountChanged(typeKey);
            }
        });
    }

    /**
     * 获取本地数据存储中的数据
     * @param typeKey 要获取的数据存放的typeKey
     * @return 数据
     */
    public String getData (String typeKey) {
        return dataMap.get(typeKey);
    }

    /**
     * 获取本地数据中对应typeKey的数据总页数,对于已解析展开的pdf,会将其的实际页数计算到总页数中,对于未展开的pdf,算一页
     * @param typeKey 要获取的数据所对应的typeKey
     * @return 总页数
     */
    public int getPageCount(String typeKey) {
        int pageCount = 0;
        if (TextUtils.isEmpty(typeKey)){
            return pageCount;
        }
        String data = dataMap.get(typeKey);
        if (data == null)
            throw new IllegalArgumentException("type Key is Illegal argument Exception.");
        if (data.endsWith("##")) {
            String contentPageStr = data.substring(data.lastIndexOf("**") + 2, data.lastIndexOf("##"));
            try {
                pageCount = Integer.parseInt(contentPageStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                pageCount =  1;
            }
        } else {
            //对于没有内部页数的content,算1页
            pageCount =  1;
        }
        return pageCount;
    }

    /**
     * 工具方法,给定一个typeKey和一个想要切换到的pageIndex,在当前adapter的数据中查找是否和合适的已展开的content满足切换的条件.返回查找结果
     * @param typeKey 要切换到的typeKey
     * @param needPageInfoIndex 要切换到的pageIndex
     * @return 本方法会返回几种结果:
     * 1.确定按照给定的条件无法查找到适合的content.返回的pageInfoQueryResult是[不用再次查询,本次查询查找不到,-1]
     * 2.确定按照给定的条件可以查找到适合的content,返回的pageInfoQueryResult是[不用再次查询,适合的content,应该加载这个content的第x页]
     * 3.不确定是否能按照给定的条件查找到适合的content,因为有未展开的content,展开后可能能够满足条件.
     * 这种情况返回的pageInfoQueryResult是[需要再次查询,希望展开的content,0]
     */
    /*protected String queryPageInfo(String typeKey , int needPageInfoIndex){
        //如果type和needPageIndex不合法,直接返回找不到
        if (TextUtils.isEmpty(typeKey) || needPageInfoIndex < 0 ){
            return null;
        }
        //给定的typeKey下无数据,直接返回找不到
        String content = dataMap.get(typeKey);
        if (content == null|| needPageInfoIndex > getPageCount(typeKey) - 1){
            return null;
        }
        //如果还没数到needPageInfoIndex,且数到一个未展开的pdf,返回需要展开后重新查找
        if ("PDF".equals(format)){
            if (!content.endsWith("##")){
                return "again";
            }
            else {

            }
        }
        return content;
    }*/

    /**
     * 当总页数发生变化时的回调
     * 本回调只会在基准层的总页数发生变化的时候被触发!!
     * 本回调只会在Main线程中被触发!!!
     * @param typeKey 发生变化的数据的typeKey
     */
    public abstract void afterPageCountChanged(String typeKey);


    /**
     * queryPageInfo返回的结果体
     */
    /*protected class PageInfoQueryResult{
        public boolean needQueryAgain;//是否需要再次查询
        public T content;//本次需要加载的content
        public int subPageIndex;//加载本次的content的内部pageIndex

        public PageInfoQueryResult(boolean needQueryAgain, T content , int subPageIndex) {
            this.needQueryAgain = needQueryAgain;
            this.content = content;
            this.subPageIndex = subPageIndex;
        }
    }*/

}
