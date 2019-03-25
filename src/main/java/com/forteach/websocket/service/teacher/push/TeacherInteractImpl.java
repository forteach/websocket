package com.forteach.websocket.service.teacher.push;

import com.forteach.websocket.common.BigQueKey;
import com.forteach.websocket.common.ClassRoomKey;
import com.forteach.websocket.common.Dic;
import com.forteach.websocket.domain.AchieveRaise;
import com.forteach.websocket.domain.Students;
import com.forteach.websocket.service.StudentsService;
import com.forteach.websocket.service.teacher.push.repeat.AnswerRepeat;
import com.forteach.websocket.service.teacher.push.repeat.JoinStuRepeat;
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
public class TeacherInteractImpl {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private StudentsService studentsService;

    @Resource
    private JoinStuRepeat joinStuRepeat;

    @Resource
    private AnswerRepeat answerRepeat;

    @Resource
    private RaiseRepeat riseRepeat;

    /**
     * 获得当前课堂提问的题目ID
     * @param circleId
     * @return
     */
    public String getNowQuestionId(String circleId){
        return  hashOperations.get(BigQueKey.QuestionsIdNow(circleId),"questionId");
    }

    /**
     * 获得当前课堂提问的题目ID
     * @param circleId
     * @return
     */
    public String getNoQuestionType(String circleId){
        return  hashOperations.get(BigQueKey.QuestionsIdNow(circleId),"questionType");
    }


    /**
     * 获得班级加入的学生ID
     *
     * @param circleId 课堂编号
     * @param        teacherId 教师编号
     * @return
     */
    public List<String> getInteractiveStudents(final String circleId, final String teacherId) {
        //获得随机数状态,页面刷新会改变随机数状态
        String radonTag=stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId,teacherId));
        //随机数改变，清除已发送学生的缓存信息
        if(radonTag.equals(ClassRoomKey.OPEN_CLASSROOM_Random_TAG_YES)) {
            //清除推送学生数据，改变随机值状态也N，未改变状态
            joinStuRepeat.clearJoinStu(circleId,teacherId);
        }
        //推送过滤已发送过的学生
        return getTuiSongStuId(circleId, teacherId);
    }

    /**
     * 生成推送的学生ID
     * @param circleId
     * @param teacherId
     * @return
     */
    private List<String> getTuiSongStuId(final String circleId, final String teacherId){
        return  stringRedisTemplate.opsForSet()
                .members(ClassRoomKey.getInteractiveIdQra(circleId))
                .stream()
                //需要过滤掉教师ID
                .filter(id -> !id.equals(teacherId))
                .filter(id->joinStuRepeat.hasJoinStu(circleId,id))
                .map(id->joinStuRepeat.joinStu(circleId,id))
                .collect(Collectors.toList());
    }
