package com.forteach.websocket.service.impl;

import com.forteach.websocket.domain.*;
import com.forteach.websocket.repository.BigQuestionRepository;
import com.forteach.websocket.service.InteractService;
import com.forteach.websocket.service.StudentsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import static com.forteach.websocket.common.Dic.*;
import static com.forteach.websocket.common.KeyStorage.*;
import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description: 互动交互数据获取
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  9:38
 */
@Slf4j
@Service
public class InteractServiceImpl implements InteractService {


    @Resource
    private HashOperations<String, String, String> hashOperations;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private BigQuestionRepository bigQuestionRepository;

    @Resource
    private MongoTemplate mongoTemplate;

    @Resource
    private StudentsService studentsService;

    /**
     * 获取课堂交互信息
     *
     * @return
     */
    @Override
    public List<ToPush> obtain() {

        Set<String> uid = getSets(INTERACTION_UID_SET_PREFIX);
        if (uid != null && uid.size() > 0) {
            return uid.stream()
                    .filter(id -> null != SESSION_MAP.get(id))
                    .filter(id -> SESSION_MAP.get(id).isOpen() != false)
                    .map(this::buildToPush)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * 构建需要推送的信息
     *
     * @param uid
     * @return
     */
    private ToPush buildToPush(String uid) {
        String uType = uidType(uid);
        if (SUBSCRIBE_USER_STUDENT.equals(uType)) {
            return ToPush.builder().uid(uid).askQuestion(achieveQuestion(uid)).build();
        } else {
            return ToPush.builder()
                    .uid(uid)
                    .achieveAnswer(achieveAnswer(uid))
                    .achieveRaise(achieveRaise(uid))
                    .achieveJoin(achieveInteractiveStudents(uid))
                    .build();
        }
    }

    /**
     * 获取需要推送的获取问题
     *
     * @param uid
     * @return
     */
    private AskQuestion achieveQuestion(String uid) {

        String uCircle = uidCircle(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(uCircle);
        String questionId = askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        String uRandom = uidRandom(uid);
        String uDistinctKey = askQuDistinctKey(uCircle, uid, questionId, uRandom);
        String cut = askQuestionCut(askKey);
        String interactive = askCategoryType(askKey);

        OptBigQuestion optBigQuestion = getQuestion(askKey, uid, interactive);
        if (optBigQuestion != null && distinctKeyIsEmpty(uDistinctKey, askKey, optBigQuestion.getSelected())) {
            return buildAskQuestion(cut, optBigQuestion, interactive);
        } else {
            return null;
        }
    }

    /**
     * 获取举手信息
     *
     * @param uid
     * @return
     */
    private AchieveRaise achieveRaise(String uid) {

        String uCircle = uidCircle(uid);
        String uRandom = uidRandom(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(uCircle);

        List<Students> uids = interactStudents(uCircle);
        if (uids.size() > 0 && raiseDistinct(raiseDistinctKey(uCircle, uRandom), askKey, uids.size())) {
            return buildAchieveRaise(uids);
        } else {
            return null;
        }
    }

    private AchieveJoin achieveInteractiveStudents(String uid) {

        String uCircle = uidCircle(uid);
        String uRandom = uidRandom(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(uCircle);

        List<Students> uids = findInteractiveStudents(uCircle);
        if (uids.size() > 0 && joinDistinct(joinDistinctKey(uCircle, uRandom), askKey, uids.size())) {
            return buildAchieveJoin(uids);
        } else {
            return null;
        }

    }


    /**
     * 主动推送给教师 当前问题的回答情况
     *
     * @return
     */
    private AchieveAnswer achieveAnswer(String uid) {

        String uCircle = uidCircle(uid);
        String uRandom = uidRandom(uid);
        String askKey = CLASSROOM_ASK_QUESTIONS_ID.concat(uCircle);
        String questionId = askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        if (!untitled(askKey)) {
            return null;
        }

        if (answerDistinct(getAnswDistinctKey(uCircle, uRandom), examineeIsReplyKey(uCircle), askKey)) {
            List<Students> students = peopleAnswer(uCircle, questionId, askKey);
            return buildAchieveAnswer(students);
        } else {
            return null;
        }

    }

    private List<Students> peopleAnswer(String uCircle, String questionId, String askKey) {
        return getAnswerStudent(askKey).stream().map(id -> {
            boolean flag = isMember(examineeIsReplyKey(uCircle), id);
            AskAnswer answ = findAskAnswer(uCircle, id, questionId);
            Students student = studentsService.findStudentsBrief(id);
            if (flag) {
                return new CircleAnswer(student, ASK_CIRCLE_ANSWER_DID, answ);
            } else {
                return new CircleAnswer(student, ASK_CIRCLE_ANSWER_ALREADY, new AskAnswer());
            }
        }).collect(Collectors.toList());
    }

    /**
     * 获取回答的学生id
     *
     * @param askKey
     * @return
     */
    private List<String> getAnswerStudent(String askKey) {
        return Arrays.asList(askSelected(askKey).split(","));
    }


    /**
     * 获取uid的问题
     *
     * @param askKey
     * @param uid
     * @param interactive
     * @return
     */
    private OptBigQuestion getQuestion(String askKey, String uid, String interactive) {
        switch (interactive) {
            case CATEGORY_PEOPLE:
                return askPeople(askKey, uid);
            case CATEGORY_TEAM:
                return null;
            default:
                log.error("获取 achieveQuestion 信息错误 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 个人对象 返回题目
     *
     * @param uid
     * @return
     */
    private OptBigQuestion askPeople(String askKey, String uid) {
        switch (askInteractiveType(askKey)) {
            case ASK_INTERACTIVE_RACE:
                return selected(selectQuestion(askKey, uid));
            case ASK_INTERACTIVE_RAISE:
                return raiseSelected(askKey, uid, findBigQuestion(askKey));
            case ASK_INTERACTIVE_SELECT:
                return selected(selectQuestion(askKey, uid));
            case ASK_INTERACTIVE_VOTE:
                return null;
            default:
                log.error(" askPeople 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 被选中的学生获得问题
     *
     * @param askKey
     * @param uid
     * @return
     */
    private BigQuestion selectQuestion(String askKey, String uid) {
        if (selectVerify(askKey, uid)) {
            return findBigQuestion(askKey);
        } else {
            return null;
        }
    }

    private OptBigQuestion selected(BigQuestion bigQuestion) {
        return new OptBigQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
    }

    private OptBigQuestion raiseSelected(String askKey, String uid, BigQuestion bigQuestion) {
        if (selectVerify(askKey, uid)) {
            return new OptBigQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
        } else {
            return new OptBigQuestion(ASK_QUESTIONS_UN_SELECTED, bigQuestion);
        }
    }

    private AskQuestion buildAskQuestion(String cut, OptBigQuestion optBigQuestion, String interactive) {
        if (optBigQuestion != null) {
            return new AskQuestion<BigQuestion>(cut, optBigQuestion, interactive);
        } else {
            return null;
        }
    }

    private AchieveRaise buildAchieveRaise(List<Students> students) {
        return new AchieveRaise(students);
    }

    private AchieveJoin buildAchieveJoin(List<Students> students) {
        return new AchieveJoin(students);
    }

    private AchieveAnswer buildAchieveAnswer(List<Students> students) {
        return new AchieveAnswer(students);
    }

    /**
     * 获取uid的身份 老师 学生...
     *
     * @param uid
     * @return
     */
    private String uidType(String uid) {
        return hashOperations.get(actionPropertyKey(uid), "type");
    }

    /**
     * 获取uid当前的课堂
     *
     * @param uid
     * @return
     */
    private String uidCircle(String uid) {
        return hashOperations.get(actionPropertyKey(uid), "circle");
    }

    /**
     * 获取uid当前的去重随机数
     *
     * @param uid
     * @return
     */
    private String uidRandom(final String uid) {
        return hashOperations.get(actionPropertyKey(uid), "random");
    }

    /**
     * 获取提问类型
     *
     * @param askKey
     * @return
     */
    private String askCategoryType(final String askKey) {
        return hashOperations.get(askKey, "category");
    }


    /**
     * 获取提问交互类型
     *
     * @param askKey
     * @return
     */
    private String askInteractiveType(final String askKey) {
        return hashOperations.get(askKey, "interactive");
    }

    /**
     * 获取问题id
     *
     * @param askKey
     * @return
     */
    private String askQuestionId(final String askKey) {
        return hashOperations.get(askKey, "questionId");
    }

    /**
     * 获取课堂提问的切换值
     *
     * @return
     */
    private String askQuestionCut(final String askKey) {
        return hashOperations.get(askKey, "cut");
    }

    /**
     * 通过 提问key,判断是否是选择
     *
     * @param askKey
     * @return
     */
    private Boolean selectVerify(final String askKey, final String examineeId) {
        return isSelected(Objects.requireNonNull(hashOperations.get(askKey, "selected")), examineeId);
    }

    /**
     * 判断学生是否被选中
     *
     * @return
     */
    private Boolean isSelected(final String selectId, final String examineeId) {
        return Arrays.asList(selectId.split(",")).contains(examineeId);
    }

    /**
     * 获得当前课堂的问题
     *
     * @param askKey
     * @return
     */
    private BigQuestion findBigQuestion(final String askKey) {

        String questionId = askQuestionId(askKey);
        if (questionId == null) {
            return null;
        }
        return bigQuestionRepository.findById(questionId).get();
    }

    /**
     * 判断是否已经推送过该题
     * 如果没有拉取过 给予正确 存入课堂题目的cut
     * 如果一致 代表已经拉取过 不再给予
     * 如果不一致 代表同题但是不同提问方式 重新发送
     *
     * @return true 没有推送过该题   false  有推送过该题
     */
    private boolean distinctKeyIsEmpty(final String distinctKey, final String askKey, final String selected) {

        String distinct = stringRedisTemplate.opsForValue().get(distinctKey);
        String cut = askQuestionCut(askKey);
        stringRedisTemplate.opsForValue().set(distinctKey, cut.concat(selected), Duration.ofSeconds(60 * 60 * 2));
        if (distinct == null) {
            return true;
        } else if (Objects.equals(distinct, cut.concat(selected))) {
            return false;
        } else {
            return true;
        }
    }

    private boolean answerDistinct(final String distinctKey, final String setKey, final String askKey) {
        String distinct = stringRedisTemplate.opsForValue().get(distinctKey);
        String findAnswerFlag = findAnswerFlag(askKey);
        Long answSize = stringRedisTemplate.opsForSet().size(setKey);

        if (answSize == null) {
            return false;
        }

        if (String.valueOf(answSize.intValue()).equals(distinct) && String.valueOf(answSize.intValue()).equals(findAnswerFlag)) {
            //如果等于 排除
            return false;
        }

        stringRedisTemplate.opsForValue().set(distinctKey, String.valueOf(answSize.intValue()), Duration.ofSeconds(60 * 60 * 2));
        hashOperations.put(askKey, "answerFlag", String.valueOf(answSize.intValue()));
        return true;
    }

    private boolean raiseDistinct(final String distinctKey, final String askKey, int size) {

        String distinct = stringRedisTemplate.opsForValue().get(distinctKey);
        String cut = askQuestionCut(askKey);
        stringRedisTemplate.opsForValue().set(distinctKey, String.valueOf(size).concat(cut), Duration.ofSeconds(60 * 60 * 2));
        if (distinct == null) {
            return true;
        } else if (distinct.equals(String.valueOf(size).concat(cut))) {
            return false;
        } else {
            return true;
        }
    }

    private boolean joinDistinct(final String distinctKey, final String askKey, int size) {

        String distinct = stringRedisTemplate.opsForValue().get(distinctKey);
        stringRedisTemplate.opsForValue().set(distinctKey, String.valueOf(size), Duration.ofSeconds(60 * 60 * 2));
        if (distinct == null) {
            return true;
        } else if (distinct.equals(String.valueOf(size))) {
            return false;
        } else {
            return true;
        }
    }


    private Set<String> getSets(String key) {
        return stringRedisTemplate.opsForSet().members(key);
    }

    /**
     * 获取互动的课堂学生信息
     *
     * @param uCircle
     * @return
     */
    private List<Students> interactStudents(String uCircle) {
        return getSets(raiseKey(uCircle))
                .stream()
                .map(studentsService::findStudentsBrief)
                .collect(Collectors.toList());
    }

    private List<Students> findInteractiveStudents(String uCircle) {

        return getSets(interactiveClassKey(uCircle))
                .stream()
                .map(id -> studentsService.findStudentsBrief(id))
                .collect(Collectors.toList());
    }

    /**
     * 查询没有题的情况
     *
     * @param askKey
     * @return
     */
    private boolean untitled(final String askKey) {
        return hashOperations.hasKey(askKey, "questionId");
    }

    /**
     * 获取选择信息
     *
     * @param askKey
     * @return
     */
    private String askSelected(final String askKey) {
        return hashOperations.get(askKey, "selected");
    }

    private boolean isMember(final String redisKey, final String examineeId) {
        return stringRedisTemplate.opsForSet().isMember(redisKey, examineeId);
    }

    private AskAnswer findAskAnswer(final String circleId, final String examineeId, final String questionId) {

        Query query = Query.query(
                Criteria.where("circleId").in(circleId)
                        .and("questionId").in(questionId)
                        .and("examineeId").in(examineeId));

        return mongoTemplate.findOne(query, AskAnswer.class);
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
}
