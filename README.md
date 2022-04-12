# 앱 이름 : WalkingPark (임시) 

## 설명 :

## 개발 현황 : 
1. Layered 앱 아키텍처 패턴에 맞추어 코드 리팩토링 및 Dagger-Hilt 를 통한 DI 패턴 적용 완료<br/><br/>
1. 레트로핏2 를 통하여 다음의 공공데이터 포털 REST API 연동 완료
    1. <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084">기상청 단기예보 조회<a>
    2. <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073861">에어코리아 대기오렴 정보<a>
    3. <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073877">에어코리아 측정소 정보<a><br/><br/>
3. 위치 검색 및 지속적인 업데이트 작업을 수행하며 사용자에게 NoTfication 을 통한 UI를 제공할 Foreground Service 작성.<br/><br/>
    1. Activity 에서는 서비스 호출만 담당하며, LocationServiceRepository 에 정의된 비즈니스 로직이 포함된 메서드를 ViewModel 에서 호출하여 LiveData 를 업데이트 하는 방식<br/><br/>
    2. 위치정보 업데이트 등록에 필요한 FusedLocationProviderClient, LocationRequest 객체는 DI 모듈로 정의. LocationCallback 은 ViewModel 에서 관리할 수 있도록 ViewModel에서 lazy를 통한 초기화 수행 후 이를 LocationServiceRepository 에 Argument 로 전달하여 위치업데이트 로직을 수행.<br/><br/>
    3. 전국 공원정보 데이터는 <a href="https://www.data.go.kr/data/15012890/standard.do">여기</a>에서 획득하여 assets 를 통하여 Room DB로 전달
        1. Rest Api 형태로도 제공되나, 제공되는 데이터량이 많아, Api 설계상 한계로 인해 효율적으로 데이터를 쿼리하기가 힘듬. 또한 많은 데이터를 한번에 처리하면 리소스 낭비가 심함
        2. Room DB 를 통한 (latitude between A and B) and (longitude between C and D) 쿼리를 통하여 보다 효율적으로 데이터를 추출할 수 있음.
    4. RestApi 통신 및 Room DB 작업의 비동기 처리는 코루틴의 suspend 키워드를 통하여 간편하게 완료  

## 미흡한 점 : 
    1. 공공데이터의 기상청 단기예보 조회 Api의 경우 http 500
    2. 현재는 TextView or Log 를 통한 데이터 수신여부만 간단하게 체크 ->    
    3. 
  
# 현재 적용중인 컴포넌트
1. Foreground Service 
    1. ㅁㄴㅇㄻㄴㅇㄻㄴㅇㄹ
2. Broadcast Receiver (동적 리시버. 서비스로부터 얻어온 결과를 추기적으로 리턴. 알맞은 요청을 다시 RequestCode와 함께 onStartCommand()로 서비스에 전달)

# 현재 적용중인 AAC 컴포넌트
- Room (스플래시에서 미리 준비된 db 파일을 Room 으로 초기화하는데 따른 별도 시간 소요. (최초 앱 실행시에만))
  1. 전국 공원정보 데이터를 로컬DB로 구축. api와는 별도로 제공되는 파일 데이터가 있고, 이를 이용하여 로컬DB를 구축하면, 쿼리를 사용할 수 있어 아래의 문제를 해결할 것으로 기대.
        1. 제공되는 데이터량이 방대하고, api의 설계상 한계로 디바이스에 부하를 줄이며, 데이터를 검색하기에는 어려움이 따른다 생각 
        2. 공원 정보 Api 에 NullpointerException 과 같은 Api상 오류 발생 확인 
  3. 공공데이터 Rest-Api 에서 기상 정보를 얻기 위하여 URI에 제공해야 하는 좌표값을 계산하기 위한 로컬 DB 구축
- ViewModel
- LiveData 

# 현재 적용중인 라이브러리
- Retrofit 2 (공공데이터 Rest Api 연동)
- Coroutine (네트워크 비동기 처리)
- Google Maps Api (포그라운드 서비스에서 작동하여, 사용자 좌표 추적 후 리턴)
- Dagger-hilt (DI)

# 적용 예정 라이브러리 (현재 미적용)
- Databinding ( 관심사 분리 및 MVVM 적용을 위해 필요)
- Firebase Auth (회원 로그인 관련-> 추후 소셜 기능이 추가될 경우.)
- Firebase FireStore (회원 데이터 저장-> 추후 소셜 기능이 추가될 경우)
