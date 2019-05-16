/**
 * Copyright (c) 2019, Bongmi
 * All rights reserved
 * Author: wi1ls@bongmi.com
 */

public class Test implements TestInterface {
  @Deprecated
  @MyAnnotation(value = 1)
  public static final int field1 = 0;
  public final float field2 = 0.5f;
  public final double field3 = 0.5f;
  public final long field4 = 8888888;

  public void sayHello() {
    System.out.println("hello world");
  }

  class Inner1{

  }

  class Inner2{

  }
}
