import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import java.util.Arrays;

public class Test {

    public static void main(String[] args) {
        GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
        byte[] serialize = genericJackson2JsonRedisSerializer.serialize(1111);
        System.out.println(Arrays.toString(serialize));
    }
}
