package com.imedia.config.application;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class RedisCfg {
    private static final Properties properties = new Properties();
    public static String host;
    public static int port;
    public static Boolean authen = false;
    public static String pass;
    public static int database_index;
    public static String hostMaincore;
    public static int portMaincore;
    public static Boolean authenMaincore = false;
    public static String passMaincore;
    public static int database_indexMaincore;

    public static void loadProperties() throws IOException {

        String fileName = RootConfig.getInstance().getFile("redis.cfg");
        try {
            FileInputStream propsFile = new FileInputStream(fileName);
            properties.load(propsFile);
            host = properties.getProperty("host", host);
            port = getIntProperty("port", port);
            authen = getBoolProperty("authen", authen);
            pass = properties.getProperty("pass", pass);
            database_index = getIntProperty("database_index", database_index);
            hostMaincore = properties.getProperty("hostMaincore", hostMaincore);
            portMaincore = getIntProperty("portMaincore", portMaincore);
            authenMaincore = getBoolProperty("authenMaincore", authenMaincore);
            passMaincore = properties.getProperty("passMaincore", passMaincore);
            database_indexMaincore = getIntProperty("database_indexMaincore", database_indexMaincore);
            propsFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static int getIntProperty(String propName, int defaultValue) {
        return Integer.parseInt(properties.getProperty(propName, Integer.toString(defaultValue)));
    }

    static boolean getBoolProperty(String propName, boolean defaultValue) {
        if (properties.getProperty(propName).equalsIgnoreCase("true"))
            return true;
        else if (properties.getProperty(propName).equalsIgnoreCase("false"))
            return false;
        else
            return defaultValue;
    }

    public static void main(String[] args) {
        System.out.println(RedisCfg.authen);
    }

    static {
        try {
            loadProperties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
