# 💪 Real Health

<img width="1881" height="480" alt="Image" src="https://github.com/user-attachments/assets/03b758c0-db58-4461-acba-4a36c74e997e" />

Real Health는 여러분의 "진정한 건강"을 찾아줄 운동 도우미 어플입니다.

## 🔩 주요 기능
### 1. 🗺️ 지도 검색
<img width="1880" height="480" alt="Image" src="https://github.com/user-attachments/assets/b551f9ed-ec94-4f0b-a5ea-ef6ca3109788" />

**1. 현위치 기반 헬스장 검색**
- 현재 위치 또는 현재 지도 기준 반경 2km 내 헬스장 검색 및 마커 표시
- 하단의 목록 버튼 클릭 시, 헬스장 목록 확인 가능
    - 거리 정보 / 별점 정보 확인 및 거리순 / 별점순 정렬 가능

**2. 헬스장 상세 정보 확인**
- 별점 / 주소 / 전화번호 / 영업시간 / 사진 확인 가능

**3. 즐겨찾기 기능**
- 헬스장 상세 정보 페이지 / 헬스장 목록 페이지에서 즐겨찾기 등록 가능
- 메인 페이지에서 오른쪽 상단의 별 버튼 클릭 시, 즐겨찾기 등록된 장소 표시


### 2. 📷 피드 관리
<img width="1880" height="480" alt="Image" src="https://github.com/user-attachments/assets/2ff72df2-71b4-4512-b2f8-6af7e4a2bae8" />

**1. 사진 업로드 기능**
- 날짜마다 5개까지 사진 등록 가능
- 사진들은 날짜 순으로 정렬됨

**2. 하루 코멘트 작성 가능**
- 태그 기능을 통해 그날의 한마디를 남길 수 있음
- 운동 기록 탭과 연계되어 해당 날짜에 어떤 운동을 했는지 확인 가능

**3. 날짜 검색 기능**
- 해당 날짜의 사진들을 검색해서 확인 가능

### 3. 🗓️ 기록 정리
<img width="1880" height="480" alt="Image" src="https://github.com/user-attachments/assets/cdb7acda-3665-4859-8134-4a3b6fc321af" />

**1. 운동 기록 가능**
- 캘린더 - 연도별, 월별, 일자별로 상세하게 날짜 선택 가능
- https://www.jefit.com/exercises 에서 크롤링하여 구축한 운동 데이터로 1300여 가지의 운동 선택 가능
- 기구/부위 별로 분류되어 운동 찾기에 용이함, 운동 이름으로 검색 기능도 제공

**2. 운동 추천 기능**
- 당일 등록된 운동 기능이 없는 경우, 오른쪽 하단의 버튼을 클릭 시 openai를 활용해 자동으로 운동 추천


## 📱 화면 구성
1. 지도 탭: 지도 검색 기능을 제공하는 화면입니다.
2. 피드 탭: 피드 관리 기능을 제공하는 화면입니다.
3. 기록 탭: 기록 정리 기능을 제공하는 화면입니다.


## 🔧 기술 스택
- Kotlin (Jetpack Compose)
- Places API
- Maps SDK for Android
