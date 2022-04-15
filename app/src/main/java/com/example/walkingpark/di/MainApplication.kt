package com.example.walkingpark.di

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : Application() {
}

/*
    클린 아키텍쳐
    애플리케이션을 구축하기 위하여 확장할 수 있는 강력한 기반을 갖추도록 하는 방법

    UI Class <- ViewModel <- Interactor(UseCase) <- Repository <- DataSource

                    (DataLayer 와 DomainLayer 의 연결점)
    Presenter <-> UseCase <-> Repository <-> Remote Server
        ↑            ↑             |              ↑
        ↓            ↓             |              ↓
      View       Translator        |           Entity
        ↑            ↑             |            AIDL
        ↓            ↓             |         Data Store
      User         Model           |          Services
                                   |
 Presentation  ->  Domain           <- Data


    1. Presentation
     - UI 와 사용자가 시스템과 상호작용하는것에 대하여 다름
    2. Domain Layer
     - Entities, UseCase, Repository Interface 를 포함.
     - 다른 레이어에 대한 의존성을 절대로 갖지 않는다. (Interface 를 통한 추상화된 Repository 를 참조하기 때문)
     - 어플리케이션의 중심이 되는 business rule (Entity) 가 포함.
     - 다른 프레임워크에 의존성을 갖지 않고, 언어에 대해서만 의존성을 갖음.
     - 어플리케이션 로직을 다루며, 모든 비즈니스 로직은 UseCase 형태로 이 계층에 존재. 이 계층이 클린아키텍쳐의 핵심.
     - 데이터를 유지하지 않고 위임만 받는다.
    3. Data Layer
     - Repository Impl - 1 개 이상의 데이터소스를 포함
     - Repository Layer 는 해당 데이터를 가져오는 역할. DataSource 에 속하는 어디서든 데이터를 가져올 수 있다.
     - 또한, 요청에 따라 Domain Layer 에 데이터를 가져올 수 있다.
     - Data Layer 는 어플리케이션에 데이터를 제공하는 계층. 들어오는 모든 데이터는 Repository 에 연결되어 어플리케이션
       요구사항에 맞추어 매핑되어야 한다.

   * 추가 설명
   1. 각 Layer 간 Context 전달의 방법에 관하여....
    ViewModel 에 Context 를 갖는 경우는 생명주기가 연결되어 있어, 문제되지 않으나, UseCase, Repository 에서
    처리할 경우 Leak 가 발생할 수 있으므로, Repository 초기화에 컨텍스트를 전달하고, Domain 에 저장하지 않는 방법으로
    이를 피할 수 있다.
  * 위의 Entity 는 네트워크상의 DTO 의 성격을 갖는 클래스로서, 클린아키텍쳐의 Entity 를 의미하지 않는다.

    UseCase : 소프트웨어가 수행할 핵심적인 기능을 정의하고 구현.
    주로 Entity (비즈니스 룰을 구현한 비즈니스 오브젝트) 라는 개념을 통하여 구현
    -> 결과적으로 이러한 결과를 UI 로 출력하거나, Http 통신으로 응답하는 것으로 수행되며, 이를 위하여
    많은 외부세계와 접촉을 위하여 의존하는 결과가 생긴다. 이를 InterfaceAdapter 를 통하여 느슨하게 결합한 채로
    비즈니스 로직을 바뀔 수 있는 모든것으로 부터 분리한다.
    ex. DB 교체가 일어나야 할 경우, 비즈니스 로직이 바뀌게 되면 이는 나쁜 설계라 본다.

    의존성 규칙. 각각의 레이어가 다른 레이어를 직접 호출하는 대신 의존성을 주입받아서 호출해야 한다.
    -> 인터페이스를 통하여, 가상의 참조 객체를 두어 이를 대상으로 UseCase 가 구현되어야 한다.

    View - 출력할 데이터를 보유한 ViewModel 에서 적절한 렌더링 수행.

    // 클린 아키텍처 상의 Entity 개념은 채택하지 않는다. (위 도표에서의 Entitiy 는 서버 데이터를 parse 하기 위한 DTO 성격의 클래스로 클린 아키텍처의 Entity 개념과 다르다.) 채택하지 않은 이유는 클린 아키텍처의 Entity 는 엔터프라이즈 비즈니스 로직 즉, 최상위 레벨의 정책을 수행하는 계층인데 이러한 정책은 보통 서버레벨에서 수행되기 때문이 아닐까 싶다.

    1. ViewModel vs UseCase 로직의 비교
    1.1 UseCase 의 조건.
      - 업무 요구사항을 담고 있다.
      - 입력, 결과물, 결과물을 생성하기 위한 처리단계를 기술한다.
      - Android 의존성 없이 수행될 수 있는 로직인지 아닌지 (IOS 등의 플랫폼이 변경되더라도 수행된다.)
      - 개발지식이 없는 사람에게 UseCase 를 보여주었을 때, 무슨 일이 수행되는지 알 수 있는지? (추상화가 중요. 세부적인 로직은 필요없음)
      ( ex. 댓글을 작성한다. 댓글 리스트를 불러온다. 댓글을 삭제한다.)
    1.2 UseCase 의 이점
      - 변경의 전파되는것을 방지 : 처음에는 UseCase 가 단순히 Wrapping 클래스 형태일지라도, 추후 변경이 발생할때를 대비하여,
        한 기능을 하는 로직을 별도의 계층으로 격리시킴으로써, 재사용이 용이하며, 변경에 쉽게 대처가 가능해진다.
      - 도메인의 요구사항을 파악하기가 쉽다 : UseCase 만 잘 분리함으로써, 해당 도메인이 무슨일을 수행하는지 쉽게 파악이가능 : 커뮤니케이션 이득.
    1.3 UseCase Rule
      - UseCase 는 Android 와 연관된 의존성이 들어갈 수 없다.
      - UseCase 는 실행할 수 있는 단일 public 메소드만 외부에서 제공받는다.
      - UseCase 는 Repository 를 주입할 때 인터페이스 타입으로 받는다.
    1.4 UseCase in UseCase
      - 각각의 세부작업을 포함한 보다 복잡한 작업 (ex. 댓글쓰기 (이미지 받아오기 + 받은 데이터를 전달하여 댓글을 등록))
      - Low Level UseCase - 단일한 비즈니스 로직을 수행
      - High Level UseCase - Low Level UseCase 를 조합하여 보다 복잡한 비즈니스 로직을 수행.
      - 단, High Level UseCase 가 High Level UseCase 를 참조하지 않도록 한다.
    * DIP 원칙 : 도메인 계층에서는 인터페이스에 의존하고, Data 계층은 인터페이스를 구현하여 의존성을 역전시킨다.



    1. DataSource : 데이터가 생성되는 곳. 클린 아키텍쳐 구조에서 Repository 와 연결되어, 다른 레이어와 통신한다.
    데이터소스를 올바르게 식별해야 효과적인 아키텍쳐 구현이 가능하다.
     - 브로드캐스트 리시버 : 클린 아키텍쳐 관점에서 DataSource 의 한 종류 Repository 와 연결될 수 있다.
     - AIDL : 한 프로세스에 있는 어플리케이션이 다른 어플리케이션과 통신하는 메커니즘 중 하나
     - Network I/O : 소켓 및 스림과 같은 저수준 네트워킹을 통해 데이터를 얻음
     - File IO : 일반적으로 DataSource 로 간주되지는 않으나, 이를 일관되게 처리하여 Repository 와 통신할 수 있으면
       데이터 소스로 간주.
     - Rest/Http Api :
     - SharedPreference : 데이터소스와 가장 먼저 연관되는것은 아니지만, 처리를 함에 일관성을 유지하기 위해 DataSource 로 처리하면 좋음
     - DB/ RealTimeDB :
     - Data from Third-party libraries/SDK : 다양한 UseCase 를 적용할수 있으며, 에플리케이션에서 사용하는 데이터를
       제공하는지 여부에 따라 DataSource 로서 간주될수 있음.
     - Data from StartActivityForResult : DataSource 로 처리하기 위해 매핑하는 과정에서 일관성을 통해 데이터소스로 간주될수 있음
     - Push Notifications : 데이터 소스로 취급하여 이를 효과적으로 처리할 수 있음.
     - 각각의 어플리케이션 간 정적인 데이터를 공유하여 데이터소스로 취급 가능.
*/


