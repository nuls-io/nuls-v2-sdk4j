/*
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */
package io.nuls.core.rpc.model.message;

import io.nuls.core.constant.ErrorCode;
import io.nuls.core.model.DateUtils;
import io.nuls.core.rpc.info.Constants;
import io.nuls.core.rpc.netty.channel.manager.ConnectManager;
import io.nuls.core.rpc.util.NulsDateUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 消息工具类，用于构造常用的基本消息体
 * Message Tool Class for Constructing Commonly Used Basic Message Body
 *
 * @author tangyi
 * @date 2018/12/4
 */
public class MessageUtil {

    /**
     * 默认Message对象
     * Default Message object
     *
     * @param messageType Message type
     * @return Message
     */
    public static Message basicMessage(MessageType messageType) {
        Message message = new Message();
        message.setMessageID(Constants.nextSequence());
        message.setMessageType(messageType.name());
        message.setTimestamp(String.valueOf( NulsDateUtils.getCurrentTimeMillis()));
        message.setTimeZone(DateUtils.TIME_ZONE_STRING);
        return message;
    }


    /**
     * 默认握手对象
     * Default NegotiateConnection object
     *
     * @return NegotiateConnection
     */
    public static NegotiateConnection defaultNegotiateConnection() {
        NegotiateConnection negotiateConnection = new NegotiateConnection();
        negotiateConnection.setAbbreviation(ConnectManager.LOCAL.getAbbreviation());
        negotiateConnection.setProtocolVersion("0.1");
        negotiateConnection.setCompressionAlgorithm("zlib");
        negotiateConnection.setCompressionRate("0");
        return negotiateConnection;
    }

    /**
     * 构造默认Request对象
     * Constructing a default Request object
     *
     * @return Request
     */
    public static Request defaultRequest() {
        Request request = new Request();
        request.setRequestAck("0");
        request.setSubscriptionEventCounter("0");
        request.setSubscriptionPeriod("0");
        request.setSubscriptionRange("0");
        request.setResponseMaxSize("0");
        request.setRequestMethods(new HashMap<>(1));
        return request;
    }


    /**
     * 根据参数构造Request对象，然后发送Request
     * Construct the Request object according to the parameters, and then send the Request
     *
     * @param cmd                      Cmd of remote method
     * @param params                   Parameters of remote method
     * @param ack                      Need an Ack?
     * @param subscriptionPeriod       远程方法调用频率（秒），Frequency of remote method (Second)
     * @param subscriptionEventCounter 远程方法调用频率（改变次数），Frequency of remote method (Change count)
     * @return Request
     */
    public static Request newRequest(String cmd, Map params, String ack, String subscriptionPeriod, String subscriptionEventCounter) {
        Request request = defaultRequest();
        request.setRequestAck(ack);
        request.setSubscriptionPeriod(subscriptionPeriod);
        request.setSubscriptionEventCounter(subscriptionEventCounter);
        request.getRequestMethods().put(cmd, params);
        return request;
    }


    /**
     * 构造一个Response对象
     * Constructing a new Response object
     *
     * @param requestId Message ID of request
     * @param status    1 = success, 0 = failed
     * @param comment   User defined string
     * @return Response
     */
    public static Response newResponse(String requestId, int status, String comment) {
        Response response = new Response();
        response.setRequestID(requestId);
        response.setResponseStatus(status);
        response.setResponseComment(comment);
        response.setResponseMaxSize(Constants.ZERO);
        return response;
    }

    /**
     * 构造一个执行成功的Response对象
     * Constructing a new Response object
     *
     * @param requestId Message ID of request
     * @return Response
     */
    public static Response newSuccessResponse(String requestId) {
        return newResponse(requestId, Response.SUCCESS, Response.SUCCESS_MSG);
    }

    /**
     * 构造一个执行成功的Response对象
     * Constructing a new Response object
     *
     * @param requestId Message ID of request
     * @return Response
     */
    public static Response newSuccessResponse(String requestId, String msg) {
        return newResponse(requestId, Response.SUCCESS, msg);
    }

    /**
     * 构造一个执行失败的Response对象
     * Constructing a new Response object
     *
     * @param requestId Message ID of request
     * @return Response
     */
    public static Response newFailResponse(String requestId, String msg) {
        return newResponse(requestId, Response.FAIL, msg);
    }

    /**
     * 构造一个执行失败的Response对象
     * Constructing a new Response object
     *
     * @param requestId Message ID of request
     * @param errorCode error object
     * @return Response
     */
    public static Response newFailResponse(String requestId, ErrorCode errorCode){
        Objects.requireNonNull(errorCode,"errorCode can't be null");
        Response response = newFailResponse(requestId,errorCode.getMsg());
        response.setResponseErrorCode(errorCode.getCode());
        return response;
    }



}
