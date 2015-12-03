package com.containersol.mesos;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.protobuf.ProtoHttpContent;
import com.google.api.client.protobuf.ProtoObjectParser;
import com.google.protobuf.MessageLite;
import com.googlecode.protobuf.pro.duplex.ClientRpcController;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClient;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.mesos.scheduler.Protos;
import com.google.api.client.protobuf.ProtocolBuffers;
import org.apache.mesos.scheduler.Scheduler;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class Main {
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

    public static void main(String[] args) {


        int port = 5050;
        String address = "localhost";
        PeerInfo server = new PeerInfo(address, port);
        System.err.println("INFO: Connecting to <" + server + ">");
        MDuplexTcpClientPipelineFactory clientFactory = new MDuplexTcpClientPipelineFactory();
        ThreadPoolCallExecutor executor = new ThreadPoolCallExecutor(1, 10);
        //clientFactory.setRpcServerCallExecutor(executor);
        //clientFactory.setConnectResponseTimeoutMillis(10000);
        //clientFactory.setCompression(true);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.handler(clientFactory);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
        bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);

        try {
            //RpcClient channel = clientFactory.peerWith(server, bootstrap);
            //ClientRpcController controller = channel.newRpcController();
            //Scheduler.BlockingMesosService.BlockingInterface blockingMesosService = Scheduler.BlockingMesosService.newBlockingStub(channel);

            Protos.Call.Subscribe subscribe = Protos.Call.Subscribe.newBuilder()
                    .setFrameworkInfo(org.apache.mesos.Protos.FrameworkInfo.newBuilder()
                            .setUser("frame").setName("Frame Framework").build()).build();
            final org.apache.mesos.scheduler.Protos.Call subscribeCall = org.apache.mesos.scheduler.Protos.Call.newBuilder()
                    .setType(org.apache.mesos.scheduler.Protos.Call.Type.SUBSCRIBE)
                    .setSubscribe(
                            subscribe
                    )
                    .build();

            Protos.Call.Subscribe parsedResponse = ProtocolBuffers.parseAndClose(
                    new ByteArrayInputStream(subscribe.toByteArray()), Protos.Call.Subscribe.class);
            System.out.print(parsedResponse.getFrameworkInfo().getName());
            HttpRequestFactory requestFactory =
                    HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
                        @Override
                        public void initialize(HttpRequest request) {
                            request.setParser(new ProtoObjectParser());
                            request.getHeaders().setAcceptEncoding("recordio").setAccept("application/x-protobuf");
                        }
                    });
            GenericUrl url = new GenericUrl("http://localhost:5050/api/v1/scheduler");
            MessageLite messageLite;
            //MessageLite message = new org.apache.mesos.scheduler.Protos.Call.Subscribe().ne
            HttpRequest mrequest = requestFactory.buildPostRequest(url, new ProtoHttpContent(subscribeCall));

            //final HttpContent content = new JsonHttpContent(new JsonFactory(), );
            //HttpRequest mrequest = requestFactory.buildPostRequest(url, new JsonHttpContent());
            HttpResponse response = mrequest.execute();
            if(response.isSuccessStatusCode()) {
               // Protos value = response.parseAs(Protos.Event.class);

            }
            //clientFactory.getRpcServiceRegistry().registerService(new SchedulerServiceImpl());
            //blockingMesosService.tearDown(controller, Scheduler.Teardown.newBuilder().setFrameworkId(subscribed.getFrameworkId()).build());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
