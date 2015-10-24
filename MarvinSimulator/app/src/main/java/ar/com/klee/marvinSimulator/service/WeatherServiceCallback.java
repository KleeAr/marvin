package ar.com.klee.marvinSimulator.service;


import ar.com.klee.marvinSimulator.data.Channel;

public interface WeatherServiceCallback {
    void serviceSuccess(Channel channel);

    void serviceFailure(Exception exception);
}