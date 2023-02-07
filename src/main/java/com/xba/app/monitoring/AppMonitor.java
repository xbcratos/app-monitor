package com.xba.app.monitoring;

import com.xba.app.utils.MemoryUtils;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.text.NumberFormat;
import java.time.Instant;

public class AppMonitor {

  private RuntimeMXBean runtimeMxBean;
  private Runtime runtime;
  private MemoryMXBean memoryMxBean;
  private OperatingSystemMXBean operatingSystemMxBean;
  private com.sun.management.OperatingSystemMXBean operatingSystemSunMxBean;
  private ThreadMXBean threadMxBean;

  public AppMonitor() {
    memoryMxBean = ManagementFactory.getMemoryMXBean();
    operatingSystemMxBean = ManagementFactory.getOperatingSystemMXBean();
    operatingSystemSunMxBean = ManagementFactory.getPlatformMXBean(com.sun.management.OperatingSystemMXBean.class);
    threadMxBean = ManagementFactory.getThreadMXBean();
    runtimeMxBean = ManagementFactory.getRuntimeMXBean();
    runtime = Runtime.getRuntime();
  }

  // memory functions
  public String getHeapInit() {
    return MemoryUtils.convertToStringRepresentation(memoryMxBean.getHeapMemoryUsage().getInit());
  }

  public String getHeapCommitted() {
    return MemoryUtils.convertToStringRepresentation(memoryMxBean.getHeapMemoryUsage().getCommitted());
  }

  public String getHeapUsed() {
    return MemoryUtils.convertToStringRepresentation(memoryMxBean.getHeapMemoryUsage().getUsed());
  }

  public String getHeapMax() {
    return MemoryUtils.convertToStringRepresentation(memoryMxBean.getHeapMemoryUsage().getMax());
  }

  // operating system functions

  public String getProcessCpuLoad() {
    return String.valueOf(operatingSystemSunMxBean.getProcessCpuLoad());
  }

  public String getSystemCpuLoad() {
    return String.valueOf(operatingSystemSunMxBean.getSystemCpuLoad());
  }
  public String getSystemName() {
    return operatingSystemMxBean.getName();
  }

  public String getSystemVersion() {
    return operatingSystemMxBean.getVersion();
  }

  public String getSystemAvailableProcessors() {
    return String.valueOf(operatingSystemMxBean.getAvailableProcessors());
  }

  public String getSystemLoad() {
    return String.valueOf(operatingSystemMxBean.getSystemLoadAverage());
  }

  public String getStorageMetrics() {
    StringBuilder storageMetrics = new StringBuilder();
    NumberFormat nf = NumberFormat.getNumberInstance();
    for (Path root : FileSystems.getDefault().getRootDirectories()) {
      StringBuilder specificPathStorageMetrics = new StringBuilder();
      specificPathStorageMetrics.append(root + ": ");
      try {
        FileStore store = Files.getFileStore(root);
        specificPathStorageMetrics.append("available=" + MemoryUtils.convertToStringRepresentation(store.getUsableSpace())
            + ", total=" + MemoryUtils.convertToStringRepresentation(store.getTotalSpace()));
      } catch (IOException e) {
        specificPathStorageMetrics.append("error querying space: " + e.toString());
      } finally {
        specificPathStorageMetrics.append(System.lineSeparator());
        storageMetrics.append(specificPathStorageMetrics);
      }
    }
    return storageMetrics.toString();
  }

  // runtime functions
  public String getJavaVersion() {
    return runtimeMxBean.getVmVersion();
  }

  public String getInputArguments() {
    return String.join(", ", runtimeMxBean.getInputArguments());
  }

  public String getStartTime() {
    return Date.from(Instant.ofEpochMilli(runtimeMxBean.getStartTime())).toString();
  }

  public String getUpTime() {
    long upTime = runtimeMxBean.getUptime();
    long milliseconds = upTime%1000;
    long seconds = upTime/1000;
    long minutes = seconds/60;
    long hours = minutes/60;
    seconds += seconds%60;
    minutes = minutes%60;

    return String.format("%s h %s min %s sec %s ms", hours, minutes, seconds, milliseconds);
  }

