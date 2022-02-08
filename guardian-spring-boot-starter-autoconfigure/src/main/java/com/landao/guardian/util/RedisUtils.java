package com.landao.guardian.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;

public class RedisUtils {

    public static RedisTemplate<String, Object> redis;

    public static ValueOperations<String, Object> value;

    public static SetOperations<String, Object> set;

    public static HashOperations<String,String,Object> map;

    public static ListOperations<String, Object> list;

    @Autowired
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate){
        RedisUtils.redis=redisTemplate;
        value=redis.opsForValue();
        set=redis.opsForSet();
        map=redis.opsForHash();
        list = redis.opsForList();
    }


}
