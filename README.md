# 앱 이름 : WalkingPark (임시) 


## 설명 : 
- 앱 개발을 위한 본격적인 비즈니스 로직 작성 이전, 앱 아키텍쳐 설계와 데이터를 끌어오기 위한 Api 통신 및 Local DB 설정 부분을 작성하였습니다.
- Api Fragment 에서는 Api 통신을 통해 받아온 데이터를 모두 출력합니다. -> (예정) UI 를 통하여 보여줄 비즈니스 로직 작성
- Maps Fragment 에서는 사용자가 선택한 범위 (거리) 를 원을 통하여 시각적으로 보여주고, 해당 범위내 공원을 지도에 모두 출력합니다.
- (예정) 공원 안에서 일어나는 스토리나 개인에 대한 운동 정보를 기록 및 공유할 수 있는 기능을 추가하고자 합니다.     
- (예정) Settings Fragment 에서는 앱 기능 완성 이후, 설정값으로 제공된 상수값의 제어를 통한 설정을 할 수 있도록 제공할 예정입니디. 

## 구성 :
### FusedLocationProvider 를 통한 GPS 를 통하여 위치정보를 읽어옴으로써, 아래의 DataSource 로 부터 데이터를 제공받아 처리합니다.  
#### 1. Retrofit2 을 통하여 공공데이터 포털 RestApi 관련 비동기 처리후 응답결과를 받아 리턴.
#### 2. 사용자의 좌표 기반으로 RoomDatabase 의 Query를 통한 데이터 검색 후 이를 Google Map 에 출력. 
- 기존의 작성한 코드를 Dagger-Hilt 를 통한 의존성 주입과 클린 아키텍쳐 패턴을 기반으로 리팩토링 하였습니다.
- 앱 실행을 위해서는 local.properties 파일 내 올바른 Google Maps Api Key 및 Public Data Api Key 가 있어야 합니다. 
    
## 미리보기
### - Rest Api - <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084"> 1. 기상청 단기예보 조회<a> <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073861"> 2. 에어코리아 대기오렴 정보<a><a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073877"> 3. 에어코리아 측정소 정보<a>
<img width="230" alt="화면 캡처 2022-04-18 142822" src="https://user-images.githubusercontent.com/60813834/163760073-020b2293-cc9c-4cb6-bff5-1e7499ba776a.png">

### - Google Maps
<img width="230" alt="화면 캡처 2022-04-18 142822" src="https://user-images.githubusercontent.com/60813834/163760648-acfe9591-4014-4463-89b3-62a6577d4d56.gif">


## 적용 Android 라이브러리 
- RxKotlin
- Dagger-hilt
- LiveData
- Room
- Google Maps Api
- Geocoding Api

## 적용 써드파트 라이브러리
- Retrofit2
    
