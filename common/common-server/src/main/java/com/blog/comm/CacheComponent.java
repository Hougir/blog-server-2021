package com.blog.comm;

import com.alibaba.fastjson.JSONObject;
import com.blog.exception.CacheException;
import com.blog.util.CommUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
@Slf4j
@Component
@RefreshScope
@ConditionalOnProperty(prefix = "cache", name = "enable", havingValue = "true")
public class CacheComponent {
    /**
     * 缓存
     */
    @Resource
    private RedisTemplate redisTemplate;

    public boolean exists(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hasExists(String key, String item) {
        try {
            return redisTemplate.opsForHash().hasKey(key, item);
        } catch (Exception ex) {
            return false;
        }

    }

    public boolean setNx(String key, Long expir) {
        boolean flag = false;
        try {
            flag = (Boolean) redisTemplate.execute(new RedisCallback() {
                @Nullable
                @Override
                public Object doInRedis(RedisConnection redisConnection) throws DataAccessException {
                    return redisConnection.setNX(key.getBytes(), longToByte(expir));
                }
            });
            return flag;
        } catch (Exception e) {
            log.error("取缓存异常, key = {}, ex = {}", key, e);
            return flag;
        }
    }

    public boolean setNx(String key, String value, long expires) {
        return setNx(key, value, expires, TimeUnit.SECONDS);
    }

    public boolean setNx(String key, String value, long expires, TimeUnit timeUnit) {
        boolean flag = false;
        try {
            flag = (boolean) redisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.set(key.getBytes(), value.getBytes(), Expiration.from(expires, timeUnit),
                            RedisStringCommands.SetOption.ifAbsent()));
        } catch (Exception e) {
            log.error("设置缓存异常, key = {}", key, e);
        }
        return flag;
    }

    /**
     * long类型转成byte数组
     */
    private static byte[] longToByte(long number) {
        byte[] b = new byte[8];
        for (int i = 0; i < b.length; i++) {
            // 将最低位保存在最低位 temp = temp
            b[i] = new Long(number & 0xff).byteValue();
            // >> 8;// 向右移8位
        }
        return b;
    }

