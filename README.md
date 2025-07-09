# 💪 Real Health

<img src="https://img.notionusercontent.com/s3/prod-files-secure%2Ff6cb388f-3934-47d6-9928-26d2e10eb0fc%2F80b907a0-d90a-4cdc-bc42-f1034e41f486%2F%EB%85%B8%EC%85%98_%EC%BB%A4%EB%B2%84_%EC%82%AC%EC%A7%84.png/size/w=790?exp=1752050853&sig=oI2pHso_uC54jMEnf6dhyOMGk0kygYfnrx5KhPF3l9o&id=22b5a1b8-3557-8087-992f-dddc08ebcb88&table=block&userId=1f5d872b-594c-81d0-8897-0002305e4db8">

Real Health는 여러분의 "진정한 건강"을 찾아줄 운동 도우미 어플입니다.

## 🔩 주요 기능
### 1. 🗺️ 지도 검색
<img src="https://img.notionusercontent.com/s3/prod-files-secure%2Ff6cb388f-3934-47d6-9928-26d2e10eb0fc%2F8f784e21-1a6f-4f7e-9e39-878802c81d52%2F%ED%83%AD1_%EB%B0%B0%EB%84%88.png/size/w=790?exp=1752051412&sig=uRQHHJWw__1qmCSPy2psCg1Ee4awJ4uNuqGembdtrAk&id=22b5a1b8-3557-80ea-8b04-eded959513ff&table=block&userId=1f5d872b-594c-81d0-8897-0002305e4db8">

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
<img src="https://img.notionusercontent.com/s3/prod-files-secure%2Ff6cb388f-3934-47d6-9928-26d2e10eb0fc%2F709acad8-7eb1-4660-aa0f-143ff08ecd2c%2F%ED%83%AD2_%EB%B0%B0%EB%84%88.png/size/w=790?exp=1752051463&sig=J3xs8rP0waRYzKlTWMJAG9GcUASPSealOQ4flDn8cVo&id=22b5a1b8-3557-80a5-bad8-e5f41d1e58d6&table=block&userId=1f5d872b-594c-81d0-8897-0002305e4db8">

**1. 사진 업로드 기능**
- 날짜마다 5개까지 사진 등록 가능
- 사진들은 날짜 순으로 정렬됨

**2. 하루 코멘트 작성 가능**
- 태그 기능을 통해 그날의 한마디를 남길 수 있음
- 운동 기록 탭과 연계되어 해당 날짜에 어떤 운동을 했는지 확인 가능

**3. 날짜 검색 기능**
- 해당 날짜의 사진들을 검색해서 확인 가능

### 3. 🗓️ 기록 정리
<img src="https://img.notionusercontent.com/s3/prod-files-secure%2Ff6cb388f-3934-47d6-9928-26d2e10eb0fc%2F46d7b28a-cfca-4631-a355-12c7c4c7f67e%2F%ED%83%AD3_%EB%B0%B0%EB%84%88.png/size/w=790?exp=1752051492&sig=1_VqY-WdGWtxz9sP1Zt8M-D0qt_W2lGHeOaw4mUUqDw&id=22b5a1b8-3557-806a-b807-ed8e80735a17&table=block&userId=1f5d872b-594c-81d0-8897-0002305e4db8">

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
