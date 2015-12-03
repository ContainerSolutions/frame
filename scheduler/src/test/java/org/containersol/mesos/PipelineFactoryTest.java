package org.containersol.mesos;

import com.googlecode.protobuf.pro.duplex.wire.DuplexProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class PipelineFactoryTest {


    private byte[] array;

    @Before
    public void setup() throws IOException {
        URL url = this.getClass().getResource("/out.txt");
        File output = new File(url.getFile());
        array = Files.readAllBytes(output.toPath());
    }

    @Test
    public void testReading() {
        EmbeddedChannel ch = new EmbeddedChannel(
//                new HttpResponseDecoder(),
                new HttpClientCodec(),
                new HttpObjectAggregator(81192),
                new ChunkedWriteHandler()
               // new LengthFieldBasedFrameDecoder(10000, 0, 4)
                new ChannelHandler() {

                    @Override
                    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                        int i = 1;
                    }

                    @Override
                    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                        int i = 1;

                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

                    }
                },
                new SimpleChannelInboundHandler<FullHttpRequest>() {
                    @Override
                    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
                        final SocketChannel sChannel = (SocketChannel) ctx.channel();

                    }
                },

                new ProtobufVarint32FrameDecoder()
//                new ProtobufDecoder(DuplexProtocol.WirePayload.getDefaultInstance())

                //new HttpResponseEncoder(),
                //new HttpClientCodec()
                //new HttpObjectAggregator(8192)
        );

        ch.writeOutbound(new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/"));
        ch.writeInbound(Unpooled.copiedBuffer(array));
        ch.finish();
        Object b = ch.readInbound();
        Object c = ch.readInbound();

        HttpResponse content = (HttpResponse) ch.readInbound();
        int i =1;
        HttpContent content2 = (HttpContent) ch.readInbound();
        i++;
        int a = content2.content().readableBytes();
        byte[] decodedData = new byte[a];
        String str = new String(decodedData, StandardCharsets.UTF_8);
        content2.content().readBytes(decodedData);
        i++;
        //Object b = ch.readInbound();
        content2.release();
    }
}
