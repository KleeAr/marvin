package ar.com.klee.marvin.service;


import ar.com.klee.marvin.data.Channel;

public interface WeatherServiceCallback {
    void serviceSuccess(Channel channel);

    void serviceFailure(Exception exception);
}