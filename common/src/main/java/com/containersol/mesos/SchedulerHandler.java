package com.containersol.mesos;

/**
 * Created by lguminski on 23/11/15.
 */
public interface SchedulerHandler {

    void onInit();

    void onSubscribed();

    void onOffers();

    void onRescind();

    void onUpdate();

    void onMessage();

    void onFailure();

    void onHeartbeat();

}
