package com.xba.app.utils;

import java.text.DecimalFormat;

public class MemoryUtils {
  public static final long K = 1024;
  public static final long M = K * K;
  public static final long G = M * K;
  public static final long T = G * K;

  public static String convertToStringRepresentation(final long value){
    final long[] dividers = new long[] { T, G, M, K, 1 };
    final String[] units = new String[] { "TB", "GB", "MB", "KB", "B" };
    if(value < 1)
      throw new IllegalArgumentException("Invalid memory size: " + value);
    String result = null;
    for(int i = 0; i < dividers.length; i++){
      final long divider = dividers[i];
      if(value >= divider){
        result = format(value, divider, units[i]);
        break;
      }
    }
    return result;
  }

  private static String format(final long value,
      final long divider,
      final String unit){
    final double result =
        divider > 1 ? (double) value / (double) divider : (double) value;
    return new DecimalFormat("#,##0.#").format(result) + " " + unit;
  }
}
