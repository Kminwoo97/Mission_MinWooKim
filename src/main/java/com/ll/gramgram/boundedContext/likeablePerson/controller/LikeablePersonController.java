package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.dto.LikeForm;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like")
    public String showLike() {
        return "usr/likeablePerson/like";
    }



    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like")
    public String like(@Valid LikeForm likeForm) {
        RsData verifyRsData = likeablePersonService.canAdd(rq.getMember(), likeForm.getUsername(), likeForm.getAttractiveTypeCode());
        //검증 실패
        if(verifyRsData.isFail())
            return rq.historyBack(verifyRsData);

        //검증 성공 - update
        if(verifyRsData.getData() != null) {
            RsData<LikeablePerson> updateRsData = likeablePersonService.update(likeForm.getUsername(),
                    likeForm.getAttractiveTypeCode(), (LikeablePerson) verifyRsData.getData());
            return rq.redirectWithMsg("/likeablePerson/list", updateRsData);
        }

        //검증 성공 - like
        RsData<LikeablePerson> createRsData = likeablePersonService.like(rq.getMember(), likeForm.getUsername(), likeForm.getAttractiveTypeCode());
        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", createRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            List<LikeablePerson> likeablePeople = likeablePersonService.findByFromInstaMemberId(instaMember.getId());
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }

    //호감 목록 삭제
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/cancel/{id}")
    public String cancel(@PathVariable("id") Long likeablePersonId){
        //삭제하려는 호감이 존재하는지 꺼내와본다.
        LikeablePerson likeablePerson = likeablePersonService.findById(likeablePersonId);

        //로그인 한 사용자가 삭제할 수 있는지 체크한다.
        RsData canActorDeleteRsData = likeablePersonService.canActorDelete(rq.getMember(), likeablePerson);
        if (canActorDeleteRsData.isFail()) {
            return rq.historyBack(canActorDeleteRsData);
        }

        RsData deleteRsData = likeablePersonService.delete(likeablePerson);
        return rq.redirectWithMsg("/likeablePerson/list", deleteRsData.getMsg());
    }
}
