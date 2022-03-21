package com.happyzombie.springinitializr.api;

import com.happyzombie.springinitializr.common.util.JsonUtil;
import lombok.Data;

@Data
public class NearBackendRequest {

    /**
     * 示例
     * [48,84,{},"com.nearprotocol.mainnet.explorer.transactions-list-by-account-id",["witt.near",10,{"endTimestamp":1646907653388,"transactionIndex":0}]]
     */
    // https://wamp-proto.org/_static/gen/wamp_latest.html
    // wamp中定义的 CALL = 48，callResult = 50
    // SUBSCRIBE = 32, SUBSCRIBED = 33 (订阅成功)
    private Integer messageType;
    // 服务端回传该id
    private Integer requestId;
    // 选项参数，暂时没遇到过需要传的场景
    private Object options;
    // uri
    private String apiName;
    private Object[] arguments;

    // 这里要把参数定义成一个类，否则太难看懂
    public void setArguments(Object... args) {
        this.arguments = args;
    }

    public String toRequestJson() {
        // 如果options是空，则传{}
        // 如果arguments是空，则不传
        return JsonUtil.arrayToString(messageType, requestId, options == null ? new Object() : JsonUtil.objectToString(options), apiName, (arguments != null && arguments.length != 0) ? arguments : null);
    }

}