  public String getLibraryPath() {
    return runtimeMxBean.getLibraryPath();
  }

  public String getClassPath() {
    return runtimeMxBean.getClassPath();
  }

  public String getSystemProperties() {
    StringBuilder systemPropertiesStrBuilder = new StringBuilder();
    runtimeMxBean.getSystemProperties().entrySet().stream().forEach(e -> addToStrBuilderWithLineSeparator(
        systemPropertiesStrBuilder,
        String.format("%s = %s", e.getKey(), e.getValue())
    ));
    return systemPropertiesStrBuilder.toString();
  }

  public String getFreeMem() {
    return MemoryUtils.convertToStringRepresentation(runtime.freeMemory());
  }

  public String getMaxMem() {
    return MemoryUtils.convertToStringRepresentation(runtime.maxMemory());
  }

  public String getTotalMem() {
    return MemoryUtils.convertToStringRepresentation(runtime.totalMemory());
  }

  // thread functions
  public String getThreadsStatuses() {
    StringBuilder threadsStatuses = new StringBuilder();
    for(Long threadID : threadMxBean.getAllThreadIds()) {
      StringBuilder threadStatus = new StringBuilder();
      ThreadInfo info = threadMxBean.getThreadInfo(threadID);
      threadStatus.append(String.format("Thread name =  %s", info.getThreadName()));
      threadStatus.append(String.format(System.lineSeparator() + "\tstate = %s", info.getThreadState()));
      threadStatus.append(String.format(System.lineSeparator() + "\tCPU time = %s ns",
          threadMxBean.getThreadCpuTime(threadID)));
      addToStrBuilderWithLineSeparator(threadsStatuses, threadStatus.toString());
    }

    return threadsStatuses.toString();
  }

  public String getThreadCount() {
    return String.valueOf(threadMxBean.getThreadCount());
  }

  // other functions
  private void addToStrBuilderWithLineSeparator(StringBuilder jvmMonitorStr, String strToAdd) {
    jvmMonitorStr.append(strToAdd);
    jvmMonitorStr.append(System.lineSeparator());
  }

  @Override
  public String toString() {
    StringBuilder jvmMonitorStr = new StringBuilder();
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "#################### App Info ####################");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "############## CPU ###################");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Process CPU load: %s", getProcessCpuLoad()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("System CPU load: %s", getSystemCpuLoad()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "#####################################");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "############## Memory ################");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Heap init: %s", getHeapInit()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Heap used: %s", getHeapUsed()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Heap committed: %s", getHeapCommitted()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Heap max: %s", getHeapMax()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Free memory: %s", getFreeMem()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Max memory: %s", getMaxMem()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Total memory: %s", getTotalMem()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "#####################################");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "############## System ###############");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("System name: %s", getSystemName()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("System version: %s", getSystemVersion()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("System load in the last minute: %s", getSystemLoad()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("System available processors: %s", getSystemAvailableProcessors()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Storage Metrics: %s", getStorageMetrics()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "#####################################");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "############## Runtime ##############");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Java version: %s", getJavaVersion()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Input Arguments: %s", getInputArguments()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Start Time: %s", getStartTime()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Up Time: %s", getUpTime()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Library Path: %s", getLibraryPath()));
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Class Path: %s", getClassPath()));
    addToStrBuilderWithLineSeparator(
        jvmMonitorStr,
        String.format("System Properties:" + System.lineSeparator() + "%s", getSystemProperties())
    );
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "#####################################");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "############## Thread ###############");
    addToStrBuilderWithLineSeparator(jvmMonitorStr, String.format("Threads count: %s", getThreadCount()));
    addToStrBuilderWithLineSeparator(
        jvmMonitorStr,
        String.format("Threads statuses: " + System.lineSeparator() + "%s", getThreadsStatuses())
    );
    addToStrBuilderWithLineSeparator(jvmMonitorStr, "##################################################");
    return jvmMonitorStr.toString();
  }
}
