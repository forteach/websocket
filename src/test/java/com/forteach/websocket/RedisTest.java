package com.forteach.websocket;

import com.forteach.websocket.domain.QuestionType;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import javax.annotation.Resource;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/26  10:49
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisTest() {
        QuestionType questionType = QuestionType.BigQuestion;
        System.out.println(questionType.name());
        stringRedisTemplate.opsForValue().set("1Test", "redis ok");
//
//        System.out.println(stringRedisTemplate.opsForValue().get("1Test"));

    }


}
