package com.ll.gramgram.boundedContext.notification.eventListener;

import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.base.event.EventAfterModifyAttractiveType;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotificationEventListener {
    private final NotificationService notificationService;

    //호감표기 이벤트 발생 시, 호감등록 알림 생성
    @EventListener
    public void listen(EventAfterLike event){
        LikeablePerson likeablePerson = event.getLikeablePerson();

        notificationService.makeLike(likeablePerson);
    }

    //호감사유 변경 이벤트 발생 시, 호감사유 변경 알림 생성
    @EventListener
    public void listen(EventAfterModifyAttractiveType event){
        LikeablePerson likeablePerson = event.getLikeablePerson();

        notificationService.makeModifyAttractive(likeablePerson, event.getOldAttractiveTypeCode());
    }
}
