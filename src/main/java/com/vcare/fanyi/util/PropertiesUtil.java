package com.vcare.fanyi.util;

import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class PropertiesUtil {

    public static void main(String[] args) {
        Properties prop = readOrderedPropertiesFile("config/sync.properties");
        String key="lastSyncStamp";
        System.out.println(key + "=" + prop.getProperty(key));
        //printProp(prop);
        prop.setProperty("lastSyncStamp",TimeUtil.getLocalTime());
        writeOrderedPropertiesFile("config/sync.properties",prop,"最后更新时间");
        System.out.println(key + "=" + prop.getProperty(key));
    }

    /**
     * 输出properties的key和value
     */
    public static void printProp(Properties properties) {
        System.out.println("---------（方式一）------------");
        for (String key : properties.stringPropertyNames()) {
            System.out.println(key + "=" + properties.getProperty(key));
        }

        System.out.println("---------（方式二）------------");
        Set<Object> keys = properties.keySet();//返回属性key的集合
        for (Object key : keys) {
            System.out.println(key.toString() + "=" + properties.get(key));
        }

        System.out.println("---------（方式三）------------");
        Set<Map.Entry<Object, Object>> entrySet = properties.entrySet();//返回的属性键值对实体
        for (Map.Entry<Object, Object> entry : entrySet) {
            System.out.println(entry.getKey() + "=" + entry.getValue());
        }

        System.out.println("---------（方式四）------------");
        Enumeration<?> e = properties.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = properties.getProperty(key);
            System.out.println(key + "=" + value);
        }
    }

    /**
     * 读Properties文件（有序）
     */
    public static Properties readOrderedPropertiesFile(String propertiesfile) {
        Properties properties = new OrderedProperties();
        InputStreamReader inputStreamReader = null;
        InputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(propertiesfile));
            //inputStream = new BufferedInputStream(new FileInputStream("config/sync.properties"));
            //prop.load(in);//直接这么写，如果properties文件中有汉子，则汉字会乱码。因为未设置编码格式。
            inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            properties.load(inputStreamReader);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStreamReader != null) {
                try {
                    inputStreamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    /**
     * 写Properties文件（有序）
     */
    public static void writeOrderedPropertiesFile(String propertiesfile,Properties properties,String comments) {
        //properties.setProperty("phone", "10087");
        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            //保存属性到b.properties文件
            //fileOutputStream = new FileOutputStream("config/sync.properties", false);//true表示追加打开,false每次都是清空再重写
            fileOutputStream = new FileOutputStream(propertiesfile, false);//true表示追加打开,false每次都是清空再重写
            //prop.store(oFile, "此参数是保存生成properties文件中第一行的注释说明文字");//这个会两个地方乱码
            //prop.store(new OutputStreamWriter(oFile, "utf-8"), "汉字乱码");//这个就是生成的properties文件中第一行的注释文字乱码
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, "utf-8");
            //properties.store(outputStreamWriter, "lll");
            properties.store(outputStreamWriter, comments);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
