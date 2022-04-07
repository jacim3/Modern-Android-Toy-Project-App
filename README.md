# WalkingPark 
## 개인 작품 (GoogleMaps Api 와 공원정보 Api 를 활용한 산책관련 앱 개발 및 출시 목표)

## * Start App Development
#### 2022.04.02 공공데이터 포털 + 레트로핏2 연동 및 MVVM을 위한 간단한 아키텍쳐 설정
#### 2022.04.03 공공데이터 Rest-Api 의 
# Manifest - meta-data 에 Google Maps Api 키 및 공공데이터 포털 키 필요.

# 주요 컴포넌트
- Foreground Service (사용자 위치 추적 및 이를 노티피케이션으로 알림)
- Broadcast Receiver (동적 리시버. 서버스로부터 얻어온 결과를 추기적으로 액티비티에 전달. 액티비티는 이러한 요청을 따라 알맞은 요청을 다시 onStartCommand()로 서비스에 전달)

# AAC 컴포넌트
- Room (최초 앱 실행시에 스플래시에서 미리 준비된 db 파일을 Room 으로 구축하는데 따른 추가 시간 소요.)
- ViewModel
- LiveData 

# 사용 라이브러리
- Retrofit 2 (공공데이터 Rest Api 연동)
- Coroutine (네트워크 비동기 처리)
- Google Maps Api (사용자 좌표 추적)

# 적용 예정 라이브러리
- Dagger-hilt (DI)
- Databinding 
- Firebase Auth (회원 로그인 관련-> 추후 소셜 기능이 추가될 경우.)
- Firebase FireStore (회원 데이터 저장-> 추후 소셜 기능이 추가될 경우)
