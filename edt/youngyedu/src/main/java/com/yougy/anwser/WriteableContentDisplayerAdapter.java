package com.yougy.anwser;


import com.yougy.common.utils.LogUtils;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/6/28.
 */

public abstract class WriteableContentDisplayerAdapter {
    //双向绑定的WCD
    private WriteableContentDisplayer mWriteableContentDisplayer;
    //数据源容器
    private HashMap<String, List<Content_new>[]> dataMap = new HashMap<String, List<Content_new>[]>();
    //layer0的adapter
    private ContentDisplayerAdapterV2 layer0Adapter = new ContentDisplayerAdapterV2() {
        @Override
        public void afterPageCountChanged(String typeKey) {
            //有可能是pdf展开导致的页码变化
            //如果基准层是layer0,则layer0上报的afterPageCountChanged在此处继续上报.
            //非基准层不管.
            if (pageCountBaseLayerIndex == 0) {
                WriteableContentDisplayerAdapter.this.afterPageCountChanged(typeKey);
            }
        }
    };

    //默认页码总数基于的层index.意思是以那一层的content数量作业页数总数的基准.
    //本方法会影响到页数toPage和getPageCount和onPageInfoChanged等关于页码的方法.
    //默认是1,即pdf显示层上面覆盖的那一层手写层.
    private int pageCountBaseLayerIndex = 1;

    /**
     * 获取layer0的adapter
     * @return
     */
    protected ContentDisplayerAdapterV2 getLayer0Adapter() {
        return layer0Adapter;
    }

    /**
     * 必要方法!设置WCD的页码数量所依赖的基准层的index,例如设0,则第0层的页码数就会被作为基准
     * 基准层会影响到所有跟pageIndex有关的逻辑.
     * @param pageCountBaseLayerIndex 基准层index,可以为0,1,2,不能为其他值,不设默认是1
     * @throws UnSupportedDataException 不支持的基准层index的值,就会抛出异常
     */
    public void setPageCountBaseLayerIndex(int pageCountBaseLayerIndex) throws UnSupportedDataException {
        if (pageCountBaseLayerIndex >= 3) {
            throw new UnSupportedDataException("不能把页码基准层设为第4层或更高的层,因为没有那么多层!");
        }
        this.pageCountBaseLayerIndex = pageCountBaseLayerIndex;
    }

    /**
     * 获取基准层index
     * @return
     */
    public int getPageCountBaseLayerIndex() {
        return pageCountBaseLayerIndex;
    }

    /**
     * 设置本adapter绑定的WCD
     * @param writeableContentDisplayer
     */
    protected void setWriteableContentDisplayer(WriteableContentDisplayer writeableContentDisplayer) {
        this.mWriteableContentDisplayer = writeableContentDisplayer;
    }

