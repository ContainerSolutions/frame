package com.containersol.mesos;

import com.containersol.mesos.model.FrameworkInfo;

/**
 * Created by lguminski on 23/11/15.
 */
public interface SchedulerAPI {

    void subscribe(FrameworkInfo frameworkInfo, boolean force) throws MesosAPIException;

    void tearDown() throws MesosAPIException;

    void accept() throws MesosAPIException;

    void decline() throws MesosAPIException;

    void kill() throws MesosAPIException;

    void shutDown() throws MesosAPIException;

    void acknowledge() throws MesosAPIException;

    void reconcile() throws MesosAPIException;

    void message() throws MesosAPIException;

    void request() throws MesosAPIException;

}
