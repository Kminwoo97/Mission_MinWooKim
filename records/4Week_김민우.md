## Title: [4Week] 김민우

### 미션 요구사항 분석 & 체크리스트

필수미션
* [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현
* [x] 네이버클라우드플랫폼을 통한 배포, 도메인, HTTPS 까지 적용

<br>

선택미션 
* [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 호감사유 필터링기능 구현
* [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 정렬기능
  * [x] 최신순: 최근에 받은 호감부터 보여준다.
  * [x] 날짜순: 가장 오래전에 받은 호감부터 보여준다.
  * [x] 인기 많은 순서: 나에게 호감을 표하는 사람들의 인기를 기준으로 정렬한다.
  * [x] 인기 적은 순서: 나에게 호감을 표하는 사람들의 인기를 기준으로 정렬한다.
  * [x] 성별순: 여성부터 표기하고 그 다음에 남성 표기. 2순위 정렬조건은 최신
  * [x] 호감사유: 외모->성격->능력 순서로 리스트를 정렬한다.
* [ ] 젠킨스를 통해서 repository 의 main 브랜치에 커밋 이벤트가 발생하면 자동으로 배포되로고 하기.

### 4주차 미션 요약

---

**[접근 방법]**

1. 필터링 기능 + 정렬 기능
* stream 의 filter()를 이용해서 List<LikeablePerson> 을 gender, attractiveTypeCode 2개를 기준으로 필터링을 했다.
* 정렬 기능도 마찬가지로 stream 을 사용했다. stream 의 sorted() 와 정렬의 기준으로 세우는 Comparator.comparing()을 사용했다.
  * 정렬 기준이 2가지 이상인 경우에는 `Comparator.comparing(1차 정렬 조건).thenComparing(2차 정렬 조건)` 이렇게 사용했다.



**[특이사항]**
* stream 을 이용해서 정렬을 하였는데, 인기를 기준으로 정렬을 하는데 조금 어려웠다.
* likeablePerson.getFromInstaMember().getToLikeablePeople().size() 로 비교를 해야되는데 계속 에러가 발생했다.
* 에러의 원인은 chatGPT 에게 물어봤더니, getToLikeablePeople() or getFromInstaMember() 에게 null 을 반환할 수 있기 때문이였다.

**참고: [Refactoring]**