    /**
     * 必要方法!更新adapter中的数据.
     * 在使用WCD toPage之前,一定要先调用这个方法把数据全部填充到adapter中.
     * WCD的toPage方法会向adapter查询应该显示什么数据,如果没有事先把数据填充到adapter中会导致很多未知问题.
     *
     * 【注意！！】在WCD显示内容时调用此方法更新adapter中的数据时,如果更新的是基准层数据,则会导致afterPageCountChanged回调被触发,并且清空之前的toPage参数.
     *
     * @param typeKey 要更新的数据存放的typeKey
     * @param layerIndex 要更新的数据应该显示的层index
     * @param contentList 数据
     * @throws UnSupportedDataException
     */
    public void updateDataList(String typeKey, int layerIndex, List<Content_new> contentList) throws UnSupportedDataException {
        //typeKey非空检查
        if (typeKey == null) {
            LogUtils.e("FH-----updateData错误!typeKey不能为空");
            return;
        }
        //设置数据层数检查
        if (layerIndex >= 3) {
            LogUtils.e("FH-----updateData错误!layerIndex >= 3");
            return;
        }
        //设置数据非空检查
        if (contentList == null || contentList.size() == 0) {
            LogUtils.e("FH-----updateData错误!update的数据list为null");
            return;
        }
        if (layerIndex == 0) {
            //设置第0层数据时,同步更新到layer0Adaper上.
            if (layer0Adapter != null) {
                layer0Adapter.updateDataList(typeKey, contentList);
            }
        }
        //存放数据至对应的位置
        List<Content_new>[] contentLists = dataMap.get(typeKey);
        if (contentLists == null) {
            contentLists = new List[3];
            dataMap.put(typeKey, contentLists);
        }
        contentLists[layerIndex] = contentList;
        //如果更改的是基准层的数据,则触发afterPageCountChanged回调,并且清空之前的toPage参数
        if (layerIndex == pageCountBaseLayerIndex) {
            if (mWriteableContentDisplayer != null){
                mWriteableContentDisplayer.currentTypeKey = null;
                mWriteableContentDisplayer.currentPageIndex = -1;
                mWriteableContentDisplayer.currentIfUseCache = false;
            }
            WriteableContentDisplayer.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    afterPageCountChanged(typeKey);
                }
            });
        }
    }

    /**
     * 删除adapter中的数据
     * @param typeKey
     * @param layerIndex
     */
    public void deleteDataList(String typeKey, int layerIndex) {
        //typeKey非空检查
        if (typeKey == null) {
            LogUtils.e("FH-----deleteDataList!typeKey不能为空");
            return;
        }
        //设置数据层数检查
        if (layerIndex >= 3) {
            LogUtils.e("FH-----deleteDataList!layerIndex >= 3");
            return;
        }
        if (layerIndex == 0) {
            //设置第0层数据时,在layer0Adaper上同步删除.
            if (layer0Adapter != null) {
                layer0Adapter.deleteDataList(typeKey);
            }
        }
        //删除对应数据
        List<Content_new>[] conteLists = dataMap.get(typeKey);
        if (conteLists != null) {
            conteLists[layerIndex] = null;
        }
        //如果更改的是基准层的数据,则触发afterPageCountChanged回调,并且清空之前的toPage参数
        if (layerIndex == pageCountBaseLayerIndex) {
            if (mWriteableContentDisplayer != null){
                mWriteableContentDisplayer.currentTypeKey = null;
                mWriteableContentDisplayer.currentPageIndex = -1;
                mWriteableContentDisplayer.currentIfUseCache = false;
            }
            WriteableContentDisplayer.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    afterPageCountChanged(typeKey);
                }
            });
        }
    }

    /**
     * 获取页数(基于基准层获取),忽略其他层的页数
     * @param typeKey 要获取的数据的typeKey
     * @return 基于基准层的typeKey下的页数.
     * 例如基准层3页,其他层1页和2两页,则返回3.
     * 例如基准层1页,其它层3页和4页,则返回1
     * 找不到typeKey的数据则返回0,
     */
    public int getPageCountBaseOnBaseLayer(String typeKey) {
        //typeKey非空检查
        if (typeKey == null) {
            LogUtils.e("FH-----getPageCountBaseOnBaseLayer失败!typeKey不能为空");
            return 0;
        }
        List<Content_new>[] conteLists = dataMap.get(typeKey);
        if (conteLists == null) {
            return 0;
        }
        if (pageCountBaseLayerIndex == 0 && layer0Adapter != null) {
            return layer0Adapter.getPageCount(typeKey);
        } else {
            if (conteLists[pageCountBaseLayerIndex] == null) {
                return 0;
            } else {
                return conteLists[pageCountBaseLayerIndex].size();
            }
        }
    }

    /**
     * 返回某typeKey下指定层的页总数
     * @param typeKey
     * @param layerIndex
     * @return 总页数.
     * 找不到typeKey的数据则返回0
     */
    public int getLayerPageCount(String typeKey , int layerIndex) {
        //typeKey非空检查
        if (typeKey == null) {
            LogUtils.e("FH-----getPageCountBaseOnBaseLayer失败!typeKey不能为空");
            return 0;
        }
        List<Content_new>[] conteLists = dataMap.get(typeKey);
        if (conteLists == null) {
            return 0;
        }

        if (layerIndex == 0 && layer0Adapter != null) {
            return layer0Adapter.getPageCount(typeKey);
        } else {
            if (conteLists[layerIndex] == null) {
                return 0;
            } else {
                return conteLists[layerIndex].size();
            }
        }
    }

    /**
     * 获取指定typeKey指定pageIndex指定层数的Content
     * @param typeKey
     * @param pageIndex
     * @param layerIndex
     * @return 如果找不到则返回null
     */
    public Content_new getContent(String typeKey , int pageIndex , int layerIndex){
        if (pageIndex < 0 || pageIndex >= getLayerPageCount(typeKey , layerIndex)){
            return null;
        }
        return dataMap.get(typeKey)[layerIndex].get(pageIndex);
    }

    /**
     * 当总页数发生变化时的回调
     * 本回调只会在基准层的总页数发生变化的时候被触发!!
     * 本回调只会在Main线程中被触发!!!
     * @param typeKey 发生变化的数据的typeKey
     */
    public abstract void afterPageCountChanged(String typeKey);

    /**
     * 当执行一个toPage请求之前,会首先执行的回调.
     *
     * 本回调只会在Main线程中被触发!!!
     * 本回调执行完之后才会执行真正的toPage逻辑!
     *
     * 由于toPage请求中的检查点的存在,有可能执行了beforeToPage不一定会执行toPage和afterToPage(因为有可能当前的toPage请求已经被中断了)
     *
     * 我只能够保证的是beforeToPage,toPage主逻辑,afterToPage这3个方法,如果执行了后一个方法,前面的方法一定已经被执行完毕了
     * 但不能保证3个方法都被执行.
     *
     * @param fromTypeKey 当前执行的toPage请求是从哪个typeKey toPage过来的
     * @param fromPageIndex 当前执行的toPage请求是从哪个pageIndex toPage过来的
     * @param toTypeKey 当前执行的toPage请求,是toPage哪个typeKey
     * @param toPageIndex 当前执行的toPage请求,是toPage哪个pageIndex
     */
    public abstract void beforeToPage(String fromTypeKey , int fromPageIndex , String toTypeKey , int toPageIndex);

    /**
     * 当执行完一个toPage请求的主逻辑之后,会执行的回调.
     *
     * 本回调只会在Main线程中被触发!!!
     *
     * 由于toPage请求中的检查点的存在,有可能执行了beforeToPage不一定会执行toPage和afterToPage(因为有可能当前的toPage请求已经被中断了)
     *
     * 我只能够保证的是beforeToPage,toPage主逻辑,afterToPage这3个方法,如果执行了后一个方法,前面的方法一定已经被执行完毕了
     * 但不能保证3个方法都被执行.
     *
     * @param fromTypeKey 当前执行的toPage请求是从哪个typeKey toPage过来的
     * @param fromPageIndex 当前执行的toPage请求是从哪个pageIndex toPage过来的
     * @param toTypeKey 当前执行的toPage请求,是toPage哪个typeKey
     * @param toPageIndex 当前执行的toPage请求,是toPage哪个pageIndex
     */
    public abstract void afterToPage(String fromTypeKey , int fromPageIndex , String toTypeKey , int toPageIndex);


    public class UnSupportedDataException extends RuntimeException{
        public UnSupportedDataException(String message) {
            super(message);
        }
    }

}
