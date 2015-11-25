package com.containersol.mesos.com.containersol.mesos.frame;

import com.google.protobuf.ServiceException;
import com.googlecode.protobuf.pro.duplex.ClientRpcController;
import com.googlecode.protobuf.pro.duplex.PeerInfo;
import com.googlecode.protobuf.pro.duplex.RpcClient;
import com.googlecode.protobuf.pro.duplex.client.DuplexTcpClientPipelineFactory;
import com.googlecode.protobuf.pro.duplex.execute.ThreadPoolCallExecutor;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.mesos.scheduler.Protos;
import org.apache.mesos.scheduler.Scheduler;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        int port = 5050;
        String address = "localhost";
        PeerInfo server = new PeerInfo(address, port);
        System.err.println("INFO: Connecting to <" + server + ">");
        DuplexTcpClientPipelineFactory clientFactory = new DuplexTcpClientPipelineFactory();
        ThreadPoolCallExecutor executor = new ThreadPoolCallExecutor(1, 10);
        clientFactory.setRpcServerCallExecutor(executor);
        clientFactory.setConnectResponseTimeoutMillis(10000);
        clientFactory.setCompression(true);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup());
        bootstrap.handler(clientFactory);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000);
        bootstrap.option(ChannelOption.SO_SNDBUF, 1048576);
        bootstrap.option(ChannelOption.SO_RCVBUF, 1048576);

        try {
            RpcClient channel = clientFactory.peerWith(server, bootstrap);
            ClientRpcController controller = channel.newRpcController();
            Scheduler.BlockingMesosService.BlockingInterface blockingMesosService = Scheduler.BlockingMesosService.newBlockingStub(channel);
            Protos.Event.Subscribed subscribed = blockingMesosService.subscribe(controller, Protos.Call.Subscribe.newBuilder()
                    .setFrameworkInfo(org.apache.mesos.Protos.FrameworkInfo.newBuilder()
                            .setUser("frame").setName("Frame Framework").build()).build());
            clientFactory.getRpcServiceRegistry().registerService(new SchedulerServiceImpl());
            blockingMesosService.tearDown(controller, Scheduler.Teardown.newBuilder().setFrameworkId(subscribed.getFrameworkId()).build());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
