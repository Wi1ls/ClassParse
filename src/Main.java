/**
 * Copyright (c) 2019, Bongmi
 * All rights reserved
 * Author: wi1ls@bongmi.com
 */

public class Main {
  public static void main(String[] args) {
    ClzObject clzObject = ClzObject.readClass("/Users/wi1ls/IdeaProjects/Todo/src/Test.class");
    clzObject.parseClass();
  }
}


