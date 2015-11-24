package com.containersol;

/**
 * Created by lguminski on 23/11/15.
 */
public interface SchedulerAPI {

    void onSubscribe();

    void onTearDown();

    void onAccept();

    void onDecline();

    void onKill();

    void onShuttdown();

    void onAcknowledge();

    void
}
