# 앱 이름 : WalkingPark (임시) 

## 설명 :
사용자 근처 공원에 대한 자세한 정보를 GoogleMaps에 출력되는 Marker 와 함께 제공하며, 이를 통하여 앱의 운동모드 
추후 소셜기능을 구현한다면, 이에 대한 고민이 조금 더 필요
## 개발 현황 : 
1. Layered 앱 아키텍처 패턴에 맞추어 코드 리팩토링 및 Dagger-Hilt 를 통한 DI 패턴 적용 완료<br/><br/>
2. 레트로핏2 를 통하여 다음의 공공데이터 포털 REST API 연동 미리 정의한 DTO 객체를 토대로 직렬화 수행 완료
    1. <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15084084">기상청 단기예보 조회<a>
    2. <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073861">에어코리아 대기오렴 정보<a>
    3. <a href="https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15073877">에어코리아 측정소 정보<a><br/><br/>
3. 위치 검색 및 지속적인 업데이트 작업을 수행하며 사용자에게 NoTfication 을 통한 UI를 제공할 Foreground Service 작성.<br/><br/>
    1. Activity 에서는 서비스 호출만 담당하며, LocationServiceRepository 에 정의된 비즈니스 로직이 포함된 메서드를 ViewModel 에서 호출하여 LiveData 를 업데이트 하는 방식<br/><br/>
    2. 위치정보 업데이트 등록에 필요한 FusedLocationProviderClient, LocationRequest 객체는 DI 모듈로 정의. LocationCallback 은 MainViewModel 에서 관리할 수 있도록 MainViewModel에서 lazy를 통한 초기화 수행 후 이를 LocationServiceRepository 에 Argument 로 전달하여 위치업데이트 로직을 수행.<br/><br/>
    3. 전국 공원정보 데이터는 <a href="https://www.data.go.kr/data/15012890/standard.do">여기</a>에서 획득하여 assets 를 통하여 Room DB로 전달
        1. Rest Api 형태로도 제공되나, 제공되는 데이터량이 많고 Api 설계상 한계로 인해 효율적으로 데이터를 쿼리하기가 힘듬. 또한 Api 서버 오류가 빈번.
        2. Room DB 를 통한 (latitude between A and B) and (longitude between C and D) 쿼리를 통하여 보다 효율적으로 데이터를 추출하며 위의 문제 해결
    4. RestApi 통신 및 Room DB 작업의 비동기 처리는 ViewModel에서 Repository의 메서드를 호출하는 것으로 수행되고, 이는 코루틴의 suspend 키워드를 통하여 간단하게 구현
4. Activity, Fragment 같은 UI 클래스에서는 1. 서비스 호출, 2. 퍼미션 체크, 3.로딩 다이얼로그의 Show() 및 DIsmiss() 호출 관련 Observer 패턴 등록 이외 가능한 데이터를 보유하지 않고, 비즈니스 로직 또한 수행하지 않도록 설계하였고, 앞으로도 이러한 방향을 Datatingbinding 을 고도화 하여, 구현할 예정  
    
## 앞으로 해야할 일: 
1. UI 에 대한 데이터 처리 비즈니스 로직 미 작성 -> MutableLiveData를 Databinding을 통하여 TextView에 출력하여 데이터 수신여부만 간편하게 확인
2. GoogleMapServiceRepository의 구글맵 관련 비즈니스 로직 로직의 보완 및 고도화 작업 수행 필요. 

## 현재 적용중인 컴포넌트
1. Foreground Service 
    1. Activity 에서는 startForeGroundService()를 통한 서비스 호출과 서비스로부터의 결과를 동적 BroadcaseReceiver 로부터 리턴받는 작업만 담당하며, 서비스에 대한 구체적인 비즈니스 로직은 LocationServiceRepository 에서 관리.
2. Broadcast Receiver (동적 리시버. 서비스로부터 얻어온 결과를 추기적으로 리턴. 알맞은 요청을 다시 RequestCode와 함께 onStartCommand()로 서비스에 전달)

## 현재 적용중인 AAC 컴포넌트
- Room (최초 앱 실행 시, createFromAssets() 를 통하여 미리 준비된 db 파일을 Room 환경에 Migration 하는데 약간의 시간 소요.)
  1. Room DB 를 통한 (latitude between A and B) and (longitude between C and D) 쿼리를 통하여 보다 효율적으로 데이터를 추출하며 위의 문제 해결
  3. 공공데이터 Rest-Api 에서 기상 정보를 얻기 위하여 URI에 제공해야 하는 좌표값을 계산하기 위한 로컬 DB 구축
- ViewModel (액티비티와 프래그먼트간 동일한 MainViewModel 의 자원 공유 및 각 UI 컴포넌트 간 ViewModel 을 별도로 두어, 비즈니스 로직과 UI 로직을 분리)
- LiveData (Observe 패턴 및 Databinding 관련 간단한 로직 구현)

## 현재 적용중인 라이브러리
- Retrofit2 (공공데이터 Rest Api 연동)
- Coroutine (네트워크 및 RoomDB 비동기 처리)
- Google Maps Api (구글맵에서 작동하도록 작성한, 비즈니스 로직을 출력하기 위하여 사용)
- Dagger-hilt (DI)

## 추후 적용 예정 라이브러리 
- Databinding ( 현재는 LivaData 에 대하여 간단한 lambda 를 통해 텍스트뷰에 출력. 추후, 고도화 작업 필요 )
- Firebase Auth (회원 로그인 관련-> 추후 소셜 기능이 추가될 경우.)
- Firebase FireStore (회원 데이터 저장-> 추후 소셜 기능이 추가될 경우)
