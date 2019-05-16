import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2019, Bongmi
 * All rights reserved
 * Author: wi1ls@bongmi.com
 */

public class ClzObject {
  private static final int MAGIC_LENGTH = 4;
  private static final int MINOR_VERSION_LENGTH = 2;
  private static final int MAJOR_VERSION_LENGTH = 2;
  private static final int CONSTANT_POOL_SIZE = 2;

  private Map<Integer, String> ut8Map = new HashMap<>();

  public void parseClass() {
    parseMagic();
    parseMinorVersion();
    parseMajorVersion();
    parseConstantPoolSize();
    parseAccessFlag();
    parseThisClass();
    parseSuperClass();
    parseInterfaces();
    parseFields();
    parseMethods();
    parseAttribute();
  }


  int index;

  byte[] clzBytes;

  public ClzObject(byte[] clzBytes) {
    this.clzBytes = clzBytes;
  }

  private void parseMagic() {
    System.out.println("魔数:0x" + getValue(MAGIC_LENGTH));
  }

  private void parseMinorVersion() {
    System.out.println("小版本号:0x" + getValue(MINOR_VERSION_LENGTH));
  }


  private void parseMajorVersion() {
    System.out.println("主版本号:0x" +getValue(MAJOR_VERSION_LENGTH));
  }

  private void parseConstantPoolSize() {
    String buffer =getValue(CONSTANT_POOL_SIZE);
    int realSize = toInteger(buffer);
    System.out.println("常量池个数:0x" + buffer+ "[十进制" + realSize + "]");
    for (int i = 1; i < realSize; i++) {
      int tag = toInteger(1);
      parseConstantPool(i, tag);
    }
  }

  private void parseConstantPool(int constantIndex, int tag) {
//    System.out.println("当前 Tag 为" + tag);
    switch (tag) {
      case 1:
        //CONSTANT_Utf8_info
        int length = toInteger(2);
        byte[] utf8Content = new byte[length];
        for (int i = 0; i < length; ++i) {
          utf8Content[i] = clzBytes[index++];
        }
        System.out.println("常量池第" + constantIndex + "项 UTF8_info:" + new String(utf8Content, 0, length));
        ut8Map.put(constantIndex, new String(utf8Content, 0, length));
        break;
      case 3:
        //CONSTANT_Integer_info

        System.out.println("常量池第" + constantIndex + "项 integer_info:" + toInteger(4));
        break;
      case 4:
        // TODO: 2019-05-15
        //CONSTANT_Float_info
        System.out.println("常量池第" + constantIndex + "项 float_info 二进制:0x" + getValue(4));
        break;
      case 5:
        //CONSTANT_Long_info
        System.out.println("常量池第" + constantIndex + "项 long_info:" + toInteger(4));
        break;
      case 6:
        // TODO: 2019-05-15
        //CONSTANT_Double_info
        System.out.println("常量池第" + constantIndex + "项 double_info 二进制:0x" + getValue(8));
        break;
      case 7:
        //CONSTANT_Class_info
        System.out.println("常量池第" + constantIndex + "项 class_info索引为:" + toInteger(2));
        break;
      case 8:
        //CONSTANT_String_info
        System.out.println("常量池第" + constantIndex + "项 string_info索引为:" + toInteger(2));
        break;
      case 9:
        //CONSTANT_Fieldref_info
        System.out.println("常量池第" + constantIndex + "项 fieldRef index1 索引为:" + toInteger(2)
            + ",,,,,, index2 索引为：" + toInteger(2));
        break;
      case 10:
        //CONSTANT_Methodref_info
        System.out.println("常量池第" + constantIndex + "项 method index1 索引为:" + toInteger(2)
            + ",,,,,, index2 索引为：" + toInteger(2));
        break;
      case 11:
        //CONSTANT_Inteface-Methodref_info
        System.out.println("常量池第" + constantIndex + "项 interface-method index1 索引为:" + toInteger(2)
            + ",,,,,, index2 索引为：" + toInteger(2));
        break;
      case 12:
        //CONSTANT_NameAndTyperef_info
        System.out.println("常量池第" + constantIndex + "项 nameAndType index1 索引为:" + toInteger(2)
            + ",,,,,, index2 索引为：" + toInteger(2));
        break;
      case 15:
        //CONSTANT_method_handle_info
        System.out.println("常量池第" + constantIndex + "项 method-Handle kind 值为:" + toInteger(1)
            + ",,,,,, index 索引为：" + toInteger(2));
        break;
      case 16:
        //CONSTANT_Method_Type_info
        System.out.println("常量池第" + constantIndex + "项 method-type 索引为:" + toInteger(2));
        break;
      case 18:
        //CONSTANT_invoke_dynamic_info
        System.out.println("常量池第" + constantIndex + "项 dynamic-info bootstrap-method-attr-index 索引为:" + toInteger(2)
            + ",,,,name_and_type+index索引为:" + toInteger(2));
        break;
    }
  }

