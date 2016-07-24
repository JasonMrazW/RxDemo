package com.jasonmrazw.rxdemo.event;

/**
 * Created by jasonmrazw on 16/7/24.
 */
public class UpdateUIEvent {
    public String event;

    public UpdateUIEvent(String event) {
        this.event = event;
    }

    @Override
    public String toString() {
        return "UpdateUIEvent{" +
                "event='" + event + '\'' +
                '}';
    }
}
