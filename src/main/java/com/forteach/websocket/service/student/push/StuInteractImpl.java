package com.forteach.websocket.service.student.push;
import com.forteach.websocket.common.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;


/**
 * @Description: 学生推送业务数据处理
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/2  15:12
 */
@Slf4j
@Service
public class StuInteractImpl {

    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     *获得当前课堂活动的题目ID
     * @param type  课堂活动  提问 任务
     * @param circleId
     * @param Interact 课堂互动方式  选人 抢答
     * @return
     */
    public String getNowQuestId(QuestionType type,String circleId,String Interact) {
        String key= BigQueKey.askTypeQuestionsIdNow(type, circleId, Interact);
        return stringRedisTemplate.opsForValue().get(key);
    }

    //获得当前题目的交互类型
    public String getNowQuestInteractive(String circleId) {
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "interactive");
    }

    //获得当前题目的参与形式
    public String getNowQuestCategory(String circleId) {
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "category");
    }

        //获得当前开课课堂列表
    public String getQuestSelectStu(String circleId){
        return hashOperations.get(BigQueKey.QuestionsIdNow(circleId), "selected");
    }
//********************************************************************
//    /**
//     * 查看回答标志
//     *
//     * @param askKey
//     * @return
//     */
//    private String findAnswerFlag(final String askKey) {
//        return hashOperations.get(askKey, "answerFlag");
//    }
//
//    //获得当前开课课堂教师的编号
//    public String getRoomTeacherId(String circleId) {
//        return stringRedisTemplate.opsForValue().get(ClassRoomKey.getRoomTeacherKey(circleId));
//    }
//
//    //获得当前开课课堂列表
//    public List<String> getOpenRooms() {
//        return stringRedisTemplate.opsForSet().members(ClassRoomKey.OPEN_CLASSROOM)
//                .stream().collect(Collectors.toList());
//    }
//
//    /**
//     * 获取选择信息
//     *
//     * @param askKey
//     * @return
//     */
//    private String askSelected(final String askKey) {
//        return hashOperations.get(askKey, "selected");
//    }
//
//    /**
//     * 获取uid当前的课堂
//     *
//     * @param uid
//     * @return
//     */
//    public String uidCircle(String uid) {
//        return hashOperations.get(actionPropertyKey(uid), "circle");
//    }
//
//    /**
//     * 获取课堂提问的切换值
//     *
//     * @return
//     */
//    public String askQuestionCut(final String askKey) {
//        return hashOperations.get(askKey, "cut");
//    }
//
//    /**
//     * 判断学生是否被选中
//     *
//     * @return
//     */
//    private Boolean isSelected(final String selectId, final String examineeId) {
//        return Arrays.asList(selectId.split(",")).contains(examineeId);
//    }
//
//
//    /**
//     * 判断学生是否被选中
//     *
//     * @return
//     */
//    private Boolean isSelectedTeam(final String selectId, final String examineeId, final String askKey) {
//
//        String circle = uidCircle(examineeId);
//        //小组id
//        List<String> ids = Arrays.asList(selectId.split(","));
//
//        //获取课堂当前的team
//        List<Team> teams = JSON.parseObject(JSON.toJSONString(redisTemplate.opsForValue().get(groupKey(circle))), new TypeReference<List<Team>>() {
//        });
//
//        boolean flag = false;
//
//        for (Team team : teams) {
//            if (ids.contains(team.getTeamId())) {
//                List<String> students = team.getStudents().stream().map(Students::getId).collect(Collectors.toList());
//                if (students.contains(examineeId)) {
//                    flag = true;
//                }
//            } else {
//                flag = false;
//            }
//        }
//        return flag;
//    }
//

