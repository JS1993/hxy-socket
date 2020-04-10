package hxy.server.socket.engine;

import hxy.server.socket.util.SpringApplicationContextHolder;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@ChannelHandler.Sharable
public class MsgOutboundHandle extends ChannelOutboundHandlerAdapter {

    private static MsgOutboundHandle msgOutboundHandle = new MsgOutboundHandle();

    public static MsgOutboundHandle getInstance() {
        return msgOutboundHandle;
    }

    private static final String SOCKET_HANDLER_BUILDER_NAME = "socketHandlerBuilder";

    public final boolean isTcp =  SpringApplicationContextHolder.getBean(SOCKET_HANDLER_BUILDER_NAME) instanceof TcpsocketHandlerBuilder;


    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (isTcp) {
            tcpWrite(ctx, msg, promise);
        } else {
            wsWrite(ctx, msg, promise);
        }
    }

    private void tcpWrite(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        byte[] contentBytes;
        if(msg instanceof String){
            msg = ((String) msg).getBytes();
        }
        if (msg instanceof byte[]) {
            contentBytes = (byte[]) msg;
            ctx.write(Unpooled.wrappedBuffer(contentBytes), promise);
        }
        throw new UnsupportedOperationException("不支持的传输类型,请转换成String和Byte类型");
    }

    private void wsWrite(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        if(msg instanceof String){
            ctx.write(new TextWebSocketFrame((String) msg), promise);
        }
        if (msg instanceof byte[]) {
            ctx.write(new BinaryWebSocketFrame(Unpooled.wrappedBuffer((byte[]) msg)), promise);
        }
        throw new UnsupportedOperationException("不支持的传输类型,请转换成String和Byte类型");
    }
}
