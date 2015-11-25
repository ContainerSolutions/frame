package com.containersol.mesos.com.containersol.mesos.frame;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.RpcCallback;
import com.google.protobuf.RpcController;

public class SchedulerService implements com.google.protobuf.Service {
    @Override
    public Descriptors.ServiceDescriptor getDescriptorForType() {
        return null;
    }

    @Override
    public void callMethod(Descriptors.MethodDescriptor method, RpcController controller, Message request, RpcCallback<Message> done) {

    }

    @Override
    public Message getRequestPrototype(Descriptors.MethodDescriptor method) {
        return null;
    }

    @Override
    public Message getResponsePrototype(Descriptors.MethodDescriptor method) {
        return null;
    }
}
