-- 운영서버용 추가 게시글 50개 (user_id 1-11 범위)

INSERT INTO board (user_id, author, nickname, title, content, status, view_count, like_count, comment_count, is_draft, is_pinned, is_important, created_at, updated_at) VALUES
-- AI/최신 기술 (1-15)
(1, 'kimcoder', '김코더', 'AI 코딩 어시스턴트 실전 활용법 #AI #개발도구', 'GitHub Copilot, Claude 등 AI 코딩 도구를 효과적으로 활용하는 방법을 공유합니다. #AI #개발도구 #생산성', 'PUBLIC', 523, 89, 0, false, false, false, NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
(2, 'leespring', '이스프링', 'ChatGPT API로 챗봇 만들기 #AI #API', 'OpenAI API를 활용한 간단한 챗봇 서비스 개발 과정입니다. #AI #API #프로젝트', 'PUBLIC', 467, 78, 0, false, false, false, NOW() - INTERVAL '14 days', NOW() - INTERVAL '14 days'),
(3, 'parkjs', '박자스', 'Spring AI 프레임워크 소개 #Spring #AI', 'Spring 생태계에서 AI를 활용하는 새로운 방법을 알아봅니다. #Spring #AI #Java', 'PUBLIC', 389, 65, 0, false, false, false, NOW() - INTERVAL '13 days', NOW() - INTERVAL '13 days'),
(4, 'choireact', '최리액트', 'Vector DB 선택 가이드 #DB #AI', 'Pinecone, Weaviate, Chroma 등 벡터 DB를 비교 분석합니다. #DB #AI #벡터', 'PUBLIC', 412, 71, 0, false, false, false, NOW() - INTERVAL '12 days', NOW() - INTERVAL '12 days'),
(5, 'jungdb', '정디비', 'LangChain RAG 시스템 구축 #AI #Python', 'LangChain으로 검색 증강 생성 시스템을 구축하는 방법입니다. #AI #Python #LangChain', 'PUBLIC', 534, 92, 0, false, false, false, NOW() - INTERVAL '11 days', NOW() - INTERVAL '11 days'),
(6, 'kangdevops', '강데브옵스', 'Hugging Face 모델 파인튜닝 #AI #NLP', '사전 학습 모델을 내 데이터로 미세 조정하는 방법입니다. #AI #NLP #파인튜닝', 'PUBLIC', 356, 61, 0, false, false, false, NOW() - INTERVAL '10 days', NOW() - INTERVAL '10 days'),
(7, 'ohbackend', '오백엔드', 'MLOps 파이프라인 구축하기 #MLOps #DevOps', 'ML 모델의 배포와 운영을 자동화하는 방법입니다. #MLOps #DevOps #AI', 'PUBLIC', 445, 76, 0, false, false, false, NOW() - INTERVAL '9 days', NOW() - INTERVAL '9 days'),
(8, 'yoonfull', '윤풀스택', 'LLM 프롬프트 엔지니어링 팁 #AI #프롬프트', '효과적인 프롬프트를 작성하는 실전 기법들입니다. #AI #프롬프트 #LLM', 'PUBLIC', 567, 97, 0, false, false, false, NOW() - INTERVAL '8 days', NOW() - INTERVAL '8 days'),
(9, 'hancloud', '한클라우드', 'Vercel AI SDK로 스트리밍 구현 #AI #React', 'Next.js에서 AI 응답 스트리밍을 구현하는 방법입니다. #AI #React #Vercel', 'PUBLIC', 445, 77, 0, false, false, false, NOW() - INTERVAL '7 days', NOW() - INTERVAL '7 days'),
(1, 'kimcoder', '김코더', 'DALL-E API 이미지 생성 서비스 #AI #프로젝트', 'DALL-E API로 이미지 생성 서비스를 만든 경험입니다. #AI #프로젝트 #DALLE', 'PUBLIC', 534, 91, 0, false, false, false, NOW() - INTERVAL '6 days', NOW() - INTERVAL '6 days'),
(2, 'leespring', '이스프링', 'GPU 클라우드 비용 최적화 #클라우드 #GPU', 'AI 워크로드의 클라우드 비용을 절감하는 방법입니다. #클라우드 #GPU #비용', 'PUBLIC', 389, 67, 0, false, false, false, NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'),
(3, 'parkjs', '박자스', 'TensorFlow Lite 모바일 적용 #TensorFlow #모바일', '모바일 앱에 온디바이스 AI를 적용하는 방법입니다. #TensorFlow #모바일 #AI', 'PUBLIC', 345, 59, 0, false, false, false, NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'),
(4, 'choireact', '최리액트', 'AI 모델 보안 가이드 #AI #보안', 'ML 모델의 보안 취약점과 대응 방법입니다. #AI #보안 #MLSec', 'PUBLIC', 423, 73, 0, false, false, false, NOW() - INTERVAL '3 days', NOW() - INTERVAL '3 days'),
(5, 'jungdb', '정디비', 'Whisper API 음성 인식 구현 #AI #음성', 'Whisper API로 음성 인식 기능을 구현하는 방법입니다. #AI #음성 #Whisper', 'PUBLIC', 389, 67, 0, false, false, false, NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'),

-- 백엔드/아키텍처 (16-30)
(6, 'kangdevops', '강데브옵스', 'Event Sourcing 패턴 이해하기 #아키텍처 #DDD', '이벤트 소싱으로 데이터 변경 이력을 관리하는 방법입니다. #아키텍처 #DDD #이벤트소싱', 'PUBLIC', 378, 65, 0, false, false, false, NOW() - INTERVAL '1 day 20 hours', NOW() - INTERVAL '1 day 20 hours'),
(7, 'ohbackend', '오백엔드', 'CQRS 패턴 실전 적용 #CQRS #아키텍처', '명령과 조회를 분리하여 성능을 개선한 사례입니다. #CQRS #아키텍처 #성능', 'PUBLIC', 312, 54, 0, false, false, false, NOW() - INTERVAL '1 day 16 hours', NOW() - INTERVAL '1 day 16 hours'),
(8, 'yoonfull', '윤풀스택', 'Saga 패턴 분산 트랜잭션 #MSA #Saga', '마이크로서비스 간 트랜잭션을 관리하는 방법입니다. #MSA #Saga #분산시스템', 'PUBLIC', 423, 73, 0, false, false, false, NOW() - INTERVAL '1 day 12 hours', NOW() - INTERVAL '1 day 12 hours'),
(9, 'hancloud', '한클라우드', 'Go 동시성 패턴 마스터 #Go #동시성', 'Go의 goroutine과 channel을 활용한 동시성 패턴입니다. #Go #동시성 #병렬처리', 'PUBLIC', 456, 78, 0, false, false, false, NOW() - INTERVAL '1 day 8 hours', NOW() - INTERVAL '1 day 8 hours'),
(1, 'kimcoder', '김코더', 'Rust 비동기 프로그래밍 #Rust #비동기', 'Tokio로 고성능 비동기 서버를 만드는 방법입니다. #Rust #비동기 #Tokio', 'PUBLIC', 367, 63, 0, false, false, false, NOW() - INTERVAL '1 day 4 hours', NOW() - INTERVAL '1 day 4 hours'),
(2, 'leespring', '이스프링', 'NestJS 심화 가이드 #NestJS #TypeScript', 'NestJS로 엔터프라이즈급 API를 개발하는 방법입니다. #NestJS #TypeScript #백엔드', 'PUBLIC', 445, 77, 0, false, false, false, NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'),
(3, 'parkjs', '박자스', 'Prisma ORM 완벽 가이드 #Prisma #ORM', 'Type-safe ORM으로 DB 작업을 효율화하는 방법입니다. #Prisma #ORM #TypeScript', 'PUBLIC', 389, 67, 0, false, false, false, NOW() - INTERVAL '20 hours', NOW() - INTERVAL '20 hours'),
(4, 'choireact', '최리액트', 'API Gateway 패턴 구현 #MSA #Gateway', 'Kong, APISIX로 API Gateway를 구축하는 방법입니다. #MSA #Gateway #Kong', 'PUBLIC', 356, 61, 0, false, false, false, NOW() - INTERVAL '16 hours', NOW() - INTERVAL '16 hours'),
(5, 'jungdb', '정디비', 'Supabase 백엔드 구축 #Supabase #BaaS', 'Supabase로 빠르게 백엔드를 구축하는 방법입니다. #Supabase #BaaS #PostgreSQL', 'PUBLIC', 445, 77, 0, false, false, false, NOW() - INTERVAL '12 hours', NOW() - INTERVAL '12 hours'),
(6, 'kangdevops', '강데브옵스', 'Temporal 워크플로우 엔진 #Temporal #워크플로우', 'Temporal로 복잡한 비즈니스 로직을 관리하는 방법입니다. #Temporal #워크플로우 #MSA', 'PUBLIC', 334, 58, 0, false, false, false, NOW() - INTERVAL '10 hours', NOW() - INTERVAL '10 hours'),
(7, 'ohbackend', '오백엔드', 'gRPC 마이크로서비스 통신 #gRPC #MSA', 'gRPC로 고성능 서비스 간 통신을 구현하는 방법입니다. #gRPC #MSA #통신', 'PUBLIC', 398, 68, 0, false, false, false, NOW() - INTERVAL '8 hours', NOW() - INTERVAL '8 hours'),
(8, 'yoonfull', '윤풀스택', 'GraphQL Federation 구축 #GraphQL #MSA', 'GraphQL Federation으로 마이크로서비스를 통합하는 방법입니다. #GraphQL #MSA #Federation', 'PUBLIC', 356, 62, 0, false, false, false, NOW() - INTERVAL '6 hours', NOW() - INTERVAL '6 hours'),
(9, 'hancloud', '한클라우드', 'WebSocket 실시간 통신 #WebSocket #실시간', 'WebSocket으로 실시간 채팅을 구현하는 방법입니다. #WebSocket #실시간 #채팅', 'PUBLIC', 423, 73, 0, false, false, false, NOW() - INTERVAL '5 hours', NOW() - INTERVAL '5 hours'),
(1, 'kimcoder', '김코더', 'Redis Pub/Sub 메시징 #Redis #메시징', 'Redis Pub/Sub으로 실시간 이벤트를 처리하는 방법입니다. #Redis #메시징 #실시간', 'PUBLIC', 378, 65, 0, false, false, false, NOW() - INTERVAL '4 hours', NOW() - INTERVAL '4 hours'),
(2, 'leespring', '이스프링', 'Kafka 이벤트 스트리밍 #Kafka #이벤트', 'Kafka로 대용량 이벤트를 처리하는 방법입니다. #Kafka #이벤트 #스트리밍', 'PUBLIC', 445, 77, 0, false, false, false, NOW() - INTERVAL '3 hours', NOW() - INTERVAL '3 hours'),

-- 프론트엔드/DevOps (31-40)
(3, 'parkjs', '박자스', 'React 19 새로운 기능 총정리 #React #프론트엔드', 'React 19의 주요 변경사항과 마이그레이션 가이드입니다. #React #프론트엔드 #React19', 'PUBLIC', 567, 97, 0, false, false, false, NOW() - INTERVAL '2 hours 30 minutes', NOW() - INTERVAL '2 hours 30 minutes'),
(4, 'choireact', '최리액트', 'Next.js 14 App Router 실전 #Next #React', 'App Router로 마이그레이션한 경험을 공유합니다. #Next #React #AppRouter', 'PUBLIC', 489, 84, 0, false, false, false, NOW() - INTERVAL '2 hours', NOW() - INTERVAL '2 hours'),
(5, 'jungdb', '정디비', 'Tailwind CSS v4 새 기능 #Tailwind #CSS', 'Tailwind CSS 최신 버전의 변경사항입니다. #Tailwind #CSS #스타일링', 'PUBLIC', 512, 88, 0, false, false, false, NOW() - INTERVAL '1 hour 45 minutes', NOW() - INTERVAL '1 hour 45 minutes'),
(6, 'kangdevops', '강데브옵스', 'shadcn/ui 컴포넌트 활용 #shadcn #UI', 'Copy & Paste 방식의 UI 컴포넌트 활용법입니다. #shadcn #UI #React', 'PUBLIC', 445, 77, 0, false, false, false, NOW() - INTERVAL '1 hour 30 minutes', NOW() - INTERVAL '1 hour 30 minutes'),
(7, 'ohbackend', '오백엔드', 'Framer Motion 애니메이션 #Framer #애니메이션', 'React에서 부드러운 애니메이션을 구현하는 방법입니다. #Framer #애니메이션 #React', 'PUBLIC', 389, 67, 0, false, false, false, NOW() - INTERVAL '1 hour 15 minutes', NOW() - INTERVAL '1 hour 15 minutes'),
(8, 'yoonfull', '윤풀스택', 'ArgoCD GitOps 배포 자동화 #ArgoCD #GitOps', 'ArgoCD로 쿠버네티스 배포를 자동화하는 방법입니다. #ArgoCD #GitOps #K8s', 'PUBLIC', 389, 67, 0, false, false, false, NOW() - INTERVAL '1 hour', NOW() - INTERVAL '1 hour'),
(9, 'hancloud', '한클라우드', 'Terraform 인프라 코드화 #Terraform #IaC', 'Terraform으로 인프라를 코드로 관리하는 방법입니다. #Terraform #IaC #인프라', 'PUBLIC', 423, 73, 0, false, false, false, NOW() - INTERVAL '50 minutes', NOW() - INTERVAL '50 minutes'),
(1, 'kimcoder', '김코더', 'GitHub Actions CI/CD 구축 #CICD #GitHub', 'GitHub Actions로 자동 배포 파이프라인을 구축하는 방법입니다. #CICD #GitHub #자동화', 'PUBLIC', 456, 79, 0, false, false, false, NOW() - INTERVAL '40 minutes', NOW() - INTERVAL '40 minutes'),
(2, 'leespring', '이스프링', 'Docker 멀티스테이지 빌드 #Docker #최적화', 'Docker 이미지 크기를 최소화하는 빌드 전략입니다. #Docker #최적화 #컨테이너', 'PUBLIC', 378, 65, 0, false, false, false, NOW() - INTERVAL '30 minutes', NOW() - INTERVAL '30 minutes'),
(3, 'parkjs', '박자스', 'K8s HPA 오토스케일링 #K8s #오토스케일링', 'Horizontal Pod Autoscaler로 자동 확장하는 방법입니다. #K8s #오토스케일링 #스케일링', 'PUBLIC', 345, 60, 0, false, false, false, NOW() - INTERVAL '20 minutes', NOW() - INTERVAL '20 minutes'),

-- 커리어/개발자 라이프 (41-50)
(4, 'choireact', '최리액트', '시니어 개발자 성장 로드맵 #커리어 #성장', '주니어에서 시니어로 성장하며 배운 점들입니다. #커리어 #성장 #개발자', 'PUBLIC', 678, 116, 0, false, false, false, NOW() - INTERVAL '15 minutes', NOW() - INTERVAL '15 minutes'),
(5, 'jungdb', '정디비', '개발자 이직 협상 노하우 #이직 #연봉', '이직 시 연봉 협상에서 배운 실전 노하우입니다. #이직 #연봉 #협상', 'PUBLIC', 789, 135, 0, false, false, false, NOW() - INTERVAL '12 minutes', NOW() - INTERVAL '12 minutes'),
(6, 'kangdevops', '강데브옵스', '기술 면접 완벽 준비 가이드 #면접 #취업', '기술 면접을 체계적으로 준비하는 방법입니다. #면접 #취업 #준비', 'PUBLIC', 567, 97, 0, false, false, false, NOW() - INTERVAL '10 minutes', NOW() - INTERVAL '10 minutes'),
(7, 'ohbackend', '오백엔드', '개발자 번아웃 예방과 극복 #번아웃 #멘탈', '번아웃을 미리 예방하고 관리하는 방법입니다. #번아웃 #멘탈 #건강', 'PUBLIC', 623, 107, 0, false, false, false, NOW() - INTERVAL '8 minutes', NOW() - INTERVAL '8 minutes'),
(8, 'yoonfull', '윤풀스택', '사이드 프로젝트 수익화 전략 #사이드프로젝트 #수익', '사이드 프로젝트를 수익화한 경험을 공유합니다. #사이드프로젝트 #수익 #창업', 'PUBLIC', 734, 126, 0, false, false, false, NOW() - INTERVAL '6 minutes', NOW() - INTERVAL '6 minutes'),
(9, 'hancloud', '한클라우드', '오픈소스 기여 시작하기 #오픈소스 #기여', '처음으로 오픈소스에 PR을 보낸 경험을 공유합니다. #오픈소스 #기여 #커뮤니티', 'PUBLIC', 445, 77, 0, false, false, false, NOW() - INTERVAL '5 minutes', NOW() - INTERVAL '5 minutes'),
(1, 'kimcoder', '김코더', '개발 블로그 성장 비결 #블로그 #브랜딩', '기술 블로그 방문자를 늘린 전략을 공유합니다. #블로그 #브랜딩 #마케팅', 'PUBLIC', 512, 88, 0, false, false, false, NOW() - INTERVAL '4 minutes', NOW() - INTERVAL '4 minutes'),
(2, 'leespring', '이스프링', '해외 취업 준비 가이드 #해외취업 #글로벌', '미국 테크 기업 취업 준비 과정을 공유합니다. #해외취업 #글로벌 #미국', 'PUBLIC', 612, 105, 0, false, false, false, NOW() - INTERVAL '3 minutes', NOW() - INTERVAL '3 minutes'),
(3, 'parkjs', '박자스', '효율적인 기술 학습 방법론 #학습 #공부', '새로운 기술을 빠르게 습득하는 방법입니다. #학습 #공부 #방법론', 'PUBLIC', 489, 84, 0, false, false, false, NOW() - INTERVAL '2 minutes', NOW() - INTERVAL '2 minutes'),
(4, 'choireact', '최리액트', '2026년 개발자 트렌드 전망 #트렌드 #전망', '2026년 개발자 시장과 기술 트렌드를 전망합니다. #트렌드 #전망 #2026', 'PUBLIC', 723, 124, 0, false, false, false, NOW(), NOW());
