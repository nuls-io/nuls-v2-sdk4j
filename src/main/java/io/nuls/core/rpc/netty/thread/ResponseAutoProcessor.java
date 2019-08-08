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
package io.nuls.core.rpc.netty.thread;

import io.nuls.core.log.Log;
import io.nuls.core.rpc.invoke.BaseInvoke;
import io.nuls.core.rpc.model.message.Response;
import io.nuls.core.rpc.netty.channel.ConnectData;
import io.nuls.core.rpc.netty.channel.manager.ConnectManager;

/**
 * 消费从服务端获取的消息
 * Consume the messages from servers
 *
 * @author tag
 * @date 2019/2/25
 */
public class ResponseAutoProcessor implements Runnable {
    private ConnectData connectData;

    public ResponseAutoProcessor(ConnectData connectData) {
        this.connectData = connectData;
    }

    /**
     * 消费从服务端获取的消息
     * Consume the messages from servers
     */
    @SuppressWarnings("InfiniteLoopStatement")
    @Override
    public void run() {
        while (connectData.isConnected()) {
            try {
                /*
                获取队列中的第一个对象
                Get the first item of the queue
                 */
                Response response = connectData.getResponseAutoQueue().take();
                if (response.getRequestID() == null) {
                    continue;
                }
                /*
                获取Response对象，这里得到的对象一定是需要自动调用本地方法
                Get Response object, The object you get here must automatically call the local method
                 */
                String messageId = response.getRequestID();

                /*
                自动调用本地方法
                Invoke local method automatically
                 */
                BaseInvoke baseInvoke = ConnectManager.INVOKE_MAP.get(messageId);
                baseInvoke.callBack(response);
            } catch (Exception e) {
                Log.error(e);
            }
        }
    }
}
