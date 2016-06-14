package com.dollarandtrump.angelcar.manager.bus;


import com.squareup.otto.Bus;

/**
 * Created by humnoy on 20/1/59.
 */
@Deprecated
public class BusProvider {
    private static Bus bus;
    private BusProvider() {}
    public static Bus getInstance() {
        if (bus == null)
            bus = new Bus();
        return bus;
    }
}
