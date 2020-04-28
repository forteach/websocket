package com.forteach.websocket;

import com.forteach.websocket.domain.QuestionType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/26  10:49
 * @see <a href="https://www.w3xue.com/exp/article/20192/20723.html">issues</a>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class RedisTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Test
    public void redisTest() {
        QuestionType questionType = QuestionType.TiWen;
        System.out.println(questionType.name());
        stringRedisTemplate.opsForValue().set("1Test", "redis ok");
//
//        System.out.println(stringRedisTemplate.opsForValue().get("1Test"));

    }


}
