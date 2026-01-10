-- 개발 관련 기술 게시글 100개 생성
-- user_id는 1~5 사이 값 사용 (기존 사용자 가정)

INSERT INTO board (title, content, author, nickname, user_id, category_id, status, is_draft, is_pinned, is_important, view_count, like_count, comment_count, created_at, updated_at) VALUES

-- Spring Boot 관련 (1-15)
('Spring Boot 3.2 주요 변경사항 총정리', '최근 릴리즈된 Spring Boot 3.2에서는 Virtual Threads 지원이 공식적으로 추가되었습니다. 이를 통해 기존의 Platform Thread보다 훨씬 가벼운 스레드를 사용할 수 있게 되었는데요. application.yml에서 spring.threads.virtual.enabled=true로 설정하면 간단히 적용 가능합니다. 또한 Observability 기능이 강화되어 Micrometer와 OpenTelemetry 통합이 더욱 쉬워졌습니다. GraalVM Native Image 지원도 개선되어 빌드 시간이 30% 이상 단축되었다고 합니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''50 days'', NOW() - INTERVAL ''50 days''),

('@Transactional 제대로 이해하기', 'Spring에서 가장 많이 사용하는 어노테이션 중 하나인 @Transactional, 과연 제대로 알고 쓰고 계신가요? 기본적으로 프록시 기반으로 동작하기 때문에 private 메서드에서는 작동하지 않습니다. 또한 같은 클래스 내부에서 호출 시에도 트랜잭션이 적용되지 않는데, 이는 자기 호출(self-invocation)에서 프록시를 거치지 않기 때문입니다. readOnly 속성을 적절히 사용하면 성능 최적화도 가능합니다. Propagation과 Isolation 레벨 설정도 중요한데, 실무에서는 주로 REQUIRED와 READ_COMMITTED를 사용합니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''49 days'', NOW() - INTERVAL ''49 days''),

('JPA N+1 문제 완벽 해결 가이드', 'JPA를 사용하다 보면 반드시 마주치게 되는 N+1 문제. 이는 연관관계 조회 시 발생하는 추가 쿼리 문제입니다. 해결 방법은 크게 3가지입니다. 첫째, Fetch Join을 사용하는 방법입니다. JPQL에서 join fetch 키워드로 해결 가능합니다. 둘째, @EntityGraph를 활용하는 방법으로 attributePaths에 fetch할 필드를 지정합니다. 셋째, @BatchSize를 사용해 IN 쿼리로 한 번에 가져오는 방법입니다. 각 방법의 장단점을 이해하고 상황에 맞게 적용해야 합니다.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''48 days'', NOW() - INTERVAL ''48 days''),

('Spring Security 6.0 마이그레이션 가이드', 'Spring Security 6.0으로 업그레이드하면서 많은 변경사항이 있었습니다. 가장 큰 변화는 WebSecurityConfigurerAdapter가 deprecated된 점입니다. 이제 SecurityFilterChain을 Bean으로 등록하는 방식으로 변경되었습니다. authorizeRequests()도 authorizeHttpRequests()로 바뀌었고, 람다 DSL이 권장됩니다. CSRF 설정도 csrf().disable()에서 csrf(csrf -> csrf.disable())로 변경되었습니다. 마이그레이션은 단계적으로 진행하는 것을 추천드립니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''47 days'', NOW() - INTERVAL ''47 days''),

('Kotlin + Spring Boot 실전 팁', 'Kotlin과 Spring Boot를 함께 사용하면 생산성이 크게 향상됩니다. data class를 Entity로 사용 시 주의할 점은 반드시 기본 생성자가 필요하다는 것입니다. allopen 플러그인으로 해결 가능합니다. lateinit var보다는 by lazy를 활용하는 것이 null 안전성 측면에서 좋습니다. @Autowired는 생략 가능하며 생성자 주입이 자동으로 됩니다. 확장 함수를 활용하면 Utility 클래스 없이도 깔끔한 코드 작성이 가능합니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''46 days'', NOW() - INTERVAL ''46 days''),

('Spring Cloud Gateway 도입기', '기존 Zuul에서 Spring Cloud Gateway로 마이그레이션한 경험을 공유합니다. Gateway는 WebFlux 기반이라 비동기 논블로킹으로 동작하여 높은 처리량을 보입니다. Route 설정은 yml 파일 또는 Java Config 두 가지 방식으로 가능합니다. Filter를 활용하면 인증, 로깅, Rate Limiting 등을 쉽게 구현할 수 있습니다. GlobalFilter와 GatewayFilter의 차이를 이해하는 것이 중요합니다. Circuit Breaker 패턴도 간단히 적용 가능합니다.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''45 days'', NOW() - INTERVAL ''45 days''),

('Spring Batch 성능 최적화하기', 'Spring Batch 작업의 성능을 10배 향상시킨 방법을 소개합니다. Chunk 사이즈는 무조건 크다고 좋은 것이 아닙니다. 메모리와 커밋 주기를 고려해 적절한 값을 찾아야 합니다. Parallel Step과 Partitioning을 활용하면 병렬 처리가 가능합니다. JpaPagingItemReader 대신 JdbcPagingItemReader를 사용하면 성능이 크게 개선됩니다. Skip 정책과 Retry 정책도 적절히 설정해야 안정적인 배치 운영이 가능합니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''44 days'', NOW() - INTERVAL ''44 days''),

('Spring WebFlux 입문 가이드', 'Reactive Programming의 세계에 오신 것을 환영합니다. Spring WebFlux는 적은 리소스로 높은 동시성을 처리할 수 있습니다. Mono는 0~1개, Flux는 0~N개의 데이터를 비동기로 처리합니다. block()을 호출하는 순간 reactive의 장점이 사라지니 주의해야 합니다. WebClient를 사용하면 논블로킹 HTTP 클라이언트를 쉽게 구현할 수 있습니다. 단, 러닝커브가 높아 팀원 전체가 reactive를 이해하고 있어야 합니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''43 days'', NOW() - INTERVAL ''43 days''),

('Spring Rest Docs로 API 문서 자동화', 'Swagger 대신 Spring Rest Docs를 선택한 이유는 테스트 코드 기반이라는 점입니다. 테스트가 성공해야만 문서가 생성되므로 문서와 실제 구현의 불일치가 없습니다. MockMvc를 사용한 테스트에 document()만 추가하면 됩니다. AsciiDoc 형식으로 작성되며 커스터마이징이 자유롭습니다. 빌드 시 자동으로 HTML 문서가 생성되어 배포됩니다. 초기 설정은 복잡하지만 한 번 구축하면 유지보수가 편합니다.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''42 days'', NOW() - INTERVAL ''42 days''),

('Spring Actuator 모니터링 활용법', 'Spring Actuator는 운영 환경에서 필수적인 모니터링 도구입니다. /actuator/health로 헬스체크, /actuator/metrics로 메트릭 조회가 가능합니다. Prometheus와 연동하면 Grafana 대시보드 구성이 쉬워집니다. Custom Health Indicator를 만들어 DB, Redis 등의 상태도 체크할 수 있습니다. 보안을 위해 프로덕션에서는 엔드포인트 노출을 제한해야 합니다. management.endpoints.web.exposure.include 설정으로 제어 가능합니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''41 days'', NOW() - INTERVAL ''41 days''),

('Spring Data JPA Query Methods 완벽 가이드', 'Spring Data JPA의 Query Method는 메서드 이름만으로 쿼리를 생성합니다. findBy, readBy, queryBy, getBy는 모두 같은 동작을 합니다. And, Or로 조건을 결합하고 OrderBy로 정렬합니다. Like, Containing, StartingWith, EndingWith로 문자열 검색이 가능합니다. Top, First로 결과 개수를 제한할 수 있습니다. 복잡한 쿼리는 @Query를 사용하거나 QueryDSL을 도입하는 것이 좋습니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''40 days'', NOW() - INTERVAL ''40 days''),

('Spring Cache 적용하여 응답속도 개선', '조회 API의 응답속도를 1초에서 50ms로 줄인 방법입니다. @EnableCaching으로 캐싱 활성화하고 @Cacheable을 메서드에 적용하면 끝입니다. Redis를 캐시 저장소로 사용하면 여러 인스턴스 간 캐시 공유가 가능합니다. @CacheEvict로 캐시 삭제, @CachePut으로 캐시 갱신이 가능합니다. TTL 설정으로 자동 만료 처리하고 Key 전략을 잘 세워야 합니다. 캐시 워밍업도 고려해야 합니다.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''39 days'', NOW() - INTERVAL ''39 days''),

('Spring AOP 실전 활용 사례', 'AOP를 활용하면 로깅, 트랜잭션, 보안 등 횡단 관심사를 분리할 수 있습니다. @Around로 메서드 실행 전후 처리가 가능하고 ProceedingJoinPoint로 메서드 실행을 제어합니다. 실행 시간 측정, 파라미터 로깅, 예외 처리를 AOP로 구현하면 코드가 깔끔해집니다. Pointcut 표현식을 잘 작성하는 것이 핵심입니다. execution, within, @annotation 등 다양한 지시자를 조합해 사용합니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''38 days'', NOW() - INTERVAL ''38 days''),

('Spring Boot Profile 관리 전략', '개발, 스테이징, 프로덕션 환경을 효과적으로 관리하는 방법입니다. application-{profile}.yml로 환경별 설정 분리가 가능합니다. spring.profiles.active로 활성 프로파일을 지정하고 @Profile 어노테이션으로 Bean 등록을 제어합니다. 민감한 정보는 환경 변수나 외부 설정 서버를 사용해야 합니다. Profile Group을 활용하면 여러 프로파일을 묶어서 활성화할 수 있습니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''37 days'', NOW() - INTERVAL ''37 days''),

('Spring Events로 도메인 이벤트 처리하기', '도메인 이벤트 패턴을 Spring Events로 구현하는 방법입니다. ApplicationEventPublisher로 이벤트를 발행하고 @EventListener로 수신합니다. @TransactionalEventListener를 사용하면 트랜잭션 커밋 후에 이벤트가 발행됩니다. 비동기 처리는 @Async를 추가하면 됩니다. 이벤트를 통해 도메인 간 결합도를 낮추고 확장성을 높일 수 있습니다. 다만 디버깅이 어려워질 수 있으니 적절한 로깅이 필요합니다.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''36 days'', NOW() - INTERVAL ''36 days''),

-- Java 관련 (16-30)
('Java 21 Virtual Threads 완벽 가이드', 'Project Loom의 핵심인 Virtual Threads가 Java 21에서 정식 기능이 되었습니다. Thread.ofVirtual()로 가상 스레드를 생성할 수 있으며 기존 Thread API와 호환됩니다. 수백만 개의 스레드도 가볍게 만들 수 있어 고성능 서버 개발에 유용합니다. Executor를 virtual thread로 변경하는 것만으로도 큰 성능 향상을 얻을 수 있습니다. 단, CPU 집약적인 작업에는 적합하지 않습니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''35 days'', NOW() - INTERVAL ''35 days''),

('Java Stream API 성능 최적화', 'Stream API는 편리하지만 잘못 사용하면 성능이 떨어집니다. 박싱/언박싱 비용을 줄이려면 IntStream, LongStream을 사용해야 합니다. parallel()은 무조건 빠른 것이 아니며 데이터 크기와 작업 복잡도를 고려해야 합니다. 중간 연산은 lazy하게 동작하고 최종 연산에서 실제로 수행됩니다. collect()보다 toList()가 더 효율적인 경우도 있습니다. 벤치마킹으로 실제 성능을 측정하는 것이 중요합니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''34 days'', NOW() - INTERVAL ''34 days''),

('Optional 제대로 사용하기', 'Optional은 null 처리를 위한 컨테이너입니다. isPresent() 후 get()을 호출하는 것은 안티패턴입니다. orElse, orElseGet, orElseThrow를 활용하세요. orElse는 값이 있어도 항상 실행되므로 비용이 큰 작업은 orElseGet을 사용해야 합니다. map, flatMap, filter로 함수형 스타일의 처리가 가능합니다. 필드나 파라미터 타입으로는 사용하지 않는 것이 좋습니다.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''33 days'', NOW() - INTERVAL ''33 days''),

('Java Record 활용 가이드', 'Java 16에서 정식 도입된 Record는 불변 데이터 클래스를 간단히 만듭니다. 자동으로 생성자, getter, equals, hashCode, toString이 생성됩니다. 상속은 불가능하지만 인터페이스 구현은 가능합니다. Compact Constructor로 유효성 검증을 추가할 수 있습니다. DTO, Value Object 구현에 적합하며 직렬화도 기본 지원합니다. 가독성과 안전성을 동시에 향상시킬 수 있습니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''32 days'', NOW() - INTERVAL ''32 days''),

('Java Garbage Collection 튜닝하기', 'GC 로그 분석을 통해 애플리케이션 성능을 개선한 경험을 공유합니다. G1GC가 기본이지만 환경에 따라 ZGC나 Shenandoah가 더 나을 수 있습니다. Heap 크기, Young/Old 비율, GC 스레드 수 등을 조정합니다. -XX:+PrintGCDetails로 상세 로그를 확인하고 GCViewer 같은 도구로 분석합니다. 메모리 누수가 의심되면 Heap Dump를 떠서 분석해야 합니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''31 days'', NOW() - INTERVAL ''31 days''),

('CompletableFuture로 비동기 프로그래밍', '비동기 작업을 쉽게 처리하는 CompletableFuture 활용법입니다. supplyAsync로 비동기 작업을 시작하고 thenApply로 체이닝합니다. thenCompose는 중첩된 CompletableFuture를 평탄화하고 thenCombine으로 여러 작업을 병합합니다. exceptionally나 handle로 예외 처리가 가능합니다. allOf, anyOf로 여러 Future를 조합할 수 있습니다. 적절한 ExecutorService 사용이 중요합니다.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''30 days'', NOW() - INTERVAL ''30 days''),

('Lombok 사용 시 주의사항', 'Lombok은 편리하지만 잘못 사용하면 문제가 됩니다. @Data는 너무 많은 기능을 포함하므로 @Getter, @Setter를 개별 사용하세요. @EqualsAndHashCode는 순환 참조 문제를 일으킬 수 있으니 exclude를 지정합니다. @Builder는 필수 필드 검증이 없으니 주의해야 합니다. @ToString도 지연 로딩 필드는 제외해야 N+1 문제를 피할 수 있습니다. IDE 플러그인 설치도 필수입니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''29 days'', NOW() - INTERVAL ''29 days''),

('Java 모듈 시스템(JPMS) 실전 적용', 'Java 9부터 도입된 모듈 시스템 적용 경험을 공유합니다. module-info.java에 의존성과 공개 패키지를 명시합니다. requires로 필요한 모듈을 선언하고 exports로 공개할 패키지를 지정합니다. 강한 캡슐화로 내부 API 접근을 차단할 수 있습니다. 레거시 라이브러리는 자동 모듈로 전환됩니다. 대규모 프로젝트에서 모듈 경계를 명확히 하는 데 유용합니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''28 days'', NOW() - INTERVAL ''28 days''),

('JVM 메모리 구조 완벽 이해', 'JVM의 메모리는 Heap, Stack, Method Area, PC Register, Native Method Stack으로 구성됩니다. Heap은 객체가 저장되고 GC 대상이 됩니다. Stack은 스레드별로 생성되며 지역변수와 메서드 호출 정보를 저장합니다. Method Area에는 클래스 메타데이터가 저장됩니다. PermGen은 Java 8부터 Metaspace로 대체되었습니다. 메모리 영역별 특성을 이해하면 OOM 문제 해결에 도움이 됩니다.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''27 days'', NOW() - INTERVAL ''27 days''),

('Reflection API 활용과 성능', 'Reflection은 런타임에 클래스 정보를 조작할 수 있게 합니다. Class.forName()으로 클래스를 로드하고 getDeclaredFields()로 필드를 가져옵니다. setAccessible(true)로 private 필드 접근이 가능합니다. 프레임워크 개발에 유용하지만 성능 오버헤드가 있습니다. 가능하면 캐싱하고 MethodHandle을 고려하세요. Java Agent나 어노테이션 프로세서도 대안이 될 수 있습니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''26 days'', NOW() - INTERVAL ''26 days''),

('Java Enum 고급 활용법', 'Enum은 단순 상수가 아닌 강력한 타입입니다. 필드와 메서드를 가질 수 있고 생성자로 초기화합니다. 추상 메서드를 선언하고 각 상수에서 구현하면 Strategy 패턴이 됩니다. EnumSet과 EnumMap으로 성능 최적화가 가능합니다. values()보다 valueOf()를 사용하고 ordinal()은 피해야 합니다. 상태 머신이나 커맨드 패턴 구현에도 적합합니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''25 days'', NOW() - INTERVAL ''25 days''),

('Exception Handling 베스트 프랙티스', '예외 처리는 견고한 애플리케이션의 핵심입니다. Checked Exception은 복구 가능한 경우에만 사용하세요. 대부분은 Unchecked Exception이 적합합니다. catch 블록에서 Exception을 먹지 마세요. 의미 있는 메시지를 포함한 커스텀 예외를 만드세요. try-with-resources로 리소스를 자동 정리합니다. 로깅과 모니터링도 빠뜨리지 마세요.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''24 days'', NOW() - INTERVAL ''24 days''),

('Java Generics 심화 학습', '제네릭의 타입 소거(Type Erasure) 때문에 런타임에 타입 정보가 사라집니다. 와일드카드 ? extends T는 읽기만, ? super T는 쓰기에 사용합니다. PECS 원칙(Producer Extends, Consumer Super)을 기억하세요. 제네릭 배열은 만들 수 없지만 List를 사용하면 됩니다. 타입 토큰 패턴으로 런타임 타입 정보를 전달할 수 있습니다.', 'admin', '관리자', 1, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''23 days'', NOW() - INTERVAL ''23 days''),

('Java Concurrent 프로그래밍', '동시성 프로그래밍의 핵심은 Thread-Safety입니다. synchronized 블록으로 동기화하고 volatile로 가시성을 보장합니다. java.util.concurrent 패키지의 Atomic 클래스들을 활용하세요. CountDownLatch, CyclicBarrier로 스레드를 조율합니다. ExecutorService로 스레드 풀을 관리하고 Future로 결과를 받습니다. 데드락과 레이스 컨디션을 항상 주의해야 합니다.', 'user1', '개발자1', 2, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''22 days'', NOW() - INTERVAL ''22 days''),

('Java NIO2 파일 처리', 'Files 클래스로 파일 작업이 간편해졌습니다. Files.readAllLines()는 작은 파일, Files.lines()는 큰 파일 읽기에 적합합니다. Path API로 경로를 추상화하고 Files.walk()로 디렉토리를 순회합니다. WatchService로 파일 변경을 감지할 수 있습니다. 메모리 맵 파일로 대용량 파일을 빠르게 처리합니다. try-with-resources를 사용해 리소스 누수를 방지하세요.', 'user2', '개발자2', 3, 1, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''21 days'', NOW() - INTERVAL ''21 days''),

-- JavaScript/TypeScript 관련 (31-45)
('TypeScript 5.0 새로운 기능들', 'TypeScript 5.0에서 Decorators가 정식 기능이 되었습니다. const Type Parameters로 더 정확한 타입 추론이 가능합니다. Enum과 Union의 성능이 개선되었고 번들 크기도 줄었습니다. extends 조건에서 모든 타입 사용이 가능해졌습니다. 빌드 속도가 10-20% 향상되어 대규모 프로젝트에 유리합니다. 마이그레이션은 대부분 호환되지만 일부 엄격해진 검사를 주의하세요.', 'admin', '관리자', 1, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''20 days'', NOW() - INTERVAL ''20 days''),

('async/await 완벽 마스터', 'Promise를 더 읽기 쉽게 만드는 async/await 완벽 가이드입니다. async 함수는 항상 Promise를 반환합니다. await는 Promise가 resolve될 때까지 기다립니다. try-catch로 에러를 처리하고 Promise.all()로 병렬 실행합니다. Top-level await로 모듈 레벨에서도 사용 가능합니다. 에러 처리를 빠뜨리지 않도록 주의하세요.', 'user1', '개발자1', 2, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''19 days'', NOW() - INTERVAL ''19 days''),

('Closure와 스코프 이해하기', 'JavaScript의 핵심 개념인 클로저를 완벽히 이해해봅시다. 클로저는 함수가 생성될 때의 렉시컬 환경을 기억합니다. 이를 활용해 데이터 은닉과 모듈 패턴을 구현할 수 있습니다. var는 함수 스코프, let/const는 블록 스코프를 가집니다. 호이스팅으로 인한 문제를 피하려면 let/const를 사용하세요. IIFE 패턴도 알아두면 유용합니다.', 'user2', '개발자2', 3, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''18 days'', NOW() - INTERVAL ''18 days''),

('ES6+ 최신 문법 정리', '모던 JavaScript의 필수 문법들을 정리했습니다. 화살표 함수는 this를 바인딩하지 않습니다. 구조 분해 할당으로 깔끔한 코드를 작성하세요. 스프레드 연산자와 rest 파라미터는 배열/객체 조작에 유용합니다. Template Literal로 문자열 조작이 편해집니다. Optional Chaining과 Nullish Coalescing으로 안전한 접근이 가능합니다.', 'admin', '관리자', 1, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''17 days'', NOW() - INTERVAL ''17 days''),

('this 바인딩 완벽 정리', 'JavaScript에서 가장 헷갈리는 this를 정복합니다. 일반 함수의 this는 호출 방식에 따라 달라집니다. call, apply, bind로 명시적 바인딩이 가능합니다. 화살표 함수는 상위 스코프의 this를 사용합니다. 클래스 메서드는 자동 바인딩되지 않으니 주의하세요. 이벤트 핸들러에서 this를 사용할 때는 bind를 활용합니다.', 'user1', '개발자1', 2, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''16 days'', NOW() - INTERVAL ''16 days''),

('Prototype과 상속 이해하기', 'JavaScript는 프로토타입 기반 언어입니다. 모든 객체는 __proto__ 링크를 가지며 이를 통해 상속합니다. Object.create()로 프로토타입을 지정할 수 있습니다. class 문법은 프로토타입의 syntactic sugar입니다. hasOwnProperty로 직접 속성인지 확인하세요. 프로토타입 체인을 이해하면 JavaScript를 깊이 있게 이해할 수 있습니다.', 'user2', '개발자2', 3, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''15 days'', NOW() - INTERVAL ''15 days''),

('이벤트 루프와 비동기 처리', 'JavaScript 런타임의 핵심인 이벤트 루프를 알아봅니다. Call Stack, Task Queue, Microtask Queue로 구성됩니다. Promise는 Microtask Queue에 들어가 우선순위가 높습니다. setTimeout(fn, 0)도 즉시 실행되지 않습니다. requestAnimationFrame은 브라우저 렌더링과 동기화됩니다. 이벤트 루프를 이해하면 성능 최적화에 도움이 됩니다.', 'admin', '관리자', 1, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''14 days'', NOW() - INTERVAL ''14 days''),

('Module System 완벽 가이드', 'CommonJS와 ES Modules의 차이를 이해합니다. require는 동기적, import는 비동기적으로 로드됩니다. ESM은 정적 분석이 가능해 Tree Shaking에 유리합니다. default export와 named export를 적절히 사용하세요. 순환 의존성을 피하고 Dynamic Import로 코드 스플리팅합니다. Node.js에서는 package.json의 type 필드로 모듈 시스템을 지정합니다.', 'user1', '개발자1', 2, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''13 days'', NOW() - INTERVAL ''13 days''),

('Web Workers로 멀티스레딩', 'JavaScript의 싱글 스레드 한계를 극복하는 Web Workers입니다. 무거운 계산을 Worker로 옮겨 메인 스레드를 차단하지 않습니다. postMessage로 통신하고 transferable objects로 성능을 최적화합니다. SharedArrayBuffer로 메모리를 공유할 수 있습니다. Worker 생성 비용이 있으니 재사용하세요. 이미지 처리, 데이터 파싱 등에 활용합니다.', 'user2', '개발자2', 3, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''12 days'', NOW() - INTERVAL ''12 days''),

('Proxy와 Reflect API 활용', 'Proxy로 객체의 기본 동작을 가로챌 수 있습니다. get, set, has 등의 트랩으로 다양한 동작을 커스터마이징합니다. Vue 3의 반응성이 Proxy 기반입니다. Reflect는 Proxy 트랩에 대응하는 메서드를 제공합니다. 유효성 검증, 로깅, 캐싱 등을 구현할 수 있습니다. 성능 오버헤드가 있으니 필요한 곳에만 사용하세요.', 'admin', '관리자', 1, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''11 days'', NOW() - INTERVAL ''11 days''),

('Iterator와 Generator 완벽 이해', 'Iterator 프로토콜로 순회 가능한 객체를 만듭니다. Generator 함수는 yield로 값을 하나씩 반환합니다. for...of는 Iterator를 소비하고 spread 연산자도 사용 가능합니다. 무한 시퀀스를 만들거나 지연 평가에 활용합니다. async generator로 비동기 스트림도 처리할 수 있습니다. Redux-Saga가 제너레이터 기반입니다.', 'user1', '개발자1', 2, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''10 days'', NOW() - INTERVAL ''10 days''),

('WeakMap과 WeakSet 활용법', 'WeakMap과 WeakSet은 약한 참조를 사용합니다. 키가 GC되면 자동으로 제거되어 메모리 누수를 방지합니다. private 데이터를 저장하는 패턴에 유용합니다. 열거가 불가능하고 size 속성이 없습니다. DOM 노드를 키로 사용할 때 적합합니다. 캐싱이나 메타데이터 저장에 활용할 수 있습니다.', 'user2', '개발자2', 3, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''9 days'', NOW() - INTERVAL ''9 days''),

('Symbol과 Well-known Symbols', 'Symbol은 고유하고 불변인 원시 값입니다. 객체 속성 키로 사용하면 충돌을 방지합니다. Well-known Symbols로 내장 동작을 커스터마이징할 수 있습니다. Symbol.iterator로 순회 가능하게 만들고 Symbol.toStringTag로 타입을 지정합니다. Symbol.for()로 전역 심볼을 만들 수 있습니다. 메타프로그래밍에 강력한 도구입니다.', 'admin', '관리자', 1, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''8 days'', NOW() - INTERVAL ''8 days''),

('TypeScript Utility Types 활용', 'TypeScript의 내장 유틸리티 타입으로 생산성을 높입니다. Partial은 모든 속성을 선택적으로, Required는 필수로 만듭니다. Pick과 Omit으로 타입을 선택하거나 제외합니다. Record로 객체 타입을 간단히 정의하고 ReturnType으로 함수 반환 타입을 추출합니다. Exclude, Extract로 유니온 타입을 조작할 수 있습니다.', 'user1', '개발자1', 2, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''7 days'', NOW() - INTERVAL ''7 days''),

('Conditional Types 심화', 'TypeScript의 조건부 타입으로 고급 타입을 만듭니다. extends 키워드로 조건을 표현하고 삼항 연산자로 타입을 선택합니다. infer로 타입을 추출할 수 있습니다. Distributive Conditional Types는 유니온을 자동 분배합니다. 재귀적 조건 타입도 가능합니다. 라이브러리 타입 정의에 많이 사용됩니다.', 'user2', '개발자2', 3, 2, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''6 days'', NOW() - INTERVAL ''6 days''),

-- React 관련 (46-60)
('React 18 새로운 기능 완벽 정리', 'Concurrent Features가 React 18의 핵심입니다. useTransition으로 급하지 않은 업데이트를 표시하고 useDeferredValue로 값 업데이트를 지연시킵니다. Automatic Batching으로 여러 상태 업데이트가 한 번에 처리됩니다. Suspense가 서버 컴포넌트를 지원하고 Streaming SSR이 가능해졌습니다. startTransition으로 UX를 크게 개선할 수 있습니다.', 'admin', '관리자', 1, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''5 days'', NOW() - INTERVAL ''5 days''),

('useEffect 완벽 이해하기', 'useEffect는 부수 효과를 다루는 Hook입니다. 의존성 배열을 정확히 명시해야 합니다. cleanup 함수로 구독을 해제하고 메모리 누수를 방지합니다. useEffect는 렌더링 후에 실행되고 useLayoutEffect는 렌더링 전에 실행됩니다. 빈 배열은 마운트 시 한 번만, 배열 없음은 매 렌더링마다 실행됩니다. ESLint 규칙을 따라 안전하게 사용하세요.', 'user1', '개발자1', 2, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''4 days'', NOW() - INTERVAL ''4 days''),

('React Query로 서버 상태 관리', 'React Query는 서버 상태 관리의 새로운 패러다임입니다. useQuery로 데이터를 가져오고 자동 캐싱, 재시도, 폴링이 가능합니다. useMutation으로 데이터를 변경하고 invalidateQueries로 캐시를 무효화합니다. Optimistic Updates로 즉각적인 UI 업데이트를 구현합니다. Suspense 모드도 지원하고 devtools로 디버깅이 쉽습니다.', 'user2', '개발자2', 3, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''3 days'', NOW() - INTERVAL ''3 days''),

('Zustand vs Recoil vs Redux', '상태 관리 라이브러리를 비교 분석했습니다. Redux는 성숙하고 강력하지만 보일러플레이트가 많습니다. Zustand는 간단하고 직관적이며 번들 크기가 작습니다. Recoil은 React스러운 API와 비동기 상태를 잘 다룹니다. 프로젝트 규모와 팀 경험에 따라 선택하세요. 중소규모는 Zustand, 대규모는 Redux Toolkit을 추천합니다.', 'admin', '관리자', 1, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''2 days'', NOW() - INTERVAL ''2 days''),

('React 성능 최적화 완벽 가이드', '불필요한 리렌더링을 방지하는 방법들을 소개합니다. React.memo로 컴포넌트를 메모이제이션하고 useMemo로 값을 캐싱합니다. useCallback으로 함수를 메모이제이션하여 props 안정성을 보장합니다. 리스트 렌더링 시 key를 올바르게 사용하세요. React DevTools Profiler로 병목을 찾아 최적화합니다. Code Splitting으로 초기 번들 크기를 줄입니다.', 'user1', '개발자1', 2, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() - INTERVAL ''1 days'', NOW() - INTERVAL ''1 days''),

('Custom Hook 제대로 만들기', 'Custom Hook으로 로직을 재사용합니다. use 접두사를 사용하고 다른 Hook을 호출할 수 있습니다. 여러 컴포넌트에서 공통 로직을 추출하여 중복을 제거합니다. 상태와 로직을 함께 캡슐화할 수 있습니다. useEffect의 cleanup도 잊지 마세요. TypeScript와 함께 사용하면 타입 안전성이 보장됩니다.', 'user2', '개발자2', 3, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW(), NOW()),

('Context API 올바른 사용법', 'Context로 전역 상태를 관리하지만 남용하면 성능 문제가 생깁니다. Provider 분리로 리렌더링 범위를 최소화하세요. useMemo로 value를 메모이제이션합니다. 자주 변경되는 값은 Context가 아닌 다른 방법을 고려하세요. Composition을 우선하고 Context는 보조 수단으로 사용합니다. 테마, 인증 정보 등에 적합합니다.', 'admin', '관리자', 1, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''1 days'', NOW() + INTERVAL ''1 days''),

('Error Boundary로 에러 처리', 'Error Boundary는 하위 컴포넌트의 에러를 catch합니다. componentDidCatch로 에러를 로깅하고 getDerivedStateFromError로 대체 UI를 표시합니다. 이벤트 핸들러 에러는 잡지 못하니 try-catch를 사용하세요. React Query의 에러 처리와 조합하면 강력합니다. 여러 레벨에 배치하여 세밀한 에러 처리가 가능합니다.', 'user1', '개발자1', 2, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''2 days'', NOW() + INTERVAL ''2 days''),

('React Testing Library 활용법', '사용자 관점에서 테스트하는 React Testing Library입니다. getByRole로 접근성을 고려한 쿼리를 작성합니다. userEvent로 실제 사용자 동작을 시뮬레이션합니다. waitFor로 비동기 동작을 테스트하고 MSW로 API를 모킹합니다. 구현 세부사항이 아닌 동작을 테스트하세요. 좋은 테스트는 리팩토링을 두려워하지 않게 합니다.', 'user2', '개발자2', 3, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''3 days'', NOW() + INTERVAL ''3 days''),

('Next.js App Router 완벽 가이드', 'App Router는 Next.js 13의 새로운 라우팅 시스템입니다. 파일 시스템 기반이며 Server Components가 기본입니다. layout.js로 레이아웃을 공유하고 loading.js로 로딩 UI를 만듭니다. 서버에서 데이터를 가져와 초기 로딩이 빠릅니다. use client 지시어로 클라이언트 컴포넌트를 명시합니다. 점진적으로 마이그레이션할 수 있습니다.', 'admin', '관리자', 1, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''4 days'', NOW() + INTERVAL ''4 days''),

('Tailwind CSS와 React 조합', 'Utility-First CSS인 Tailwind로 빠른 스타일링이 가능합니다. className에 유틸리티 클래스를 나열하고 JIT 모드로 번들 크기를 최소화합니다. @apply로 반복되는 패턴을 추출할 수 있습니다. clsx나 classnames로 조건부 스타일을 깔끔하게 작성합니다. 디자인 시스템과 통합하면 일관된 UI를 유지할 수 있습니다.', 'user1', '개발자1', 2, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''5 days'', NOW() + INTERVAL ''5 days''),

('React Hook Form으로 폼 관리', 'React Hook Form은 성능에 최적화된 폼 라이브러리입니다. register로 필드를 등록하고 handleSubmit으로 제출을 처리합니다. watch로 값을 관찰하되 불필요한 리렌더링을 피합니다. yup이나 zod로 스키마 기반 유효성 검사를 통합합니다. Controller로 커스텀 컴포넌트도 쉽게 통합됩니다. 복잡한 폼도 간단히 다룰 수 있습니다.', 'user2', '개발자2', 3, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''6 days'', NOW() + INTERVAL ''6 days''),

('Framer Motion으로 애니메이션', 'Framer Motion은 선언적인 애니메이션 라이브러리입니다. motion 컴포넌트에 animate props를 전달하면 됩니다. variants로 복잡한 애니메이션을 정의하고 재사용합니다. AnimatePresence로 마운트/언마운트 애니메이션을 만듭니다. layout 애니메이션은 자동으로 최적화됩니다. 드래그, 제스처도 쉽게 구현할 수 있습니다.', 'admin', '관리자', 1, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''7 days'', NOW() + INTERVAL ''7 days''),

('React Server Components 이해하기', 'RSC는 서버에서만 실행되는 컴포넌트입니다. 클라이언트 번들에 포함되지 않아 크기가 줄어듭니다. async 컴포넌트로 서버에서 데이터를 직접 가져올 수 있습니다. 민감한 로직을 서버에 유지하여 보안이 강화됩니다. 클라이언트 컴포넌트와 혼용 가능하며 점진적 도입이 가능합니다. 아직 실험적이지만 미래의 표준이 될 것입니다.', 'user1', '개발자1', 2, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''8 days'', NOW() + INTERVAL ''8 days''),

('React Suspense 활용 전략', 'Suspense로 로딩 상태를 선언적으로 처리합니다. fallback으로 로딩 UI를 지정하고 여러 레벨에 배치할 수 있습니다. React.lazy로 컴포넌트를 지연 로딩하고 코드 스플리팅합니다. React Query와 함께 사용하면 데이터 로딩도 Suspense로 처리됩니다. Streaming SSR과 결합하여 초기 로딩 속도를 개선합니다.', 'user2', '개발자2', 3, 3, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''9 days'', NOW() + INTERVAL ''9 days''),

-- Database 관련 (61-75)
('PostgreSQL 성능 튜닝 실전', 'EXPLAIN ANALYZE로 쿼리 실행 계획을 분석합니다. 인덱스를 적절히 생성하고 VACUUM으로 데이터베이스를 정리합니다. shared_buffers, work_mem 등 메모리 설정을 조정합니다. 파티셔닝으로 대용량 테이블을 분할하고 Materialized View로 집계 쿼리를 최적화합니다. Connection Pooling으로 연결 오버헤드를 줄입니다. pg_stat_statements로 슬로우 쿼리를 찾아냅니다.', 'admin', '관리자', 1, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''10 days'', NOW() + INTERVAL ''10 days''),

('MongoDB 스키마 설계 패턴', 'NoSQL이지만 스키마 설계는 중요합니다. 임베딩 vs 참조를 적절히 선택하세요. 1:N 관계에서 N이 작으면 임베딩, 크면 참조가 유리합니다. 버킷 패턴으로 시계열 데이터를 효율적으로 저장합니다. 계산된 패턴으로 집계 결과를 미리 저장하고 폴리모픽 패턴으로 다양한 타입을 저장합니다. 인덱스 전략도 RDBMS와 유사하게 중요합니다.', 'user1', '개발자1', 2, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''11 days'', NOW() + INTERVAL ''11 days''),

('Redis 캐싱 전략과 패턴', 'Redis를 효과적으로 활용하는 캐싱 전략입니다. Cache-Aside 패턴이 가장 일반적이며 애플리케이션이 캐시를 직접 관리합니다. Write-Through는 쓰기 시 캐시도 함께 업데이트합니다. TTL을 적절히 설정하여 메모리를 관리하고 Eviction Policy로 만료 정책을 선택합니다. Pub/Sub으로 캐시 무효화를 전파할 수 있습니다. Sorted Set으로 순위 기능도 구현 가능합니다.', 'user2', '개발자2', 3, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''12 days'', NOW() + INTERVAL ''12 days''),

('SQL 인덱스 완벽 이해', '인덱스는 검색 속도를 높이지만 쓰기 성능을 떨어뜨립니다. B-Tree 인덱스가 기본이며 범위 검색에 효율적입니다. Hash 인덱스는 동등 비교만 빠르고 Bitmap 인덱스는 카디널리티가 낮을 때 유용합니다. 복합 인덱스는 컬럼 순서가 중요하며 선택도가 높은 컬럼을 앞에 배치합니다. 커버링 인덱스로 테이블 접근을 피할 수 있습니다.', 'admin', '관리자', 1, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''13 days'', NOW() + INTERVAL ''13 days''),

('트랜잭션 격리 수준 이해하기', 'ACID 중 Isolation을 결정하는 격리 수준입니다. Read Uncommitted는 Dirty Read가 발생하고 Read Committed는 이를 방지합니다. Repeatable Read는 Phantom Read를 허용하고 Serializable은 완전히 격리합니다. 격리 수준이 높을수록 동시성이 떨어집니다. 대부분의 경우 Read Committed면 충분하며 필요시 비관적/낙관적 락을 추가합니다.', 'user1', '개발자1', 2, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''14 days'', NOW() + INTERVAL ''14 days''),

('N+1 쿼리 문제 해결하기', 'ORM 사용 시 흔히 발생하는 N+1 문제를 해결합니다. Eager Loading으로 필요한 데이터를 한 번에 가져오고 Join을 활용하여 쿼리 수를 줄입니다. Batch Loading으로 IN 쿼리를 사용하고 DataLoader 패턴으로 중복 요청을 제거합니다. 쿼리 로그를 모니터링하여 문제를 조기에 발견하세요. 성능 테스트로 실제 영향을 측정합니다.', 'user2', '개발자2', 3, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''15 days'', NOW() + INTERVAL ''15 days''),

('데이터베이스 정규화와 반정규화', '정규화는 중복을 제거하고 무결성을 보장합니다. 1NF는 원자값, 2NF는 부분 종속 제거, 3NF는 이행 종속 제거입니다. BCNF까지 하면 대부분 충분합니다. 반정규화는 조회 성능을 위해 의도적으로 중복을 허용합니다. 읽기가 많은 시스템에서는 반정규화가 유리할 수 있습니다. 트레이드오프를 이해하고 선택하세요.', 'admin', '관리자', 1, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''16 days'', NOW() + INTERVAL ''16 days''),

('Sharding과 Replication 전략', '대용량 데이터 처리를 위한 샤딩과 복제 전략입니다. 수평 샤딩으로 데이터를 여러 서버에 분산하고 샤드 키 선택이 핵심입니다. Range, Hash, Directory 샤딩 방식이 있습니다. Master-Slave 복제로 읽기 성능을 향상하고 Multi-Master로 쓰기도 분산합니다. 일관성과 가용성의 트레이드오프를 고려해야 합니다.', 'user1', '개발자1', 2, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''17 days'', NOW() + INTERVAL ''17 days''),

('Elasticsearch 실전 활용', '전문 검색 엔진인 Elasticsearch 활용법입니다. Inverted Index로 빠른 전문 검색이 가능하고 Analyzer로 텍스트를 분석합니다. Bool Query로 복잡한 조건을 조합하고 Aggregation으로 집계 분석합니다. Mapping을 잘 정의하면 검색 품질이 향상됩니다. Kibana로 시각화하고 Logstash로 데이터를 수집합니다.', 'user2', '개발자2', 3, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''18 days'', NOW() + INTERVAL ''18 days''),

('SQL 쿼리 최적화 기법', '슬로우 쿼리를 개선하는 실전 기법입니다. SELECT *를 피하고 필요한 컬럼만 조회합니다. WHERE 절에 함수를 사용하면 인덱스를 못 타니 주의하세요. EXISTS가 IN보다 빠른 경우가 많고 JOIN 순서도 중요합니다. LIMIT으로 결과를 제한하고 UNION ALL이 UNION보다 빠릅니다. 실행 계획으로 병목을 찾아 개선합니다.', 'admin', '관리자', 1, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''19 days'', NOW() + INTERVAL ''19 days''),

('Connection Pool 설정 가이드', 'DB 커넥션 풀 설정은 성능에 직결됩니다. 최소/최대 연결 수를 적절히 설정하고 너무 크면 DB에 부담을 줍니다. Connection Timeout과 Idle Timeout을 조정합니다. HikariCP가 가장 빠르며 설정이 간단합니다. 모니터링으로 실제 사용량을 파악하고 조정하세요. 연결이 부족하면 병목이 되고 너무 많으면 자원 낭비입니다.', 'user1', '개발자1', 2, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''20 days'', NOW() + INTERVAL ''20 days''),

('데이터베이스 백업과 복구', '데이터 손실을 방지하는 백업 전략입니다. Full Backup, Incremental Backup, Differential Backup을 조합합니다. Point-in-Time Recovery로 특정 시점으로 복구할 수 있습니다. 백업 검증을 주기적으로 수행하고 복구 절차를 문서화합니다. RTO와 RPO를 정의하여 요구사항을 명확히 합니다. 자동화로 휴먼 에러를 방지합니다.', 'user2', '개발자2', 3, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''21 days'', NOW() + INTERVAL ''21 days''),

('GraphQL과 DataLoader 패턴', 'GraphQL의 N+1 문제를 DataLoader로 해결합니다. DataLoader는 요청을 배칭하고 캐싱합니다. 같은 리퀘스트 내에서 중복 요청을 제거하고 단일 배치로 묶어 실행합니다. Prime 메서드로 캐시를 미리 채울 수 있습니다. Resolver 레벨에서 적용하며 성능이 크게 향상됩니다. 복잡한 쿼리에서 진가를 발휘합니다.', 'admin', '관리자', 1, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''22 days'', NOW() + INTERVAL ''22 days''),

('시계열 데이터베이스 선택 가이드', '시계열 데이터는 특수한 DB가 효율적입니다. InfluxDB는 쓰기 성능이 뛰어나고 압축률이 높습니다. TimescaleDB는 PostgreSQL 기반이라 익숙합니다. Prometheus는 모니터링에 특화되어 있습니다. Retention Policy로 오래된 데이터를 자동 삭제하고 Down Sampling으로 정밀도를 조절합니다. 쓰기가 많고 읽기가 적은 특성에 최적화되어 있습니다.', 'user1', '개발자1', 2, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''23 days'', NOW() + INTERVAL ''23 days''),

('데이터베이스 모니터링 전략', '데이터베이스 상태를 실시간으로 모니터링합니다. Slow Query Log로 느린 쿼리를 찾고 Connection Count로 연결 상태를 확인합니다. Replication Lag을 모니터링하고 Disk I/O와 CPU 사용률을 추적합니다. 임계값을 설정하여 알림을 받고 대시보드로 한눈에 파악합니다. Prometheus와 Grafana 조합이 강력합니다.', 'user2', '개발자2', 3, 4, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''24 days'', NOW() + INTERVAL ''24 days''),

-- DevOps/인프라 (76-90)
('Docker 멀티 스테이지 빌드', '멀티 스테이지 빌드로 이미지 크기를 90% 줄였습니다. 빌드와 런타임 스테이지를 분리하고 빌드 도구는 최종 이미지에 포함되지 않습니다. --from으로 이전 스테이지의 파일을 복사하고 alpine 베이스로 크기를 최소화합니다. .dockerignore로 불필요한 파일을 제외하세요. 레이어 캐싱을 활용하면 빌드 시간도 단축됩니다.', 'admin', '관리자', 1, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''25 days'', NOW() + INTERVAL ''25 days''),

('Kubernetes Pod 리소스 관리', 'Pod의 리소스 requests와 limits를 적절히 설정합니다. requests는 스케줄링에 사용되고 limits는 최대 사용량을 제한합니다. CPU는 압축 가능하지만 메모리는 비압축이라 OOMKilled될 수 있습니다. Vertical Pod Autoscaler로 자동 조정하고 HPA로 Pod 수를 확장합니다. LimitRange로 기본값을 설정하고 ResourceQuota로 네임스페이스를 제한합니다.', 'user1', '개발자1', 2, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''26 days'', NOW() + INTERVAL ''26 days''),

('CI/CD 파이프라인 최적화', 'GitHub Actions로 효율적인 CI/CD를 구축합니다. 캐싱으로 의존성 설치 시간을 줄이고 병렬 실행으로 전체 시간을 단축합니다. Matrix 전략으로 여러 환경을 테스트하고 조건부 실행으로 불필요한 작업을 건너뜁니다. Secrets로 민감 정보를 관리하고 Environment로 배포를 제어합니다. 실패 시 즉시 알림을 받도록 설정합니다.', 'user2', '개발자2', 3, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''27 days'', NOW() + INTERVAL ''27 days''),

('Terraform 모듈화 전략', 'Terraform 코드를 재사용 가능한 모듈로 구성합니다. 변수로 유연성을 제공하고 출력으로 정보를 전달합니다. Remote State로 상태를 공유하고 Workspace로 환경을 분리합니다. terragrunt로 DRY 원칙을 지키고 모듈 버전을 관리합니다. terraform plan으로 변경사항을 검토하고 안전하게 적용합니다.', 'admin', '관리자', 1, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''28 days'', NOW() + INTERVAL ''28 days''),

('Prometheus와 Grafana 모니터링', 'Pull 방식의 Prometheus로 메트릭을 수집합니다. PromQL로 쿼리하고 AlertManager로 알림을 전송합니다. Grafana로 대시보드를 구성하고 시각화합니다. Exporter로 다양한 시스템의 메트릭을 수집하고 Service Discovery로 동적 타겟을 관리합니다. Recording Rule로 자주 사용하는 쿼리를 미리 계산합니다.', 'user1', '개발자1', 2, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''29 days'', NOW() + INTERVAL ''29 days''),

('Service Mesh with Istio', 'Istio로 마이크로서비스 간 통신을 제어합니다. Traffic Management로 라우팅을 세밀하게 조정하고 Canary 배포를 쉽게 구현합니다. Circuit Breaker로 장애를 격리하고 Retry 정책으로 복원력을 높입니다. mTLS로 서비스 간 통신을 암호화하고 Observability 기능으로 추적합니다. 러닝커브가 높지만 복잡한 네트워크 관리에 강력합니다.', 'user2', '개발자2', 3, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''30 days'', NOW() + INTERVAL ''30 days''),

('AWS Lambda 최적화 기법', 'Lambda 함수의 콜드 스타트를 최소화합니다. 메모리를 늘리면 CPU도 증가하여 빠를 수 있습니다. 초기화 코드를 핸들러 밖으로 빼서 재사용하고 Provisioned Concurrency로 준비된 환경을 유지합니다. 번들 크기를 줄이고 레이어로 공통 의존성을 분리합니다. X-Ray로 성능을 분석하고 병목을 찾습니다.', 'admin', '관리자', 1, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''31 days'', NOW() + INTERVAL ''31 days''),

('GitOps로 인프라 관리', 'Git을 진실의 원천으로 하는 GitOps 방식입니다. ArgoCD나 Flux로 Git 변경을 자동 감지하여 배포합니다. 선언적 정의로 원하는 상태를 기술하고 자동 동기화합니다. 롤백이 간단하며 모든 변경이 Git 히스토리에 남습니다. PR로 변경을 리뷰하고 승인 후 자동 배포됩니다. 감사 추적이 용이하고 재현 가능합니다.', 'user1', '개발자1', 2, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''32 days'', NOW() + INTERVAL ''32 days''),

('로그 수집과 분석 파이프라인', 'ELK/EFK 스택으로 로그를 중앙화합니다. Fluentd나 Filebeat로 로그를 수집하고 Elasticsearch에 저장합니다. Kibana로 검색하고 분석합니다. 구조화된 로그(JSON)를 사용하면 분석이 쉽습니다. Index Lifecycle Management로 오래된 로그를 자동 삭제합니다. 로그 레벨을 적절히 사용하고 민감 정보는 마스킹합니다.', 'user2', '개발자2', 3, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''33 days'', NOW() + INTERVAL ''33 days''),

('Blue-Green 배포 전략', '무중단 배포를 위한 Blue-Green 방식입니다. 새 버전을 별도 환경(Green)에 배포하고 테스트합니다. 트래픽을 한 번에 전환하여 다운타임이 없습니다. 문제 발생 시 즉시 롤백 가능합니다. 두 배의 리소스가 필요하지만 안전성이 높습니다. 로드 밸런서나 DNS로 전환하며 데이터베이스 마이그레이션은 주의가 필요합니다.', 'admin', '관리자', 1, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''34 days'', NOW() + INTERVAL ''34 days''),

('Helm Chart 작성 가이드', 'Kubernetes 패키징 도구인 Helm 활용법입니다. Chart로 애플리케이션을 패키징하고 Values로 설정을 외부화합니다. Template 함수로 조건부 렌더링하고 Named Template으로 재사용합니다. Dependencies로 다른 Chart를 포함하고 Repository로 공유합니다. Hooks로 설치/업그레이드 시 작업을 실행할 수 있습니다.', 'user1', '개발자1', 2, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''35 days'', NOW() + INTERVAL ''35 days''),

('Nginx vs HAProxy 비교', '로드 밸런서 선택 가이드입니다. Nginx는 웹 서버 기능도 있고 간단한 설정이 장점입니다. HAProxy는 L7 로드 밸런싱에 특화되어 있고 헬스체크가 강력합니다. Nginx Plus는 상용이지만 고급 기능을 제공합니다. 둘 다 높은 성능을 보이며 용도에 따라 선택하세요. SSL Termination, Sticky Session 등 필요 기능을 고려합니다.', 'user2', '개발자2', 3, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''36 days'', NOW() + INTERVAL ''36 days''),

('Kubernetes 보안 베스트 프랙티스', 'K8s 클러스터를 안전하게 운영하는 방법입니다. RBAC로 권한을 최소화하고 Pod Security Policy로 제약을 가합니다. Network Policy로 통신을 제한하고 Secrets를 암호화합니다. 컨테이너는 root가 아닌 사용자로 실행하고 이미지 스캔으로 취약점을 찾습니다. Admission Controller로 정책을 강제하고 감사 로그를 활성화합니다.', 'admin', '관리자', 1, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''37 days'', NOW() + INTERVAL ''37 days''),

('Infrastructure as Code 패턴', 'IaC로 인프라를 코드로 관리합니다. 버전 관리로 변경 이력을 추적하고 코드 리뷰로 품질을 높입니다. Immutable Infrastructure로 변경 대신 교체합니다. 모듈화로 재사용성을 높이고 테스트를 자동화합니다. Terraform, CloudFormation, Pulumi 등 도구를 선택하고 환경별로 변수를 분리합니다. 드리프트 감지로 불일치를 찾아냅니다.', 'user1', '개발자1', 2, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''38 days'', NOW() + INTERVAL ''38 days''),

('gRPC 마이크로서비스 통신', 'HTTP/2 기반의 고성능 RPC인 gRPC 활용법입니다. Protocol Buffers로 스키마를 정의하고 강타입 통신을 보장합니다. 양방향 스트리밍이 가능하고 바이너리 프로토콜로 빠릅니다. Service Mesh와 잘 통합되고 다중 언어를 지원합니다. REST보다 빠르지만 브라우저 지원은 제한적입니다. gRPC-Web이나 Envoy로 해결 가능합니다.', 'user2', '개발자2', 3, 5, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''39 days'', NOW() + INTERVAL ''39 days''),

-- 알고리즘/자료구조 (91-100)
('해시 테이블 구현과 충돌 해결', '해시 테이블의 핵심은 해시 함수와 충돌 해결입니다. Chaining은 연결 리스트로 충돌을 처리하고 Open Addressing은 다른 빈 슬롯을 찾습니다. Load Factor가 0.75를 넘으면 재해싱으로 크기를 늘립니다. 좋은 해시 함수는 균등 분포를 만들고 빠르게 계산됩니다. Java HashMap은 Chaining과 Red-Black Tree를 사용합니다.', 'admin', '관리자', 1, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''40 days'', NOW() + INTERVAL ''40 days''),

('이진 탐색 트리 vs AVL 트리', 'BST는 간단하지만 불균형해질 수 있습니다. AVL 트리는 자가 균형으로 항상 O(log n)을 보장합니다. 삽입 시 회전 연산으로 균형을 유지하고 높이 차이가 1을 넘지 않습니다. Red-Black Tree는 덜 엄격하지만 실무에서 더 많이 사용됩니다. TreeMap이 RB Tree 기반이며 정렬된 순회가 가능합니다.', 'user1', '개발자1', 2, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''41 days'', NOW() + INTERVAL ''41 days''),

('동적 계획법 완벽 마스터', 'DP는 중복 부분 문제를 메모이제이션으로 해결합니다. Top-down은 재귀와 메모를, Bottom-up은 반복문을 사용합니다. 피보나치, LCS, 배낭 문제가 대표적입니다. 상태 정의와 점화식 도출이 핵심이며 최적 부분 구조를 가져야 합니다. 공간 최적화로 메모리를 줄일 수 있고 실전에서는 캐싱 전략으로 응용됩니다.', 'user2', '개발자2', 3, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''42 days'', NOW() + INTERVAL ''42 days''),

('그래프 탐색 알고리즘 비교', 'DFS는 스택이나 재귀로, BFS는 큐로 구현합니다. DFS는 경로 탐색에, BFS는 최단 거리에 유리합니다. 위상 정렬은 DFS로, 최단 경로는 BFS로 해결합니다. Dijkstra는 우선순위 큐로 가중치 그래프의 최단 경로를 찾고 Bellman-Ford는 음수 가중치도 다룹니다. Floyd-Warshall로 모든 쌍 최단 경로를 구합니다.', 'admin', '관리자', 1, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''43 days'', NOW() + INTERVAL ''43 days''),

('정렬 알고리즘 성능 비교', 'Quick Sort는 평균 O(n log n)이지만 최악은 O(n²)입니다. Merge Sort는 항상 O(n log n)이고 안정적이지만 추가 공간이 필요합니다. Heap Sort는 제자리 정렬이고 Tim Sort는 실전에서 가장 빠릅니다. Python과 Java의 기본 정렬이 Tim Sort이며 작은 데이터는 Insertion Sort가 빠를 수 있습니다.', 'user1', '개발자1', 2, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''44 days'', NOW() + INTERVAL ''44 days''),

('Union-Find 자료구조 활용', 'Disjoint Set으로 집합을 효율적으로 관리합니다. Path Compression으로 트리 높이를 줄이고 Union by Rank로 균형을 유지합니다. Kruskal 알고리즘에서 사이클 검사에 사용되고 네트워크 연결성 확인에 유용합니다. 거의 상수 시간에 동작하며 구현이 간단합니다. 동적 연결성 문제의 표준 해법입니다.', 'user2', '개발자2', 3, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''45 days'', NOW() + INTERVAL ''45 days''),

('트라이(Trie) 자료구조 구현', '문자열 검색에 특화된 트리 구조입니다. 각 노드가 문자를 저장하고 경로가 단어를 형성합니다. 자동완성, 사전, 스펠 체커에 활용되며 접두사 검색이 빠릅니다. 공간 복잡도가 높지만 검색은 O(m)으로 빠릅니다. Compressed Trie로 공간을 절약하고 Ternary Search Tree도 대안입니다.', 'admin', '관리자', 1, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''46 days'', NOW() + INTERVAL ''46 days''),

('세그먼트 트리로 구간 쿼리', '구간 합, 최소값 등을 빠르게 구하는 세그먼트 트리입니다. 빌드는 O(n), 쿼리와 업데이트는 O(log n)입니다. 완전 이진 트리로 구현하고 Lazy Propagation으로 구간 업데이트를 최적화합니다. 펜윅 트리가 더 간단하지만 세그먼트 트리가 더 범용적입니다. 온라인 쿼리 문제에 강력한 도구입니다.', 'user1', '개발자1', 2, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''47 days'', NOW() + INTERVAL ''47 days''),

('KMP 문자열 매칭 알고리즘', '문자열 패턴 매칭을 O(n+m)에 해결하는 KMP 알고리즘입니다. 실패 함수를 전처리하여 불필요한 비교를 건너뜁니다. 접두사와 접미사의 최대 일치 길이를 저장하고 패턴 이동 거리를 결정합니다. Boyer-Moore는 실전에서 더 빠르고 Rabin-Karp는 여러 패턴 검색에 유리합니다.', 'user2', '개발자2', 3, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''48 days'', NOW() + INTERVAL ''48 days''),

('LRU 캐시 구현하기', 'Least Recently Used 캐시를 HashMap과 Doubly Linked List로 구현합니다. HashMap으로 O(1) 접근하고 List로 순서를 관리합니다. 접근 시 노드를 맨 앞으로 이동하고 용량 초과 시 맨 뒤를 제거합니다. LinkedHashMap을 사용하면 더 간단하게 구현 가능합니다. Redis나 Memcached의 기본 전략이며 실무에서 자주 사용됩니다.', 'admin', '관리자', 1, 6, 'PUBLIC', false, false, false, 0, 0, 0, NOW() + INTERVAL ''49 days'', NOW() + INTERVAL ''49 days'');

-- 모든 게시글에 대한 통계 업데이트 (선택사항)
-- UPDATE board SET view_count = FLOOR(RANDOM() * 100), like_count = FLOOR(RANDOM() * 20)
-- WHERE created_at >= NOW() - INTERVAL ''50 days'';
