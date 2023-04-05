package com.imedia.util;

import com.google.gson.Gson;
import com.imedia.config.application.RedisCfg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CallRedis {
    static final Logger logger = LogManager.getLogger(CallRedis.class);
    private static final Gson GSON = new Gson();

    /**
     * Đẩy dữ liệu lên queue Redis
     *
     * @param queueName
     * @param dataRequest
     * @return
     */
    public static Boolean pushQueue(String queueName, String dataRequest, Integer index) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            if (index != null)
                jedis.select(index);
            else
                jedis.select(RedisCfg.database_index);
            // do operations with jedis resource
            jedis.rpush(queueName.getBytes(StandardCharsets.UTF_8), dataRequest.getBytes(StandardCharsets.UTF_8));
            logger.info("=====DATA TO QUEUE=====:" + queueName + "||" + dataRequest);
        } catch (Exception e) {
            logger.info("pushToQueue :" + GSON.toJson(dataRequest), e);
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return true;
    }

    public static Boolean pushMaincoreQueue(String queueName, String dataRequest) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.hostMaincore, RedisCfg.portMaincore);
            if (RedisCfg.authenMaincore)
                jedis.auth(RedisCfg.passMaincore);
            jedis.select(5);
            // do operations with jedis resource
            jedis.rpush(queueName.getBytes(StandardCharsets.UTF_8), dataRequest.getBytes(StandardCharsets.UTF_8));
            logger.info("=====DATA TO QUEUE MAINCORE 2=====:" + queueName + "||" + dataRequest);
        } catch (Exception e) {
            logger.info("=====DATA TO QUEUE MAINCORE 2 EXCEPTION=====" + GSON.toJson(dataRequest), e);
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return true;
    }

    public static Boolean pushFirstQueue(String queueName, String dataRequest) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            // do operations with jedis resource
            jedis.lpush(queueName.getBytes(StandardCharsets.UTF_8), dataRequest.getBytes(StandardCharsets.UTF_8));
            logger.info("=====DATA TO QUEUE=====:" + queueName + "||" + dataRequest);
        } catch (Exception e) {
            logger.info("pushToQueue :" + GSON.toJson(dataRequest), e);
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return true;
    }

    public static String getCache(String key) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            // do operations with jedis resource
            String data = jedis.get(key);
