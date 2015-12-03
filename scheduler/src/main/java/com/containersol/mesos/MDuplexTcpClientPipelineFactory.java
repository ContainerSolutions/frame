package com.containersol.mesos;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.googlecode.protobuf.pro.duplex.RpcSSLContext;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.handler.ClientConnectResponseHandler;
import com.googlecode.protobuf.pro.duplex.handler.Handler;
import com.googlecode.protobuf.pro.duplex.wire.DuplexProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.ssl.SslHandler;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.io.SessionInputBuffer;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.channels.Channels;
import java.util.List;

import static com.googlecode.protobuf.pro.duplex.handler.Handler.FRAME_DECODER;
import static com.googlecode.protobuf.pro.duplex.handler.Handler.PROTOBUF_ENCODER;

public class MDuplexTcpClientPipelineFactory extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        p.addLast(
                new HttpClientCodec(),
                new HttpObjectAggregator(8192),
                new ProtobufVarint32FrameDecoder()
                //new ProtobufDecoder(DuplexProtocol.WirePayload.getDefaultInstance(),getWirelinePayloadExtensionRegistry())
               // handler);
        );
//
//        RpcSSLContext ssl = getSslContext();
//        if ( ssl != null ) {
//            p.addLast(Handler.SSL, new SslHandler(ssl.createClientEngine()) );
//        }
//
//        p.addLast(Handler.FRAME_DECODER, new ProtobufVarint32FrameDecoder());
//        p.addLast(Handler.PROTOBUF_DECODER, new ProtobufDecoder(DuplexProtocol.WirePayload.getDefaultInstance(),getWirelinePayloadExtensionRegistry()));
//
//        p.addLast(Handler.FRAME_ENCODER, new ProtobufVarint32LengthFieldPrepender());
//        p.addLast(Handler.PROTOBUF_ENCODER, new ProtobufEncoder());
//
//        // the connectResponseHandler is swapped after the client connection
//        // handshake with the RpcClient for the Channel
//        p.addLast(Handler.CLIENT_CONNECT, new ClientConnectResponseHandler());
    }

    private class HttpDecoder extends ByteToMessageDecoder {


        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {

            in.markReaderIndex();
            final byte[] buf = new byte[5];
            for (int i = 0; i < buf.length; i ++) {
                if (!in.isReadable()) {
                    in.resetReaderIndex();
                    return;
                }

                buf[i] = in.readByte();
                if (buf[i] >= 0) {
                    int length =  CodedInputStream.newInstance(buf, 0, i + 1).readRawVarint32();
                    if (length < 0) {
                        throw new CorruptedFrameException("negative length: " + length);
                    }

                    if (in.readableBytes() < length) {
                        in.resetReaderIndex();
                        return;
                    } else {
                        out.add(in.readBytes(length));
                        return;
                    }
                }
            }

            // Couldn't find the byte whose MSB is off.
            throw new CorruptedFrameException("length wider than 32-bit");
        }
    }

    private class HttpEncoder extends MessageToByteEncoder<ByteBuf> {
        @Override
        protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {

        }
    }

    private class DataSource implements Runnable {
        private PipedOutputStream dataPipe;
        private ByteBuf in;

        public DataSource(PipedOutputStream dataPipe, ByteBuf in) {
            this.dataPipe = dataPipe;
            this.in = in;
        }

        @Override
        public void run() {
            try{
                int length = in.readableBytes();
                final byte[] buf = new byte[length];
                in.readBytes(buf);
                dataPipe.write(buf, 0, buf.length);

            } catch (IOException e) {
                e.printStackTrace();
            } finally
            {
                try
                {
                    dataPipe.flush();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private class DataConsumer implements Runnable  {
        private PipedInputStream convertPipe;

        public DataConsumer(PipedInputStream convertPipe) {
            this.convertPipe = convertPipe;
        }

        @Override
        public void run() {

        }
    }
}
