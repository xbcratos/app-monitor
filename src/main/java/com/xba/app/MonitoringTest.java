package com.xba.app;

import com.xba.app.monitoring.AppMonitor;

public class MonitoringTest {

  public static void main(String[] args) {
    testJvm();
  }

  public static void testJvm() {
    AppMonitor appMonitor = new AppMonitor();
    System.out.println(appMonitor.toString());
  }

}