//***********************************************************************
//    /**
//     * 获得班级加入的学生
//     *
//     * @param
//     * @return
//     */
//    public List<String> getInteractiveStudents(final String circleId, final String teacherId) {
//        return  stringRedisTemplate.opsForSet().members(ClassRoomKey.getInteractiveIdQra(circleId))
//                .stream().filter(id -> !id.equals(teacherId))//需要过滤掉教师ID
//                .collect(Collectors.toList());
//    }
//
//
//
//    /**
//     * 查询没有题的情况
//     *
//     * @param askKey
//     * @return
//     */
//    public boolean untitled(final String askKey) {
//        return hashOperations.hasKey(askKey, "questionId");
//    }
//
//
//
//    public boolean isMember(final String redisKey, final String examineeId) {
//        return stringRedisTemplate.opsForSet().isMember(redisKey, examineeId);
//    }
//
//    /**
//     * 获取回答的学生id
//     *
//     * @param askKey
//     * @return
//     */
//    public List<String> getAnswerStudent(String askKey) {
//        return Arrays.asList(askSelected(askKey).split(","));
//    }
//
//    /**
//     * 获取uid的身份 老师 学生...
//     *
//     * @param uid
//     * @return
//     */
//    public String uidType(String uid) {
//        return hashOperations.get(actionPropertyKey(uid), "type");
//    }
//
//
//
//    /**
//     * 获取uid当前的去重随机数
//     *
//     * @param uid
//     * @return
//     */
//    public String uidRandom(final String uid) {
//        return hashOperations.get(actionPropertyKey(uid), "random");
//    }
//
//    /**
//     * 获取提问类型
//     *
//     * @param askKey
//     * @return
//     */
//    public String askCategoryType(final String askKey) {
//        return hashOperations.get(askKey, "category");
//    }
//
//    /**
//     * 获取提问交互类型
//     *
//     * @param askKey
//     * @return
//     */
//    public String askInteractiveType(final String askKey) {
//        return hashOperations.get(askKey, "interactive");
//    }
//
//    /**
//     * 获取问题id
//     *
//     * @param askKey
//     * @return
//     */
//    public String askQuestionId(final String askKey) {
//        return hashOperations.get(askKey, "questionId");
//    }
//
//
//
//    /**
//     * 通过 提问key,判断是否是选择
//     *
//     * @param askKey
//     * @return
//     */
//    public Boolean selectVerify(final String askKey, final String examineeId) {
//        return isSelected(Objects.requireNonNull(hashOperations.get(askKey, "selected")), examineeId);
//    }
//
//    /**
//     * 通过 提问key,判断是否是选择
//     *
//     * @param askKey
//     * @return
//     */
//    public Boolean selectTeamVerify(final String askKey, final String examineeId) {
//        return isSelectedTeam(Objects.requireNonNull(hashOperations.get(askKey, "selected")), examineeId, askKey);
//    }
//
//
//    /**
//     * 判断是否已经推送过该题
//     * 如果没有拉取过 给予正确 存入课堂题目的cut
//     * 如果一致 代表已经拉取过 不再给予
//     * 如果不一致 代表同题但是不同提问方式 重新发送
//     *
//     * @param distinctKey
//     * @param askKey
//     * @param selected
//     * @return true 没有推送过该题   false  有推送过该题
//     */
//    public boolean distinctKeyIsEmpty(final String distinctKey, final String askKey, final String selected) {
//        if (log.isDebugEnabled()) {
////            log.debug("判断是否已经推送过该题 参数 ==> distinctKey : {}, askKey : {}, selected : {}", distinctKey, askKey, selected);
//        }
//        String distinct = stringRedisTemplate.opsForValue().get(distinctKey);
//        String cut = askQuestionCut(askKey);
//        stringRedisTemplate.opsForValue().set(distinctKey, cut.concat(selected), Duration.ofSeconds(60 * 60 * 2));
//        if (distinct == null) {
//            return true;
//        } else if (Objects.equals(distinct, cut.concat(selected))) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    /**
//     * 获取回答去重
//     *
//     * @param distinctKey 去重key
//     * @param setKey
//     * @param askKey
//     * @return
//     */
//    public boolean answerDistinct(final String distinctKey, final String setKey, final String askKey) {
//        if (log.isDebugEnabled()) {
////            log.debug("获取回答去重 参数 ==> distinctKey : {}, setKey : {}, askKey : {}", distinctKey, setKey, askKey);
//        }
//        String distinct = stringRedisTemplate.opsForValue().get(distinctKey);
//        String findAnswerFlag = findAnswerFlag(askKey);
//        Long answSize = stringRedisTemplate.opsForSet().size(setKey);
//
//        if (answSize == null) {
//            return false;
//        }
//
//        if (String.valueOf(answSize.intValue()).equals(distinct) && String.valueOf(answSize.intValue()).equals(findAnswerFlag)) {
//            //如果等于 排除
//            return false;
//        }
//
//        stringRedisTemplate.opsForValue().set(distinctKey, String.valueOf(answSize.intValue()), Duration.ofSeconds(60 * 60 * 2));
//        hashOperations.put(askKey, "answerFlag", String.valueOf(answSize.intValue()));
//        return true;
//    }
//
//    /**
//     * 获取举手去重
//     *
//     * @param distinctKey
//     * @param askKey
//     * @param size
//     * @return
//     */
//    public boolean raiseDistinct(final String distinctKey, final String askKey, int size) {
//        if (log.isDebugEnabled()) {
////            log.debug("获取举手去重 参数 ==> distinctKey : {}, askKey : {}, size : {}", distinctKey, askKey, size);
//        }
//        String distinct = stringRedisTemplate.opsForValue().get(distinctKey);
//        String cut = askQuestionCut(askKey);
//        stringRedisTemplate.opsForValue().set(distinctKey, String.valueOf(size).concat(cut), Duration.ofSeconds(60 * 60 * 2));
//        if (distinct == null) {
//            return true;
//        } else if (distinct.equals(String.valueOf(size).concat(cut))) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    /**
//     * 学生加入信息去重
//     *
//     * @param distinctKey
//     * @param askKey
//     * @param size
//     * @return
//     */
//    public boolean joinDistinct(final String distinctKey, final String askKey, int size) {
//        if (log.isDebugEnabled()) {
////            log.debug("学生加入信息去重 参数 ==> distinctKey : {}, askKey : {}, size : {}", distinctKey, askKey, size);
//        }
//        String distinct = stringRedisTemplate.opsForValue().get(distinctKey);
//        stringRedisTemplate.opsForValue().set(distinctKey, String.valueOf(size), Duration.ofSeconds(60 * 60 * 2));
//        if (distinct == null) {
//            return true;
//        } else if (distinct.equals(String.valueOf(size))) {
//            return false;
//        } else {
//            return true;
//        }
//    }
//
//    public Set<String> getSets(String key) {
//        return stringRedisTemplate.opsForSet().members(key);
//    }
//

}
