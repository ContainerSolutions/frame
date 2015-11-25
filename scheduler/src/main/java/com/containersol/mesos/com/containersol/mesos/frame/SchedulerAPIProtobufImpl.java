/*
 * (C) Copyright 2015 Container Solutions B.V. (http://www.container-solutions.com/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
package com.containersol.mesos.com.containersol.mesos.frame;

import com.containersol.mesos.MesosAPIException;
import com.containersol.mesos.SchedulerAPI;
import com.containersol.mesos.model.FrameworkInfo;

import org.apache.mesos.scheduler.Protos;

import java.io.IOException;
import java.io.OutputStream;


public class SchedulerAPIProtobufImpl implements SchedulerAPI {

    private OutputStream outputStream;


    public SchedulerAPIProtobufImpl(OutputStream outputStream) {
        super();
        this.outputStream = outputStream;
    }

    @Override
    public void subscribe(FrameworkInfo frameworkInfo, boolean force) throws MesosAPIException {
        try {
            Protos.Call.Subscribe.newBuilder().setFrameworkInfo(org.apache.mesos.Protos.FrameworkInfo.getDefaultInstance().newBuilderForType()
                    .setUser(frameworkInfo.getUser()).setName(frameworkInfo.getName())).setForce(force).build().writeTo(outputStream);
        } catch (IOException e) {
            throw new MesosAPIException("Failed to subscribe", e);
        }
    }

    @Override
    public void tearDown() throws MesosAPIException {

    }

    @Override
    public void accept() throws MesosAPIException {

    }

    @Override
    public void decline() throws MesosAPIException {

    }

    @Override
    public void kill() throws MesosAPIException {

    }

    @Override
    public void shutDown() throws MesosAPIException {

    }

    @Override
    public void acknowledge() throws MesosAPIException {

    }

    @Override
    public void reconcile() throws MesosAPIException {

    }

    @Override
    public void message() throws MesosAPIException {

    }

    @Override
    public void request() throws MesosAPIException {

    }
}
