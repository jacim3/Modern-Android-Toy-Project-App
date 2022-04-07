# WalkingPark 
- 개인 앱 개발 프로젝트 (GoogleMaps Api 와 공원정보 Api 를 활용한 산책관련 앱 개발 및 출시 목표)

## 설명 :
- 현재는 기획및 설계에 따라 컴포넌트 관련 로직에 집중 중. 이러한 세팅이 완료된 이후, 본격적으로 비즈니스 로직 작성 예정
- 앱 권장 아키텍쳐(3Layer : Presentation, Domain(Optional), Data) 패턴을 준수하려 노력
- ViewModel은 현재 적용중. 추후 DataBinding 도입으로 뷰화 뷰모델을 완전히 분리하는 것으로 MVVM 패턴 완성 예정 
- Google Maps Api 와 공공데이터 포털을 이용하므로, Manifest의 meta-data에 키 등록 필요.

## App Development 
- 2022.04.02 공공데이터 포털 + 레트로핏2 연동 및 MVVM을 위한 간단한 아키텍쳐 설정
- 2022.04.03 기획 수정 + 로컬 DB 연동 및 초기화 관련 로직 작성
- 2022.04.04 기획 수정 + 포그라운드 서비스 로직 작정

# 현재 적용중인 컴포넌트
- Foreground Service (사용자 위치 추적 수행 및 이러한 상황을 노티피케이션으로 알림)
- Broadcast Receiver (동적 리시버. 서버스로부터 얻어온 결과를 추기적으로 액티비티에 전달. 액티비티는 이러한 요청을 따라 알맞은 요청을 다시 onStartCommand()로 서비스에 전달)

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
- Google Maps Api (포그라운드 서비스에서 작동하여, 사용자 좌표 추적 후 액티비티에 전달)

# 적용 예정 라이브러리 (현재 미적용)
- Dagger-hilt (DI)
- Databinding (MVVM 완성을 위해 필요)
- Firebase Auth (회원 로그인 관련-> 추후 소셜 기능이 추가될 경우.)
- Firebase FireStore (회원 데이터 저장-> 추후 소셜 기능이 추가될 경우)
