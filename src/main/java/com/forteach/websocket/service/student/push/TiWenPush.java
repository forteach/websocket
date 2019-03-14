package com.forteach.websocket.service.student.push;

import com.forteach.websocket.domain.*;
import com.forteach.websocket.repository.BigQuestionRepository;
import com.forteach.websocket.repository.BrainstormQuestionRepository;
import com.forteach.websocket.repository.SurveyQuestionRepository;
import com.forteach.websocket.repository.TaskQuestionRepository;
import com.forteach.websocket.service.RedisInteract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import static com.forteach.websocket.common.Dic.*;

/**
 * @Description:推送给学生
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2019/1/20  14:29
 */
@Slf4j
@Component
public class TiWenPush {

    @Resource
    private BigQuestionRepository bigQuestionRepository;

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
                return selected(findBigQuestion(questionId));
            case ASK_INTERACTIVE_RAISE:
                //raiseSelected(askKey, uid, findBigQuestion(askKey));
                return null;
            case ASK_INTERACTIVE_SELECT:
                return selected(findBigQuestion(questionId));
            case ASK_INTERACTIVE_VOTE:
                return null;
            default:
                log.error(" askPeople 非法参数 错误的数据类型");
                return null;
        }
    }

    /**
     * 封装是否能够回答
     *
     * @param bigQuestion
     * @return
     */
    private OptQuestion selected(BigQuestion bigQuestion) {
        return new OptQuestion(ASK_QUESTIONS_SELECTED, bigQuestion);
    }

    /**
     * 获得当前课堂的问题
     *
     * @param questionId 题目Id
     * @return
     */
    private BigQuestion findBigQuestion(final String questionId) {

        return bigQuestionRepository.findById(questionId).get();
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
