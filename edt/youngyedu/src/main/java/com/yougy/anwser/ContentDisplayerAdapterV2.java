package com.yougy.anwser;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by FH on 2018/7/3.
 */

public abstract class ContentDisplayerAdapterV2 {
    //双向绑定的ContentDisplayer
    private ContentDisplayerV2 mContentDisplayer;

    //本地数据存储
    private HashMap<String, List<Content_new>> dataMap = new HashMap<String, List<Content_new>>();

    /**
     * 设置绑定的ContentDisplayer,双向绑定用
     */
    protected void setContentDisplayer(ContentDisplayerV2 mContentDisplayer) {
        this.mContentDisplayer = mContentDisplayer;
    }

    /**
     * 更新本地数据存储中的数据,会导致afterPageCountChanged被触发
     * @param typeKey 要更新的数据需要存放的typeKey
     * @param dataList 数据
     */
    public void updateDataList(String typeKey, List<Content_new> dataList) {
        dataMap.put(typeKey, dataList);
        ContentDisplayerV2.runOnUiThread(new Runnable() {
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
        ContentDisplayerV2.runOnUiThread(new Runnable() {
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
    public List<Content_new> getDataList(String typeKey) {
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
        List<Content_new> dataList = dataMap.get(typeKey);
        if (dataList != null) {
            for (Content_new content : dataList) {
                String contentValue = content.getValue();
                //当前content内部包含的页数会以**页数##的形式拼在content的value后面.
                //对于有内部页数的content,算它的内部页数
                if (contentValue.endsWith("##")) {
                    String contentPageStr = contentValue.substring(contentValue.lastIndexOf("**") + 2, contentValue.lastIndexOf("##"));
                    try {
                        pageCount = pageCount + Integer.parseInt(contentPageStr);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        pageCount = pageCount + 1;
                    }
                } else {
                    //对于没有内部页数的content,算1页
                    pageCount = pageCount + 1;
                }
            }
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
    protected PageInfoQueryResult queryPageInfo(String typeKey , int needPageInfoIndex){
        //如果type和needPageIndex不合法,直接返回找不到
        if (TextUtils.isEmpty(typeKey) || needPageInfoIndex < 0){
            return new PageInfoQueryResult(false , null , -1);
        }
        //给定的typeKey下无数据,直接返回找不到
        List<Content_new> contentList = dataMap.get(typeKey);
        if (contentList == null || contentList.size() == 0){
            return new PageInfoQueryResult(false ,null , -1);
        }
        //从数据集里第0个开始数,一直数到请求的needPageInfoIndex,看看有没有适合的content.
        for (int i = 0 , tempIndex = 0 ; i < contentList.size(); i++) {
            Content_new content = contentList.get(i);
            //如果刚好数到needPageInfoIndex,直接返回找到了.
            if (tempIndex == needPageInfoIndex){
                return new PageInfoQueryResult(false , content , 0);
            }
            //如果还没数到needPageInfoIndex,且数到一个未展开的pdf,返回需要展开后重新查找
            if (content.getType() == Content_new.Type.PDF){
                if (!content.getValue().endsWith("##")){
                    return new PageInfoQueryResult(true , content , 0);
                }
                else {
                    String contentValue = content.getValue();
                    int contentIncludePageCount = Integer.parseInt(contentValue.substring(contentValue.lastIndexOf("**") + 2, contentValue.lastIndexOf("##")));
                    //如果还没数到needPageInfoIndex,且数到一个展开的pdf,则尝试本pdf的内部页数是否能满足查询条件.
                    if (tempIndex + contentIncludePageCount > needPageInfoIndex){
                        //如果能满足,则返回本pdf和响应内部页数
                        return new PageInfoQueryResult(false , content , needPageInfoIndex - tempIndex);
                    }
                    else {
                        //如果不能满足,则把本pdf的页数全部加上,继续下一个pdf
                        tempIndex = tempIndex + contentIncludePageCount;
                    }
                }
            }
            else {
                //数的不是pdf,则都算一页,直接数下一个content
                tempIndex++;
            }
        }
        //如果数到最后,所有的content都数完了还不能满足needPageInfoIndex,返回找不到.
        return new PageInfoQueryResult(false , null , -1);
    }

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
    protected class PageInfoQueryResult{
        public boolean needQueryAgain;//是否需要再次查询
        public Content_new content;//本次需要加载的content
        public int subPageIndex;//加载本次的content的内部pageIndex

        public PageInfoQueryResult(boolean needQueryAgain, Content_new content , int subPageIndex) {
            this.needQueryAgain = needQueryAgain;
            this.content = content;
            this.subPageIndex = subPageIndex;
        }
    }






}
