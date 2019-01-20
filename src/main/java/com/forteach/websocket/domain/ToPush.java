package com.forteach.websocket.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @author: liu zhenming
 * @version: V1.0
 * @date: 2018/12/28  15:09
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToPush {

    private String uid;

    private AskQuestion askQuestion;

    private AchieveJoin achieveJoin;

    private AchieveRaise achieveRaise;

    private AchieveAnswer achieveAnswer;

    private AchieveSurveyAnswer achieveSurveyAnswer;

    private AchieveBrainstormAnswer achieveBrainstormAnswer;

    private AchieveTaskAnswer achieveTaskAnswer;

    private AskSurvey askSurvey;

    private AskTask askTask;

    private AskBook askBook;

    private AskBrainstorm askBrainstorm;

    private AchieveBookAnswer achieveBookAnswer;



}