//
//    //判断该学生是否已经推送过
//    private boolean hasJoin(String circleId, String stuId){
//       return (!stringRedisTemplate.opsForSet().isMember(ClassRoomKey.getJoinTuisongStuKey(circleId),stuId).booleanValue());
//    }
//
//    //将课堂加入的学生登记入已推送列表推送过
//    private String joinStuTuiSong(String circleId, String stuId){
//        stringRedisTemplate.opsForSet().add(ClassRoomKey.getJoinTuisongStuKey(circleId),stuId);
//        return stuId;
//    }
//
//    private void clearJoinStuTuiSong(final String circleId,final String teacherId){
//       if(stringRedisTemplate.delete(ClassRoomKey.getJoinTuisongStuKey(circleId)).booleanValue()){
//           stringRedisTemplate.opsForValue().set(ClassRoomKey.getOpenClassRandomTag(circleId, teacherId), ClassRoomKey.OPEN_CLASSROOM_Random_TAG_NO);
//       }
//    }

    /**
     * 获得班级加入的学生ID
     *
     * @param circleId 课堂编号
     * @param        teacherId 教师编号
     * @return
     */
    public List<String> getAnswerStu(String circleId,String questId,String typeName,String teacherId) {
        //获得随机数状态,页面刷新会改变随机数状态
        String radonTag=stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId,teacherId));
        //随机数改变，过滤已发送过的学生
        if(radonTag.equals(ClassRoomKey.OPEN_CLASSROOM_Random_TAG_YES)) {
            //清除推送学生数据，改变随机值状态也N
            answerRepeat.clearAnswer(circleId,teacherId);
        }
        //推送学生信息
        return getAnswerStudent(circleId, questId,typeName);
    }

    /**
     * 获取问题已回答的学生id
     * @param circleId
     * @param questId
     * @param typeName
     * @return
     */
    public List<String> getAnswerStudent(String circleId,String questId,String typeName) {
        //获得学生回答顺序列表
        return stringRedisTemplate.opsForList().range(BigQueKey.answerTypeQuestStuList(circleId, questId, typeName),0,-1)
                .stream()
                .filter(id->answerRepeat.answerHasJoin(circleId,id))
                .map(id->answerRepeat.joinAnswer(circleId,id))
                .collect(Collectors.toList());
    }

    /**
     * 获得学生题目回答的答案
     * @param circleId
     * @param questId
     * @param typeName
     * @param examineeId
     * @return
     */
    public String getQuestAnswer(String circleId,String questId,String typeName,String examineeId){
       return  hashOperations.get(BigQueKey.answerTypeQuestionsId(circleId,questId,typeName),examineeId);
    }

    /**
     * 获得自动批改结果
     * @param circleId
     * @param questId
     * @param typeName
     * @param examineeId
     * @return
     */
    public String piGaiResult(String circleId,String questId,String typeName,String examineeId){
        return  hashOperations.get(BigQueKey.piGaiTypeQuestionsId(circleId,questId,typeName),examineeId);
    }

    /**
     * 查看回答标志
     *
     * @param askKey
     * @return
     */
    private String findAnswerFlag(final String askKey) {
        return hashOperations.get(askKey, "answerFlag");
    }

    /**
     * 获得当前开课课堂教师的编号
     * @param circleId
     * @return
     */
    public String getRoomTeacherId(String circleId) {
        return stringRedisTemplate.opsForValue().get(ClassRoomKey.getRoomTeacherKey(circleId));
    }

    /**
     * 获得当前开课课堂列表
     * @return
     */
    public List<String> getOpenRooms() {
        return stringRedisTemplate.opsForSet().members(ClassRoomKey.OPEN_CLASSROOM)
                .stream().collect(Collectors.toList());
    }

    /**
     * 获取举手信息  old
     *
     * @param circleId 课堂ID
     * @return
     */
    public AchieveRaise achieveRaise(String circleId,String questId,String  questionType,String teacherId) {

       List<Students> students= getRaiseStu( circleId, questId, questionType,teacherId);
        return new AchieveRaise(students);
    }

    /**
     * 获得班级加入的学生ID
     *
     * @param circleId 课堂编号
     * @return
     */
    public List<Students> getRaiseStu(String circleId,String questId,String typeName,String teacherId) {
        //获得随机数状态,页面刷新会改变随机数状态
        String radonTag=stringRedisTemplate.opsForValue().get(ClassRoomKey.getOpenClassRandomTag(circleId,teacherId));
        //随机数改变，过滤已发送过的学生
        if(radonTag.equals(ClassRoomKey.OPEN_CLASSROOM_Random_TAG_YES)) {
            //清除推送学生数据，改变随机值状态也N
            riseRepeat.clearAnswer(circleId,teacherId);
        }
        //推送学生信息
        return raiseStudents(circleId, questId,typeName);
    }

    //获得回答学生的基本对象信息
    public List<Students> raiseStudents(String circleId,String questId,String questionType){
        return stringRedisTemplate.opsForSet().members(BigQueKey.askTypeQuestionsId(questionType,circleId, Dic.ASK_INTERACTIVE_RAISE,questId))
                .stream()
                .filter(id->riseRepeat.answerHasJoin(circleId,id))
                .map(id->riseRepeat.joinAnswer(circleId,id))
                .map(stuId ->studentsService.findStudentsBrief(stuId))
                .collect(Collectors.toList());
    }

}
