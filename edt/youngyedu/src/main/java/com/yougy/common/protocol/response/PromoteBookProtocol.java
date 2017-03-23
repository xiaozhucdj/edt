package com.yougy.common.protocol.response;

/**
 * Created by Administrator on 2017/2/13.
 * 书城推荐返回 协议
 */
public class PromoteBookProtocol extends BookBaseResponse {
    /**返回结果
    {
        "ret": 0,
            "msg": "success",
            "count": 1,
            "data": [
        {
            "count": xxx,
                "bookList": []
        }
        ]
    }

    参数	类型	描述
    ret	int	返回结果编码1
    msg	string	返回结果描述
    count	int	data大小
    data	array	推荐数据
    count	int	bookList大小
    bookList	array	推荐图书数组
     */


}