  private void parseAccessFlag() {
    System.out.println("class Flag:0x" + getValue(2));
  }

  private void parseThisClass() {
    System.out.println("thisClass 索引为:" + toInteger(2));
  }

  private void parseSuperClass() {
    System.out.println("superClass 索引为:" + toInteger(2));
  }

  private void parseInterfaces() {
    int interfaceCount = toInteger(2);
    System.out.println("interface count 为:" + interfaceCount);
    if (interfaceCount > 0) {
      for (int i = 0; i < interfaceCount; i++) {
        parseInterface(i + 1);
      }
    }
  }

  private void parseInterface(int interfaceCount) {
    System.out.println("实现的第" + interfaceCount + "个接口 索引：" + toInteger(2));
  }

  private void parseFields() {
    int fieldCount = toInteger(2);
    System.out.println("field count 为:" + fieldCount);
    if (fieldCount > 0) {
      for (int i = 0; i < fieldCount; i++) {
        parseField(i + 1);
      }
    }
  }

  private void parseField(int fieldIndex) {
    String accessFlagBuffer = getValue(2);

    int nameIndex = toInteger(2);

    int descriptorIndex = toInteger(2);

    int attributeCount = toInteger(2);

    System.out.println("第" + fieldIndex + "个字段，accessFlag:0x" + accessFlagBuffer
        + ",nameIndex=" + nameIndex + ",descriptorIndex=" + descriptorIndex
        + ",attributeCount=" + attributeCount);
    for (int i = 0; i < attributeCount; i++) {
      parseAttributeInfo(i);
    }
  }

  private void parseCode(int attributeLength) {
    int maxStack = toInteger(getValue(2));
    System.out.println("  *maxStack:" + maxStack);
    int maxLocals = toInteger(getValue(2));
    System.out.println("  *maxLocals:" + maxLocals);
    int codeLength = toInteger(getValue(4));
    System.out.println("  *codeLength:" + codeLength);
    String codes = getValue(codeLength);
    System.out.println("  *codes:" + codes);
    int exceptionLength = toInteger(getValue(2));
    System.out.println("  *exceptionLength:" + exceptionLength);
    for (int i = 0; i < exceptionLength; i++) {
      int startPc = toInteger(getValue(2));
      int endPc = toInteger(getValue(2));
      int handlerPc = toInteger(getValue(2));
      int catchType = toInteger(getValue(2));
      System.out.println("    *第" + (i + 1) + "项异常" + ",startPc=" + startPc + ",endPc=" + endPc + ",handlerPc=" + handlerPc + ",catchType=" + catchType);
    }

    int attributeCount = toInteger(getValue(2));
    System.out.println("  *attributeCount:" + attributeCount);
    for (int i = 0; i < attributeCount; i++) {
      parseAttributeInfo(i);
    }
  }

  private void parseMethods() {
    int methodCount = toInteger(2);
    System.out.println("----方法长度:" + methodCount);
    for (int i = 0; i < methodCount; i++) {
      parseMethod(i);
    }
  }

  private void parseMethod(int countIndex) {
    countIndex++;
    System.out.println("*解析第" + countIndex + "个方法");

    System.out.println("  *access_flag:0x" + getValue(2));

    System.out.println("  *nameIndex:" + toInteger(2));

    System.out.println("  *descriptorIndex:" + toInteger(2));

    int attributeCount = toInteger(2);
    System.out.println("  *attributeCount:" + attributeCount);

    for (int i = 0; i < attributeCount; i++) {
      parseAttributeInfo(i);
    }

  }

  private void parseAttribute() {
    int attributeCount = toInteger(2);
    System.out.println("attributeCount=" + attributeCount);
    for (int i = 0; i < attributeCount; i++) {
      parseAttributeInfo(i);
    }
  }


  //http://www.blogjava.net/DLevin/archive/2011/09/05/358035.html
  //todo
  private void parseAttributeInfo(int attributeInfoIndex) {
    attributeInfoIndex++;
    //u2 attribute_name_index
    int attributeNameIndex = toInteger(2);

    String attributeNameString = ut8Map.get(attributeNameIndex);
    //u4 attribute_length
    int attributeLength = toInteger(4);

    System.out.print("  *属性长度为:" + attributeLength + "//");
    System.out.print("  *第" + attributeInfoIndex + "个属性为:" + ut8Map.get(attributeNameIndex));

    if ("ConstantValue".equals(attributeNameString)) {
      parseConstantValue(attributeLength);
    } else if ("Deprecated".equals(attributeNameString)) {
      parseDeprecated();
    } else if ("RuntimeVisibleAnnotations".equals(attributeNameString)) {
      parseRuntimeVisibleAnnotations(attributeLength);
    } else if ("Code".equals(attributeNameString)) {
      parseCode(attributeLength);
    } else if ("LineNumberTable".equals(attributeNameString)) {
      parseLineNumberTable();
    } else if ("SourceFile".equals(attributeNameString)) {
      parseSourceFile();
    }else if("InnerClasses".equals(attributeNameString)){
      parseInnerClasses();
    } else {
      System.out.println("  *else:第" + attributeInfoIndex + "个属性为:" + ut8Map.get(attributeNameIndex));
    }
  }

