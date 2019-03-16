package com.easv.oe.whereami;

import android.location.Location;

/**
 * Created by ole on 11/03/2018.
 */

public interface IViewCallBack {

      void setSpeed(double speed);

      void setCurrentLocation(Location location);

      void setCounter(int count);
}