    public Long incrBy(String key, long val) {
        Long v = null;

        try {
            v = redisTemplate.opsForValue().increment(key, val);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Long incrBy(String key, String filed, long val) {
        Long v = null;

        try {
            v = redisTemplate.opsForHash().increment(key, filed, val);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Long incr(String key) {
        return this.incrBy(key, 1);
    }

    public Long incr(String key, long val, int interval, TimeUnit unit) {
        Long v = null;

        try {
            v = this.incrBy(key, val);
            // val小于0表示回退，所以要忽略
            if (v == val && val > 0) {
                redisTemplate.expire(key, interval, unit);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean expire(String key, int interval) {
        Boolean v = false;

        try {
            redisTemplate.expire(key, interval, TimeUnit.SECONDS);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean expire(String key, int interval, TimeUnit unit) {
        Boolean v = false;

        try {
            redisTemplate.expire(key, interval, unit);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean expireAt(String key, Date expireDate) {
        Boolean v = false;

        try {
            redisTemplate.expireAt(key, expireDate);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean add(String key, Serializable value) {
        Boolean v = false;

        try {
            redisTemplate.opsForValue().set(key, value);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean add(String key, Serializable value, int minutes) throws CacheException {
        Boolean v = false;

        try {
            redisTemplate.opsForValue().set(key, value, minutes, TimeUnit.MINUTES);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean add(String key, Serializable value, int interval, TimeUnit unit) {
        Boolean v = false;

        try {
            redisTemplate.opsForValue().set(key, value, interval, unit);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addForLong(String key, Serializable value, long interval, TimeUnit unit) {
        Boolean v = false;

        try {
            redisTemplate.opsForValue().set(key, value, interval, unit);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean expire(String key, long interval, TimeUnit unit) {
        Boolean v = false;

        try {
            redisTemplate.expire(key, interval, unit);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    @Async
    public void addAsyn(final String key, final Serializable value) {
        add(key, value);
    }

    @Async
    public void addAsyn(final String key, final Serializable value, final int minutes) {
        add(key, value, minutes);
    }

    @Async
    public void addAsyn(final String key, final Serializable value, final int interval, final TimeUnit unit) {
        add(key, value, interval, unit);
    }

    public Object getAndSet(String key, Serializable value, int interval, TimeUnit unit) {
        Object v = null;

        try {
            v = redisTemplate.opsForValue().getAndSet(key, value);
            if (v != null) {
                redisTemplate.expire(key, interval, unit);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <T> T getOrSet(String key, Supplier<T> supplier, Integer minutes){
        return getOrSet(key, supplier, minutes, TimeUnit.MINUTES);
    }

    public <T> T getOrSet(String key, Supplier<T> supplier, Integer time, TimeUnit timeUnit){
        T t = null;
        try {
            t = CommUtils.cast(redisTemplate.opsForValue().get(key));
        } catch (Exception ex){
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        if(t == null){
            t = supplier.get();
            if(t != null) {
                try {
                    if(time == null){
                        redisTemplate.opsForValue().set(key, t);
                    } else {
                        redisTemplate.opsForValue().set(key, t, time, timeUnit);
                    }
                    log.info("添加缓存成功,key = {}, value = {}",key, JSONObject.toJSONString(t));
                } catch (Exception ex){
                    log.error("添加缓存异常, key = {}, ex = {}", key, ex);
                }
            }
        }
        return t;
    }

    public Boolean addList(String key, Collection<Serializable> values) {
        Boolean v = false;

        try {
            if (values != null && values.size() > 0) {
                redisTemplate.opsForList().leftPushAll(key, values);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <T> Boolean addList(String key, List<T> values) {
        Boolean v = false;

        try {
            if (values != null && values.size() > 0) {
                Collection datas = values;
                redisTemplate.opsForList().leftPushAll(key, datas);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <T> Boolean rightPushAll(String key, List<T> values) {
        Boolean v = false;

        try {
            if (values != null && !values.isEmpty()) {
                redisTemplate.opsForList().rightPushAll(key, values);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addList(String key, Collection values, int minutes) {
        Boolean v = false;

        try {
            redisTemplate.opsForList().leftPushAll(key, values);
            redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addList(String key, List values, int minutes) {
        Boolean v = false;

        try {
            redisTemplate.opsForList().leftPushAll(key, values);
            redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <T> Boolean listAppend(final String key, T value) {
        Boolean v = false;

        try {
            if (null != value) {
                redisTemplate.opsForList().leftPush(key, value);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    @Async
    public <T> void listAppendAsyn(final String key, T value) {
        listAppend(key, value);
    }

    @Async
    public void addListAsyn(final String key, final Collection<Serializable> values) {
        addList(key, values);
    }

    @Async
    public void addListAsyn(final String key, final Collection<Serializable> values, final int minutes) {
        addList(key, values, minutes);
    }

    public Boolean addMap(String key, Map<Object, Object> valueMap) {
        Boolean v = false;

        try {
            if (valueMap != null && valueMap.keySet().size() > 0) {
                redisTemplate.opsForHash().putAll(key, valueMap);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <K, V> Boolean addMap2(String key, Map<K, V> valueMap) {
        Boolean v = false;

        try {
            if (valueMap != null && valueMap.keySet().size() > 0) {
                redisTemplate.opsForHash().putAll(key, valueMap);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <K, V> Boolean addMap2(String key, Map<K, V> valueMap, int minutes) {
        Boolean v = false;

        try {
            if (valueMap != null && valueMap.keySet().size() > 0) {
                redisTemplate.opsForHash().putAll(key, valueMap);
                redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addMap(String key, Map<Object, Object> valueMap, int minutes) {
        Boolean v = false;

        try {
            redisTemplate.opsForHash().putAll(key, valueMap);
            redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    @Async
    public void addMapAsyn(final String key, final Map<Object, Object> valueMap) {
        addMapAsyn(key, valueMap);
    }

    @Async
    public void addMapAsyn(final String key, final Map<Object, Object> valueMap, final int minutes) {
        addMapAsyn(key, valueMap, minutes);
    }

    public Map<Object, Object> getMap(String key) {
        Map<Object, Object> v = null;

        try {
            v = redisTemplate.opsForHash().entries(key);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <K, V> Map<K, V> getMap2(String key) {
        Map<K, V> v = null;

        try {
            v = redisTemplate.opsForHash().entries(key);
            log.debug("获取缓存, key = {}, v = {}", key, v);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean addOneToList(String key, Serializable value) {
        Boolean v = false;

        try {
            redisTemplate.opsForList().leftPush(key, value);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    @Async
    public void addOneToListAsyn(final String key, final Serializable value) {
        addOneToList(key, value);
    }

    public Object get(String key) {
        Object v = null;

        try {
            v = redisTemplate.opsForValue().get(key);
            log.debug("获取缓存, key = {}, value = {}", key, v);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public List<Serializable> getList(String key) {
        List<Serializable> v = null;

        try {
            Long size = redisTemplate.opsForList().size(key);
            v = redisTemplate.opsForList().range(key, 0, size);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <T> List<T> getObjectList(String key) {
        List<T> v = null;

        try {
            Long size = redisTemplate.opsForList().size(key);
            v = redisTemplate.opsForList().range(key, 0, size);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public <T> List<T> popObjectList(String key) {
        List<T> v = null;

        try {
            redisTemplate.multi();
            Long size = redisTemplate.opsForList().size(key);
            v = redisTemplate.opsForList().range(key, 0, size);
            for (T item : v) {
                redisTemplate.opsForList().remove(key, 0, item);
            }
            redisTemplate.exec();
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Serializable getListFirstOne(String key) {
        Serializable v = null;

        try {
            List<Serializable> le = redisTemplate.opsForList().range(key, 0, 1);
            if (le != null && le.size() > 0) {
                v = le.get(0);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Integer getCountLike(String keyPrefix) {
        Integer v = null;

        try {
            if (StringUtils.isNotEmpty(keyPrefix)) {
                Set<String> matchedCacheKeys = redisTemplate.keys(keyPrefix + "*");
                v = matchedCacheKeys.size();
            }
        } catch (Exception ex) {
            log.error("取缓存异常, keyPrefix = {}, ex = {}", keyPrefix, ex);
        }
        return v;
    }

    public Boolean remove(String key) {
        Boolean v = false;

        try {
            redisTemplate.delete(key);
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public Boolean removeLike(String keyPrefix) {
        Boolean v = false;

        try {
            if (StringUtils.isNotEmpty(keyPrefix)) {
                Set<String> matchedCacheKeys = redisTemplate.keys(keyPrefix + "*");
                for (String cacheKey : matchedCacheKeys) {
                    this.remove(cacheKey);
                }
            }
            v = true;
        } catch (Exception ex) {
            log.error("取缓存异常, keyPrefix = {}, ex = {}", keyPrefix, ex);
        }
        return v;
    }

    public Set<String> getAllKeyByKeyPrefix(String keyPrefix) {

        Set<String> matchedCacheKeys = null;
        try {
            if (StringUtils.isNotEmpty(keyPrefix)) {
                matchedCacheKeys = redisTemplate.keys(keyPrefix + "*");
            }
        } catch (Exception ex) {
            log.error("取缓存异常, keyPrefix = {}, ex = {}", keyPrefix, ex);
        }
        return matchedCacheKeys;
    }

    public void lpushList(String key, final Serializable value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    public void rpushList(String key, final Serializable value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public Object rpopList(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    public Long getListSize(String key) {
        Long v = null;

        try {
            v = redisTemplate.opsForList().size(key);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    public List<Serializable> popList(String key, int offset) {
        List<Serializable> v = null;

        try {
            v = redisTemplate.opsForList().range(key, 0, offset - 1);
            for (Serializable item : v) {
                redisTemplate.opsForList().remove(key, 0, item);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return v;
    }

    /**
     * 从redis中获取list集合值并删除
     *
     * @param key
     * @return
     */
    public String popOne(String key) {
        List<Serializable> v = null;
        String value = null;
        try {
            v = redisTemplate.opsForList().range(key, 0, 0);
            for (Serializable item : v) {
                redisTemplate.opsForList().remove(key, 0, item);
            }
            if (CommUtils.isNull(v)) {
                return value;
            }
            value = String.valueOf(v.get(0));
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return value;
    }

    /**
     * <p>
     * 通过key从对应的list中删除指定的count个 和 value相同的元素
     * </p>
     *
     * @param key
     * @param count 当count为0时删除全部
     * @param value
     * @return 返回被删除的个数
     */
    public Long listRemove(String key, Long count, String value) {
        return redisTemplate.opsForList().remove(key, count, value);
    }

    public void hset(String key, String field, Serializable value, int interval, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            if (interval > 0) {
                redisTemplate.expire(key, interval, timeUnit);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
    }

    public void hset(String key, String field, Serializable value, int minutes) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            if (minutes > 0) {
                redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
            }
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
    }

    public boolean hexists(String key, String field) {
        try {
            return redisTemplate.opsForHash().hasKey(key, field);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return false;
    }

    public void hset(String key, String field, Serializable value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
    }

    public Object hget(String key, String field) {
        try {
            return redisTemplate.opsForHash().get(key, field);
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return null;
    }

    @Async
    public void hsetAsyn(final String key, final String field, final Serializable value,
                         final int interval, final TimeUnit timeUnit) {
        redisTemplate.opsForHash().put(key, field, value);
        if (interval > 0) {
            redisTemplate.expire(key, interval, timeUnit);
        }
    }

    @Async
    public void hsetAsyn(final String key, final String field, final Serializable value, final int minutes) {
        redisTemplate.opsForHash().put(key, field, value);
        if (minutes > 0) {
            redisTemplate.expire(key, minutes, TimeUnit.MINUTES);
        }
    }

    @Async
    public void hsetAsyn(final String key, final String field, final Serializable value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    public Object hgetAsyn(final String key, final String field) {
        redisTemplate.opsForHash().get(key, field);

        return null;
    }

    public Boolean hdel(String key, String field) {
        try {
            Long cnt = redisTemplate.opsForHash().delete(key, field);

            return true;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return false;
    }

    public RedisTemplate<String, Serializable> getRedisCacheTemplate() {
        return redisTemplate;
    }

    public void setRedisCacheTemplate(RedisTemplate<String, Serializable> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <V> Boolean zadd(String key, V value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    public <V> Set<V> rangeByScore(String key, double min, double max) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max);
    }

    public <V> Set<V> rangeByScore(String key, double min, double max, long offset, long count) {
        return redisTemplate.opsForZSet().rangeByScore(key, min, max, offset, count);
    }

    public Long zRemove(String key, Object... values) {
        return this.redisTemplate.opsForZSet().remove(key, values);
    }

    public void subscribe(String channel, Object message) {
        redisTemplate.convertAndSend(channel, message);
    }

    public Object deserialize(byte[] body) {
        return redisTemplate.getValueSerializer().deserialize(body);
    }


    /**
     * 添加分布式锁
     *
     * @param key     key
     * @param value   value
     * @param expires 过期时间
     * @return 是否添加成功
     */
    public boolean lock(String key, String value, long expires, TimeUnit timeUnit) {
        return lock(key, expires, timeUnit);
    }

    public boolean lock(String key, long expires, TimeUnit timeUnit) {
        try {
            Long val = this.incrBy(key, 1l);
            log.debug("RedisLockKey = {}, lock = {}", key, val);
            if (Objects.isNull(val)) {
                redisTemplate.expire(key, 2, TimeUnit.SECONDS);
            }
            if (Objects.nonNull(val) && val == 1) {
                redisTemplate.expire(key, expires, timeUnit);
            }
            return val == 1;
        } catch (Exception ex) {
            log.error("取缓存异常, key = {}, ex = {}", key, ex);
        }
        return false;
    }

    /**
     * 释放分布式锁
     *
     * @param key   key
     * @param value value
     * @return 是否释放成功
     */
    public boolean releaseLock(String key, String value) {
        return redisTemplate.delete(key);
    }
}