  private void parseConstantValue(int attributeLength) {
    if (attributeLength > 0) {
      int attributeInfoIndex = toInteger(2);
      System.out.println("----该属性的索引为:" + attributeInfoIndex);
    }
  }

  private void parseDeprecated() {
  }

  private void parseRuntimeVisibleAnnotations(int attributeLength) {
    if (attributeLength > 0) {
      int annotationsNum = toInteger(2);
      System.out.println("----注解集合长度:" + annotationsNum);

      for (int i = 0; i < annotationsNum; i++) {
        System.out.println("解析第" + (i + 1) + "个注解");

        System.out.println("  *注解 type-index=" + toInteger(2));

        int elementPairsNum = toInteger(2);
        System.out.println("  *注解键值对共" + elementPairsNum);

        if (elementPairsNum > 0) {
          for (int elementPairsNumIndex = 0; elementPairsNumIndex < elementPairsNum; elementPairsNumIndex++) {
            System.out.println("    *解析第" + (elementPairsNumIndex + 1) + "个键值对");
            //解析 key
            System.out.println("      *key索引" + toInteger(2));
            //解析 value
            byte tagByte = clzBytes[index++];
            String tag = new String(new byte[]{tagByte}, 0, 1);
            System.out.print("      *value tag为" + tag);
            switch (tagByte) {
              case 'B':
              case 'C':
              case 'D':
              case 'F':
              case 'I':
              case 'J':
              case 'S':
              case 'Z':
              case 's':
                //基本类型+String
                System.out.println("constant_ (value索引" + toInteger(2) + ")");
                break;
              case 'e':
                // enum constant
                System.out.println("枚举类型名索引" + toInteger(2));
                System.out.println("枚举值索引" + toInteger(2));
                break;
              case 'c':
                //class
                //TODO
                break;
              case '@':
                //annotation type
                //TODO
                break;
              case '[':
                //array
                //TODO
                break;
            }
          }
        }
      }
    }
  }

  private void parseLineNumberTable() {
    int lineNumberTableLength = toInteger(2);
    System.out.println("  *线性表长度:" + lineNumberTableLength);
    for (int i = 0; i < lineNumberTableLength; i++) {
      System.out.println("    *第:" + (i + 1) + "个线性表");
      int startPc = toInteger(2);
      int lineNumber = toInteger(2);
      System.out.println("    *startPc=" + startPc + ",lineNumber=" + lineNumber);
    }
  }

  private void parseSourceFile() {
    int sourceFileIndex = toInteger(2);
    System.out.println(" *sourceFileIndex=" + sourceFileIndex);
  }

  private void parseInnerClasses(){
    int innerClassLength=toInteger(2);
    System.out.println("  *innerClassCount:"+innerClassLength);
    for(int i=0;i<innerClassLength;i++){
      System.out.println("  *解析第"+(i+1)+"个 Inner");
      int innerClassInfoIndex=toInteger(2);
      int outerClassInfoIndex=toInteger(2);
      int innerNameInfoIndex=toInteger(2);
      String innerClassAccessFlagIndex=getValue(2);
      System.out.println("    *innerClassIndex 索引:"+innerClassInfoIndex);
      System.out.println("    *outerClassInfoIndex 索引:"+outerClassInfoIndex);
      System.out.println("    *innerNameInfoIndex 索引:"+innerNameInfoIndex);
      System.out.println("    *innerClassAccessFlagIndex:0x"+innerClassAccessFlagIndex);
    }
  }

  private String getValue(int length) {
    StringBuffer valueBuffer = new StringBuffer();
    for (int i = 0; i < length; i++) {
      valueBuffer.append(toHex(clzBytes[index++]));
    }
    return valueBuffer.toString();
  }

  private String toHex(byte data) {
    String hex = String.format("%x", data);
    return hex.length() == 1 ? "0" + hex : hex;
  }

  private int toInteger(int length) {
    return Integer.valueOf(getValue(length), 16);
  }

  private int toInteger(String buffer) {
    return Integer.valueOf(buffer, 16);
  }

  public static ClzObject readClass(String path) {
    try {

      BufferedInputStream in = new BufferedInputStream(
          new FileInputStream(path));
      ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

      byte[] temp = new byte[1024];
      int size = 0;
      while ((size = in.read(temp)) != -1) {
        out.write(temp, 0, size);
      }
      in.close();
      return new ClzObject(out.toByteArray());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;

  }

}