//            logger.info("=====GET CACHE DATA=====:" + key);
            return data;
        } catch (Exception e) {
            logger.info("GET CACHE DATA FAILED :" + key, e);
            return null;
        } finally {
            if (jedis != null)
                jedis.close();
        }

    }

    public static Boolean setCacheExpiry(String key, String data) {
        Jedis jedis = null;
        try {
            int remain = DateUtil.getRemainSecondsTilNextDay(3);
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            // do operations with jedis resource
//            jedis.setex(key.getBytes(StandardCharsets.UTF_8), remain, data.getBytes(StandardCharsets.UTF_8));
            jedis.setex(key.getBytes(StandardCharsets.UTF_8), remain, data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.info("SET CACHE DATA FAILED :" + GSON.toJson(data), e);
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return true;
    }

    public static Boolean updateKey(String key, String data) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            long remain = jedis.ttl(key) + 5000;
            // do operations with jedis resource
//            jedis.setex(key.getBytes(StandardCharsets.UTF_8), remain, data.getBytes(StandardCharsets.UTF_8));
            jedis.setex(key.getBytes(StandardCharsets.UTF_8), Math.toIntExact(remain), data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.info("SET CACHE DATA FAILED :" + GSON.toJson(data), e);
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return true;
    }

    public static Boolean setCacheExpiry(String key, String data, int dateAm) {
        Jedis jedis = null;
        try {
            int remain = DateUtil.getRemainSecondsTilNextDay(dateAm);
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            jedis.setex(key.getBytes(StandardCharsets.UTF_8), remain, data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.info("SET CACHE DATA FAILED :" + GSON.toJson(data), e);
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return true;
    }

    public static Boolean setCacheExpiry(String key, int seconds, String data) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            jedis.setex(key.getBytes(StandardCharsets.UTF_8), seconds, data.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.info("SET CACHE DATA FAILED :" + GSON.toJson(data), e);
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return true;
    }

    public static Boolean setCache(String key, String data) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            // do operations with jedis resource
            jedis.set(key.getBytes(StandardCharsets.UTF_8), data.getBytes(StandardCharsets.UTF_8));
//            logger.info("=====SET CACHE DATA=====:" + key + "||" + data);
        } catch (Exception e) {
            logger.info("SET CACHE DATA FAILED :" + GSON.toJson(data), e);
            return false;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return true;
    }

    public static String listenQueue(String queueName) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            String dataFromQueue = jedis.lpop(queueName);
            while (dataFromQueue == null) {
                dataFromQueue = jedis.lpop(queueName);
                Thread.sleep(500);
            }
            return dataFromQueue;
        } catch (Exception e) {
            logger.info("LISTEN QUEUE FAILED :" + queueName, e);
            return null;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    /**
     * Quét dữ liệu từ queue Redis
     *
     * @param queueName
     * @return
     */
    public static String getQueue(String queueName) {
        String res = "";
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            res = jedis.lpop(queueName);
            logger.info("====DATA FROM REDIS===== :" + queueName + "||" + res);
        } catch (Exception e) {
            logger.info("GET QUEUE :" + queueName, e);
            return res;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return res;
    }


    public static List<String> getBatchQueue(String queueName) {
        Jedis jedis = null;
        List<String> datas = new ArrayList<>();
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            for (int i = 0; i < 50; i++) {
                String dataFromQueue = jedis.lpop(queueName);
                if (dataFromQueue != null && !dataFromQueue.isEmpty())
                    datas.add(dataFromQueue);
                else break;
            }
        } catch (Exception e) {
            logger.info("GET ALL QUEUE :" + queueName, e);
            return datas;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return datas;
    }

    public static List<String> getBatchQueue(String queueName, int index) {
        Jedis jedis = null;
        List<String> datas = new ArrayList<>();
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(index);
            for (int i = 0; i < 100; i++) {
                String dataFromQueue = jedis.lpop(queueName);
                if (dataFromQueue != null && !dataFromQueue.isEmpty())
                    datas.add(dataFromQueue);
                else break;
            }
        } catch (Exception e) {
            logger.info("GET ALL QUEUE :" + queueName, e);
            return datas;
        } finally {
            if (jedis != null)
                jedis.close();
        }
        return datas;
    }

    public static void delCache(String key) {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(RedisCfg.database_index);
            jedis.del(key);
        } catch (Exception e) {
            logger.info("DEL CACHE :" + key, e);
            return;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public static String generateSequence() {
        Jedis jedis = null;
        try {
            jedis = new Jedis(RedisCfg.host, RedisCfg.port);
            if (RedisCfg.authen)
                jedis.auth(RedisCfg.pass);
            jedis.select(0);
            Long seqKey = jedis.incr("seq_key");
            if (seqKey < 10000 || seqKey > 99999) {
                jedis.set("seq_key", "10000");
                seqKey = 10000L;
            }
            return String.valueOf(seqKey);
        } catch (Exception e) {
            logger.info("GET SEQUENCE EXCEPTION :", e);
            return null;
        } finally {
            if (jedis != null)
                jedis.close();
        }
    }

    public static void main(String[] args) {
        List<BigDecimal> a = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
//            Runnable myRunnable = new Runnable(){
//                public void run(){
            BigDecimal b = GenerateTransactionId.generateUniqueOrderCode();
            a.add(b);
            System.out.println(b);
//                }
//            };
//            Thread thread = new Thread(myRunnable);
//            thread.start();
        }
        System.out.println(a.size());
        List<BigDecimal> c = a.stream().distinct().collect(Collectors.toList());
        System.out.println(c.size());

    }
}
