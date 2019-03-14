package com.forteach.websocket.service.student.push;

import com.forteach.websocket.common.Dic;
import com.forteach.websocket.common.QuestionType;
import com.forteach.websocket.domain.AskQuestion;
import com.forteach.websocket.domain.BigQuestion;
import com.forteach.websocket.domain.OptQuestion;
import com.forteach.websocket.domain.ToStudentPush;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import static com.forteach.websocket.common.Dic.*;
import static com.forteach.websocket.service.WsService.SESSION_MAP;

/**
 * @Description:推送给学生
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class TiWenPush {

    //学生交互操作类
    @Resource
    private StuInteractImpl stuInteract;

    /**
     *学生提问推送信息
     * @param circleId
     * @return
     */
    public List<ToStudentPush> tiWenStudent(final String circleId){

        //获得提问方式的题目编号
        final String questId=stuInteract.getNowQuestId(QuestionType.TiWen,circleId, Dic.ASK_INTERACTIVE_SELECT);
        //获得当前题目选中的学生
        final String stus= stuInteract.getQuestSelectStu(circleId);

        //获得当前题目的交互方式
        //交互方式  选人、举手、抢答
        final String interactive=stuInteract.getNowQuestInteractive(circleId);
        //暂时设定，需要从redis里面去除该值
        //小组 个人
        final String category=stuInteract.getNowQuestCategory(circleId);

        //根据所选的学生，对比Session数据是否在线，并获得学生推送详情
        return Arrays.asList(stus.split(",")).stream()
                .filter(id -> null != SESSION_MAP.get(id))
                .filter(id -> SESSION_MAP.get(id).isOpen())
                //创建推送数据
                .map(uid->TStudentToPush(uid,questId, interactive, category))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 推送学生数据对象构造
     ** @param uid 学生编号
     * @param questid 题目编号
     * @param interactive  交互方式  选人、举手、抢答
     * @param category  小组 个人
     * @return
     */
    private ToStudentPush TStudentToPush(String uid,String questid,String interactive,String category) {
                //是学生推送学生信息
                return ToStudentPush.builder()
                        .uid(uid)
                        //提问问题
                        .askQuestion(achieveQuestion(questid,interactive,category))
                        .build();
    }

    /**
     *
    // * @param circleid  课堂编号
     * @param interactive     题目的回答方式
     * @param questid   提问的问题编号
     * @param category   个人、小组回答
     * @return  返回题目信息
     */
    public AskQuestion achieveQuestion(String questid,String interactive,String category){
        //前端发送过来的时间阀值
        String cut="";
        //创建题目信息
        OptQuestion optQuestion = getQuestion(category, questid,  interactive);
        //返回提问信息
        return buildAskQuestion(cut, optQuestion, interactive, category);
    }


    /**
     * 获取uid的问题
     *
     * @param interactive
     * @return
     */
    private OptQuestion getQuestion(String category,String questionId, String interactive) {
        switch (category) {
            case CATEGORY_PEOPLE:
                //个人回答
                return askPeople(questionId, interactive);
            case CATEGORY_TEAM:
                //组回答
                //askTeam(askKey, uid, interactive);
                return null;
            default:
                log.error("获取 getQuestion 信息错误 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 个人对象 返回题目
     *
     * @param questionId
     * @param interactive
     * @return
     */
    private OptQuestion askPeople(String questionId, String interactive) {
        switch (interactive) {
            case ASK_INTERACTIVE_RACE:
                return selected(stuInteract.getBigQuestion(questionId));
            case ASK_INTERACTIVE_RAISE:
                //raiseSelected(askKey, uid, findBigQuestion(askKey));
                return null;
            case ASK_INTERACTIVE_SELECT:
                return selected(stuInteract.getBigQuestion(questionId));
            case ASK_INTERACTIVE_VOTE:
                return null;
            default:
                log.error(" askPeople 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 选人方式题目推送
     *
     * @param bigQuestion
     * @return
     */
    private OptQuestion selected(BigQuestion bigQuestion) {
        return new OptQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
    }



        /**
     * 构建提问问题返回值
     *
     * @param cut
     * @param optQuestion
     * @param interactive
     * @return
     */
    private AskQuestion buildAskQuestion(String cut, OptQuestion optQuestion, String interactive, String category) {
        if (optQuestion != null) {
            return new AskQuestion<>(cut, optQuestion, interactive, category);
        } else {
            return null;
        }
    }
}
