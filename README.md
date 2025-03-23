# 💊 글로벌 의약품 정보 플랫폼 - [어디약]

안녕하세요!  
이 프로젝트는 **글로벌 의약품 정보**를 한국 사용자에게 친숙하게 제공하기 위한 웹 서비스입니다.  
해외 의약품 정보를 번역해 제공하고, 위치 기반으로 약국을 찾을 수 있으며, 커뮤니티를 통해 사용자 간 소통도 지원합니다.

---

## 🔧 주요 기능

- 🌍 **소셜 로그인**: Google, Kakao, Naver를 통한 간편 로그인  
- 💊 **상비약 정보 제공**:
  - 미국 FDA 및 식약청 OpenAPI를 활용해 미국/한국 약 정보 제공
  - 증상별 카테고리로 정리
- 🗺️ **근처 약국 찾기**: Google Maps API를 활용한 위치 기반 검색
- 💬 **커뮤니티**: 사용자 간 정보 공유 게시판
- 🧘‍♂️ **영양제 정보 확인**:
  - 국가별 / 목적별 영양제 데이터
- 외국 데이터는 Google 번역 API로 자동 번역 제공

---

## 🛠️ 사용 기술

### 📌 백엔드
- Java 17, Spring Boot
- JPA, Spring Security, Validation
- MySQL (AWS RDS)
- OAuth2 소셜 로그인 (Google, Kakao, Naver)

### ☁️ 인프라
- AWS EC2 (Ubuntu, Nginx, SSL)
- AWS RDS (MySQL)
- AWS S3 (이미지 저장소)
- GitHub Actions 기반 CI/CD 자동 배포 파이프라인

### 📡 외부 API
- Google Maps API (근처 약국 찾기)
- Google Translation API (영문 정보 → 한글 번역)
- FDA OpenAPI (미국 약 정보)
- 식약처 OpenAPI (한국 약 정보)
- 네이버 쇼핑 OpenAPI (영양제 제품 정보)

  ## 🚀 배포 구조

1. GitHub Actions CI → EC2 서버 자동 배포
2. Nginx + Spring Boot 내장 Tomcat
3. RDS, S3 연동
4. 정적 리소스 S3 제공, API는 Spring에서 제공

---

## 👨‍👩‍👧‍👦 팀 구성

| 이름 | 역할 |
|------|------|
|@https://github.com/joamksh| 인프라 구축 배포, 상비약 정보 |
|@https://github.com/lehojun| 로그인 마이페이지 |
|@https://github.com/heessunny| 커뮤니티 |
|@https://github.com/ggamnunq| 지도,홈화면 |
|@https://github.com/rlawjdgns02| 영양제 |
