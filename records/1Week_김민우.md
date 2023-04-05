## Title: [1Week] 김민우

### 미션 요구사항 분석 & 체크리스트

필수 기능
* [x] 호감목록 삭제 기능


선택 기능
* [x] 구글 로그인 (선택)


### 1주차 미션 요약

---

**[접근 방법]**

1. 호감목록 삭제 기능
* 처음에는 단순하게 LikeablePersonService 에 likeablePersonRepository.delete() 를 하였다.
* 단순하게 delete() 를 하게되면 삭제 기능은 동작한다. 하지만 검증을 하지 않았다.

<br>

* 검증
    * LikeablePersonController 에 구현한 method 상단에 `@PreAuthorize("isAuthenticated()")` 를 붙여서 로그인 한 사용자만 요청할 수 있도록 하였다.
    * 현재 호감목록 삭제 요청이 GET 방식이기 때문에 URL에  `/likeablePerson/delete/3` 와 같이 삭제요청을 할 수 있으므로 자신이 등록한 호감목록이 아닌 경우를 검증하기 위해서 LikeablePersonService의 delete() 에 요청한 사용자 검증을 추가했다.
    * 마찬가지로 존재하지 않은 likeablePersonId 가 넘어올 수 있으므로 그것에 대한 검증도 추가하였다.

<br>

2. 구글 로그인
* https://lotuus.tistory.com/79 -> 해당 블로그를 참조했다.

<br>

**[특이사항]**
* 일반회원 로그인 같은 경우는 로그아웃 했다가 다시 로그인 하면 회원정보를 다시 입력해야하는데, OAuth2 로그인(카카오, 구글)은 첫 로그인 이후에 로그아웃을 했다가 다시 로그인 하면 자동으로 로그인 되는 부분이 궁금하다.
* 삭제요청은 DELETE method 혹은 POST method 로 해야되는 것으로 알고있는데, 이부분은 추후에 수정해야겠다..
<br>

**참고: [Refactoring]**

* application.yml 파일의 중요정보를 application.properties 파일에 환경변수로 등록해서 가져와서 사용했다.
