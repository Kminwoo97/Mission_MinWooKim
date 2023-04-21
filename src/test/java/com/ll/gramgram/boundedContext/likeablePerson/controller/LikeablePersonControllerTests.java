package com.ll.gramgram.boundedContext.likeablePerson.controller;


import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LikeablePersonControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private LikeablePersonService likeablePersonService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("등록 폼(인스타 인증을 안해서 폼 대신 메세지)")
    @WithUserDetails("user1")
    void t001() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        먼저 본인의 인스타그램 아이디를 입력해주세요.
                        """.stripIndent().trim())))
        ;
    }

    @Test
    @DisplayName("등록 폼")
    @WithUserDetails("user2")
    void t002() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/like"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showLike"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <input type="text" name="username"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="1"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="2"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="radio" name="attractiveTypeCode" value="3"
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <input type="submit" value="추가"
                        """.stripIndent().trim())));
        ;
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 user3에게 호감표시(외모))")
    @WithUserDetails("user2")
    void t003() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "insta_user3")
                        .param("attractiveTypeCode", "1")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());
        ;
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 abcd에게 호감표시(외모), abcd는 아직 우리 서비스에 가입하지 않은상태)")
    @WithUserDetails("user2")
    void t004() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "abcd")
                        .param("attractiveTypeCode", "2")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());
        ;
    }

    @Test
    @DisplayName("호감목록")
    @WithUserDetails("user3")
    void t005() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/list"))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showList"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_username">insta_user4</span>
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_attractiveTypeDisplayName">외모</span>
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_username">insta_user100</span>
                        """.stripIndent().trim())))
                .andExpect(content().string(containsString("""
                        <span class="toInstaMember_attractiveTypeDisplayName">성격</span>
                        """.stripIndent().trim())));
    }

    @Test
    @DisplayName("호감삭제")
    @WithUserDetails("user3")
    void t006() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/likeablePerson/delete/1").with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/likeablePerson/list**"))
        ;

        //삭제 했으니, 조회한 결과가 null 인지 체킹
        Assertions.assertThat(likeablePersonService.findById(1L)).isEqualTo(null);
    }

    @Test
    @DisplayName("호감삭제(없는거 삭제, 삭제가 안되어야 함)")
    @WithUserDetails("user3")
    void t007() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/likeablePerson/delete/100").with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("호감삭제(권한이 없는 경우, 삭제가 안됨)")
    @WithUserDetails("user2")
    void t008() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(delete("/likeablePerson/delete/1").with(csrf()))
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("동일한 사유로는 호감등록이 불가능하다.")
    @WithUserDetails("user3")
    void t009() throws Exception{

        //when
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf())
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", String.valueOf(1)));
        //then
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("이미 등록한 호감대상에게 호감사유 변경")
    @WithUserDetails("user3")
    void t010() throws Exception{
        //given
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = memberRepository.findByUsername(user.getUsername()).get();
        int beforeSize = member.getInstaMember().getFromLikeablePeople().size();

        //when
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/like")
                        .with(csrf())
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", String.valueOf(3)));


        //then
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("like"))
                .andExpect(status().is3xxRedirection());

        //변경감지로 인해서 member 다시 조회 안해도된다.
        int afterSize = member.getInstaMember().getFromLikeablePeople().size();
        Assertions.assertThat(beforeSize).isEqualTo(afterSize);
    }

    @Test
    @DisplayName("호감등록은 최대 likeablePersonFromMax 명까지만 가능하다.")
    @WithUserDetails("user2")
    void t011() throws Exception{
        User user = (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        long likeablePersonFromMax = AppConfig.getLikeablePersonFromMax();

        //when - likeablePersonFromMax + 1명을 호감 등록해보자.
        ResultActions resultActions = null;
        for(int i=1; i<=likeablePersonFromMax+1; i++){
            resultActions = mvc
                    .perform(post("/likeablePerson/like")
                            .with(csrf())
                            .param("username", "AAAA" + i)
                            .param("attractiveTypeCode", String.valueOf(1)));
        }

        //then
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("add"))
                .andExpect(status().is4xxClientError());
        Member member = memberRepository.findByUsername(user.getUsername()).get();
        int size = member.getInstaMember().getFromLikeablePeople().size();
        Assertions.assertThat(size).isEqualTo(10);
    }
}
