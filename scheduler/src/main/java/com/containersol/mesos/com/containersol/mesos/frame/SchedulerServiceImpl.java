package com.containersol.mesos.com.containersol.mesos.frame;

import com.google.protobuf.*;
import org.apache.mesos.scheduler.Protos;
import org.apache.mesos.scheduler.Scheduler;

public class SchedulerServiceImpl extends Scheduler.NonBlockingSchedulerService {

    @Override
    public void onOffers(RpcController controller, Protos.Event.Offers request, RpcCallback<Scheduler.Empty> done) {

    }

    @Override
    public void onRescind(RpcController controller, Protos.Event.Rescind request, RpcCallback<Scheduler.Empty> done) {

    }

    @Override
    public void onUpdate(RpcController controller, Protos.Event.Update request, RpcCallback<Scheduler.Empty> done) {

    }

    @Override
    public void onMessage(RpcController controller, Protos.Event.Message request, RpcCallback<Scheduler.Empty> done) {

    }

    @Override
    public void onFailure(RpcController controller, Protos.Event.Failure request, RpcCallback<Scheduler.Empty> done) {

    }

    @Override
    public void onError(RpcController controller, Protos.Event.Error request, RpcCallback<Scheduler.Empty> done) {

    }

    @Override
    public void onHearbeat(RpcController controller, Scheduler.Heartbeat request, RpcCallback<Scheduler.Empty> done) {

    }
}
