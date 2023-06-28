package jungle.fairyTeller.board.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisDao {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean addValueToList(String key, String value) {
        Long result = redisTemplate.opsForValue().increment(key);
        return result != null;
    }

}
