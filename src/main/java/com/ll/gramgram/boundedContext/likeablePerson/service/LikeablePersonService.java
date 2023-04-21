package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        InstaMember fromInstaMember = member.getInstaMember(); //내 인스타
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData(); // 상대방 인스타

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();
        /**
         * 양방향 연관관계를 위한 코드
         */
        // 너가 좋아하는 호감표시 생겼어.
        fromInstaMember.addFromLikeablePerson(likeablePerson);
        // 너를 좋아하는 호감표시 생겼어.
        toInstaMember.addToLikeablePerson(likeablePerson);

        likeablePersonRepository.save(likeablePerson); // 저장


        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }


    public RsData verify(Member member, String username, int attractiveTypeCode) {
        if (member.hasConnectedInstaMember() == false) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember fromInstaMember = member.getInstaMember();
        List<LikeablePerson> MyLikePeople = fromInstaMember.getFromLikeablePeople();
        for (LikeablePerson likeablePerson : MyLikePeople) {
            //기존에 등록되었던 인스타id 이면
            if (username.equals(likeablePerson.getToInstaMemberUsername())) {
                //호감 사유가 동일하면 에러 메세지, 동일하지 않으면 update
                if (attractiveTypeCode == likeablePerson.getAttractiveTypeCode())
                    return RsData.of("F-1", "동일한 사유로 동일한 인물에게 호감을 표할수없습니다.");

                //호감 사유가 다르면 해당 update 를 해야하는 likeablePerson 을 담아서 반환한다.
                return RsData.of("S-1", "update 쿼리문을 날려야합니다.", likeablePerson);
            }
        }

        long likeablePersonFromMax = AppConfig.getLikeablePersonFromMax();
        if (MyLikePeople.size() >= likeablePersonFromMax) {
            return RsData.of("F-1", "호감을 등록할 수 있는 인원("+ likeablePersonFromMax + "명)을 초과하였습니다.");

        }

        return RsData.of("S-1", "검증기를 통과했습니다. 호감등록을 해야합니다.");
    }

    @Transactional //-> update 쿼리가 나가도록 설정한다.
    public RsData update(String username, int attractiveTypeCode, LikeablePerson likeablePerson) {
        String beforeAttractionCode = likeablePerson.getAttractiveTypeDisplayName();
        likeablePerson.updateAttractiveTypeCode(attractiveTypeCode);

        return RsData.of("S-2", "인스타유저(%s)의 호감사유를 '%s' 에서 '%s' 로 변경했습니다.".formatted(
                username, beforeAttractionCode, likeablePerson.getAttractiveTypeDisplayName()));
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    @Transactional
    public RsData delete(LikeablePerson likeablePerson) {
        likeablePersonRepository.delete(likeablePerson );

        return RsData.of("S-1", "인스타유저(%s)를 호감목록에서 제거했습니다.".formatted(likeablePerson.getToInstaMemberUsername()));
    }

    public LikeablePerson findById(Long likeablePersonId) {
        Optional<LikeablePerson> findLikeablePersonOpt = likeablePersonRepository.findById(likeablePersonId);
        if (findLikeablePersonOpt.isPresent())
            return findLikeablePersonOpt.get();
        return null;
    }

    public RsData canActorDelete(Member actor, LikeablePerson likeablePerson) {
        if (likeablePerson == null) {
            return RsData.of("F-1", "존재하지 않는 호감 목록 입니다.");
        }

        // 수행자의 인스타계정 번호
        long actorInstaMemberId = actor.getInstaMember().getId();

        // 삭제 대상의 작성자(호감표시한 사람)의 인스타계정 번호
        long fromInstaMemberId = likeablePerson.getFromInstaMember().getId();

        if (actorInstaMemberId != fromInstaMemberId)
            return RsData.of("F-2", "권한이 없습니다.");

        return RsData.of("S-1", "삭제가능합니다.");
    }
}
