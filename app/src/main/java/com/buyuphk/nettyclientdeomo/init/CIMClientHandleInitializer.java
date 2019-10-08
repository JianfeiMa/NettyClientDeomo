package com.buyuphk.nettyclientdeomo.init;

import android.content.Context;

import com.buyuphk.nettyclientdeomo.handle.CIMClientHandle;
import com.crossoverjie.cim.common.protocol.CIMResponseProto;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 23/02/2018 22:47
 * @since JDK 1.8
 */
public class CIMClientHandleInitializer extends ChannelInitializer<Channel> {

    //private final CIMClientHandle cimClientHandle = new CIMClientHandle();
    private final CIMClientHandle cimClientHandle;

    public CIMClientHandleInitializer(Context context) {
        cimClientHandle = new CIMClientHandle(context);
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
                //10 秒没发送消息 将IdleStateHandler 添加到 ChannelPipeline 中
                .addLast(new IdleStateHandler(0, 10, 0))

                //心跳解码
                //.addLast(new HeartbeatEncode())

                // google Protobuf 编解码
                //拆包解码
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(CIMResponseProto.CIMResProtocol.getDefaultInstance()))
                //
                //拆包编码
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(cimClientHandle)
        ;
    }
}
