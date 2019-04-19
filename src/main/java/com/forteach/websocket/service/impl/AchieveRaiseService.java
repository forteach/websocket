package com.forteach.websocket.service.impl;

import com.forteach.websocket.service.Key.ClassRoomKey;
import com.forteach.websocket.domain.AchieveRaise;
import com.forteach.websocket.domain.Students;
import com.forteach.websocket.service.Key.AchieveRaiseKey;
import com.forteach.websocket.service.teacher.push.repeat.RaiseRepeat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 学生推送业务数据处理
 * @author: zjw
 * @version: V1.0
 * @date: 2019/1/2  15:12
 */
@Slf4j
@Service
public class AchieveRaiseService {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private StudentsService studentsService;

    @Resource
    private ClassStudentService classStudentService;

    @Resource
    private RaiseRepeat riseRepeat;

    /**
     * 获得当前课堂提问的题目ID
     *
     * @param circleId
     * @return
     */
    public String getNowQuestionId(String circleId) {
        return hashOperations.get(AchieveRaiseKey.QuestionsIdNow(circleId), "questionId");
    }

    /**
     * 获得当前课堂提问的题目ID
     *
     * @param circleId
     * @return
     */
    public String getNoQuestionType(String circleId) {
        return hashOperations.get(AchieveRaiseKey.QuestionsIdNow(circleId), "questionType");
    }


    /**
     * 获得当前开课课堂列表
     *
     * @return
     */
    public List<String> getOpenRooms() {
        return classStudentService.getOpenRooms();
    }

    /**
     * 获取举手信息  old
     *
     * @param circleId 课堂ID
     * @return
     */
    public AchieveRaise achieveRaise(String circleId, String questId, String questionType, String teacherId) {

        List<Students> students = getRaiseStu(circleId, questId, questionType, teacherId);
        return (students != null && students.size() > 0) ? new AchieveRaise(students) : null;
    }

    /**
     * 获得班级加入的学生ID
     *
     * @param circleId 课堂编号
     * @return
     */
    public List<Students> getRaiseStu(String circleId, String questId, String typeName, String teacherId) {
        //获得随机数状态,页面刷新会改变随机数状态
        String radonTag = stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId, teacherId, AchieveRaiseKey.CLASSROOM_CLEAR_TAG_RAISE));
        //随机数改变，过滤已发送过的学生
        if (radonTag.equals(ClassRoomKey.OPEN_CLASSROOM_RANDOM_TAG_YES)) {
            //清除推送学生数据，改变随机值状态也N
            riseRepeat.clearAnswer(circleId, questId, teacherId);
        }
        //推送学生信息
        return raiseStudents(circleId, questId, typeName);
    }

    /**
     * 获得回答学生的基本对象信息
     *
     * @param circleId
     * @param questId
     * @param questionType
     * @return
     */
    public List<Students> raiseStudents(String circleId, String questId, String questionType) {
        return stringRedisTemplate.opsForSet().members(AchieveRaiseKey.askTypeQuestionsId(questionType, circleId, AchieveRaiseKey.CLASSROOM_CLEAR_TAG_RAISE, questId))
                .stream()
                .filter(id -> riseRepeat.answerHasJoin(circleId, questId, id))
                .map(id -> riseRepeat.joinAnswer(circleId, questId, id))
                .map(stuId -> studentsService.findStudentsBrief(stuId))
                .collect(Collectors.toList());
    }

}