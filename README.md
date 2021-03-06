# TDD-membership
tdd, springboot, jpa

## TDD : 테스트 주도 개발
- 단위 테스트의 중요성
  - 코드 수정 또는 기능 추가 시 빠르게 검증 가능
  - 리팩토링 시에 안정성 확보
  - 개발 및 테스팅에 대한 시간과 비용 절감
- 좋은 테스트 특징
  - Fast              : 빠르다
  - Independent       : 독립적이다
  - Repeatable        : 어느 환경에서도 반복 가능
  - Self-Validating   : 자체적으로 검증되어야 한다. (성공 또는 실패)
  - Timely            : 실제 코드를 구현하기 직전에 구현
  정리 : **빠르고 독립적으로 어느 환경에서나 실행되어야 하며 검증되어야 한다**. 
  Clean Code 에서 테스트 코드는 실제 코드 구현하기 직전에 구현되어야 한다고 설명하고 있다.

## 테스트 코드를 먼저 작성해야 하는 이유
- 깔끔한 코드를 작성할 수 있다.
- 장기적으로 개발 비용을 절감할 수 있다.
- 개발이 끝나면 테스트 코드를 작성하는 것은 매우 귀찮다. 실패 케이스면 더욱 그렇다... (뜨끔)

장기적으로 본다면 개발비용을 확실하게 절감할 수 있다.
**그리고 실패 테스트부터 작성해야 한다.** 순차적으로 실패하는 테스트를 먼저 작성하고, 오직 테스트가
실패할 경우에만 새로운 코드를 작성한다. 그러하고 중복된 코드가 있으면 제거를 하는 것이다.
