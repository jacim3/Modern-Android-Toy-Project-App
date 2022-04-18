# 앱 이름 : WalkingPark (임시) 

## 미리보기
### - Rest Api - <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084"> 1. 기상청 단기예보 조회<a> <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073861"> 2. 에어코리아 대기오렴 정보<a><a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073877"> 3. 에어코리아 측정소 정보<a>
<img width="230" alt="화면 캡처 2022-04-18 142822" src="https://user-images.githubusercontent.com/60813834/163760073-020b2293-cc9c-4cb6-bff5-1e7499ba776a.png">

### - Google Maps
<img width="230" alt="화면 캡처 2022-04-18 142822" src="https://user-images.githubusercontent.com/60813834/163760648-acfe9591-4014-4463-89b3-62a6577d4d56.gif">


## 설명 :
### FusedLocationProvider 를 통한 GPS 를 통하여 위치정보를 읽어옴으로써, 아래의 DataSource 로 부터 데이터를 제공받아 처리합니다.  
#### 1. Retrofit2 을 통하여 아래의 Api 통신을 수행하고 결과 리턴.
#### 2. 사용자의 좌표 기반으로 RoomDatabase 의 Query를 통한 데이터 검색 후 이를 Google Map 에 출력. 
- Dagger-Hilt 를 통한 의존성 주입 및 클린 아키텍쳐 패턴을 기반으로 작성한 코드를 리팩토링 하였습니다.
<img width="221" alt="화면 캡처 2022-04-18 140857" src="https://user-images.githubusercontent.com/60813834/163760069-fabe6390-475f-4412-af0a-7b134d2227da.png">
- local.properties 파일 내 Google Api Key 및 Public Api Key 가 있어야 앱 실행이 가능합니다.
    
## 앞으로 해야할 일: 
1. 앱의 디자인 및 운동기능 관련 UI 및 기능 설계
2. 비즈니스 로직의 고도화
3. 클린 아키텍쳐에 대한 깊은 스터디

## 현재 적용중인 컴포넌트
1. Foreground Service 
2. Broadcast Receiver

## 현재 적용중인 라이브러리 및 AAC 컴포넌트
- Dagger-hilt
- Retrofit2
- Coroutine
- Google Maps Api
- Room
- ViewModel
- LiveData
