# 홈포 서버
홈포 서비스를 제공하는 서버입니다. 멀티모듈 - 모놀리틱으로 구성되어 있습니다. 

## 모듈
모듈은 다음과 같은 원칙하에 설계하고 있습니다.

+ 하위 모듈에만 의존성을 가진다.
+ 같은 계층의 모듈과는 의존성을 가지지 않는다.

### main
부팅가능한 어플리케이션입니다. Jackson, Security, Swagger 등 어플리케이션에 필요한 설정을 수행합니다.

### apps
부팅이 불가능하지만 주요 기능으로 세분화된 어플리케이션 레벨 모듈입니다. 추후 서버를 분리한다면 apps의 하위 모듈 단위로 분리할 예정입니다.

다음과 같은 원칙을 가지고 있습니다.

#### user
사용자를 위한 어플리케이션 모듈입니다.

#### employee
관리자를 위한 어플리케이션 모듈입니다.

#### real-estate
부동산 관련 작업을 위한 어플리케이션 모듈입니다.

### domains
도메인에만 집중하는 모듈입니다. 다음과 같은 원칙이 추가로 적용됩니다.

1. 어플리케이션의 로직을 모른다. 
2. 하나의 모듈은 하나의 액터만을 책임진다. 
3. 하나의 모듈은 최대 하나의 인프라에 대한 책임만 갖는다. (출처 : <a href="https://techblog.woowahan.com/2637/">멀티모듈 설계 이야기 with Spring, Gradle</a>)

2번과 3번 원칙을 같이 적용하기 위해서 도메인을 다음과 같이 세분화 했습니다.

+ domain-{actor} - 하나의 액터에 대한 도메인을 Interface로 관리합니다.
+ domain-{actor}-{infra} - 하나의 액터에 대한 도메인을, 하나의 인프라만을 사용해 실제로 구현합니다.

Infra에는 MySQL, Oracle, MongoDB 등이 사용될 수 있습니다. 실제 개발에서는 이러한 구현체를 참조하지 못 하게끔 다음과 같은 컨벤션을 가지고 있습니다.

1. 컴파일 타임에는 interface-module을 활용해, 실제 구현체와의 의존성을 분리한다. (참고 : <a href="https://www.youtube.com/watch?v=nuRcbCfW-YM&list=PLgXGHBqgT2TundZ81MAVHPzeYOTeII69j&index=7">모놀리식에서 점진적 서비스 분리: 사업과제와 병행하여 시스템 개선하기</a>)
2. 실제 구현체는 runtimeOnly로 적용한다.

이를 통해 다른 모듈에서는 실제 구현체를 전혀 몰라도 되고, 동시에 DB 커넥션 풀을 낭비하지 않을 수 있다는 장점이 있었습니다.

#### domain-common
JWT처럼 모든 액터에게 공통으로 적용되어야 하는 도메인을 관리합니다.

#### domain-employee
관리자 액터와 관련있는 도메인을 Interface로 관리합니다.

#### domain-employee-mysql
관리자 액터 도메인을 MySQL를 활용해 구현합니다. 현재 라이브 서버에 제공되는 DB입니다.

#### domain-employee-oracle
관리자 액터 도메인을 Oracle DB를 활용해 구현합니다. 인스턴스 비용 문제로 Oracle Cloud Free Tier로 제공되는 autonomous database를 사용할 예정입니다.

#### domain-real-estate
부동산 시스템 액터와 관련있는 도메인을 Interface로 관리합니다.

#### domain-real-estate-mysql
부동산 시스템 도메인을 MySQL를 활용해 구현합니다. 현재 라이브 서버에 제공되는 DB입니다.

#### domain-real-estate-oracle
부동산 시스템 도메인을 Oracle DB를 활용해 구현합니다. 인스턴스 비용 문제로 Oracle Cloud Free Tier로 제공되는 autonomous database를 사용할 예정입니다.

#### domain-user
사용자 액터와 관련있는 도메인을 Interface로 관리합니다.

#### domain-user-mysql
관리자 액터 도메인을 MySQL를 활용해 구현합니다. 현재 라이브 서버에 제공되는 DB입니다.

#### domain-user-oracle
관리자 액터 도메인을 Oracle DB를 활용해 구현합니다. 인스턴스 비용 문제로 Oracle Cloud Free Tier로 제공되는 autonomous database를 사용할 예정입니다.

### clients
외부 클라이언트와의 소통에 집중하는 모듈입니다. 다음과 같은 원칙을 가지고 있습니다.

+ 서비스 요구사항이나 도메인에 대해 알지 못 합니다.

추후 푸시 알람, 메세지 큐 등을 확장할 예정입니다.

#### kakao-map-client
카카오 맵 API를 사용하기 위한 클라이언트 모듈입니다.

#### public-data-portal-client
공공데이터포털 API를 사용하기 위한 클라이언트 모듈입니다.

#### t-map-client
공공데이터포털 API를 사용하기 위한 클라이언트 모듈입니다.

### core
제일 하위 모듈입니다. 시스템 내 모든 레이어에서 공통으로 사용되어야 할 POJO, Util 등을 모아놓습니다.