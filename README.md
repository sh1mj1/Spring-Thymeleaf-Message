# Spring-Thymeleaf-Message


# 1. 프로젝트 설정

이전 프로젝트에 이어서 메시지, 국제화 기능을 학습해봅니다.

스프링 MVC 1편에서 학습했던 예제를 그대로 사용합니다.

참고로 메시지, 국제화 예제에 집중하기 위해서 복잡한 체크, 셀렉트 박스 관리 기능은 제거했습니다.


# 2. 메시지, 국제화 소개

### 메시지

만약에 개발이 거의 모두 진행된 상황에서 기획자가 화면에 보이는 문구가 마음에 들지 않는다고, 상품명이라는 단어를 모두 상품이름으로 고쳐달라고 하면 어떻게 해야 할까요?

여러 화면에 보이는 상품명, 가격, 수량 등, label 에 있는 단어를 변경하려면 다음 화면들을 다 찾아가면서 모두 변경해야 합니다.

지금처럼 화면 수가 적으면 문제가 되지 않지만 화면이 수십개 이상이라면 수십개의 파일을 모두 고쳐야 한다.

왜냐하면 우리의 `addForm.html` , `editForm.html` , `item.html` , `items.html 의`  HTML 파일들에 메시지가 하드코딩 되어있기 때문이다.

이런 다양한 메시지를 한 곳에서 관리하도록 하는 기능을 메시지 기능이라고 한다.

예를 들어서 `messages.properties` 라는 메시지 관리용 파일을 만들고 나서 각 HTML 들은 그 데이터들을 key 값으로 불러서 사용하는 것이다.

`addForm.html`

```html
<label for="itemName" th:text="#{item.itemName}"></label>
```

`editForm.html`

```html
<label for="itemName" th:text="#{item.itemName}"></label>
```

### 국제화

그렇다면 메시지에서 한 발 더 나아가봅시다.

메시지에서 설명한 메시지 파일(`message.properties`) 을 각 나라별로 별도로 관리하면 서비스를 국제화할 수 있습니다.

예를 들어서 다음과 같이 2개의 파일을 만들어서 분류합니다.

`messages_en.properties`

```clojure
item=Item
item.id=Item ID
item.itemName=Item Name
...
```

`messages_ko.properties`

```html
item=상품
item.id=상품 ID
item.itemName=상품명
...
```

영어를 사용하는 사람이면 `messages_en.properties` 을 사용하고 한국어를 사용하는 사람이면 `messages_ko.properties` 을 사용하게 개발하면 된다.

그렇다면 한국어를 사용하는 사람인지 영어를 사용하는 사람인지는 어떻게 알까요??

한국에서 접근한 것인지 영어에서 접근한 것인지는 인식하는 방법은 HTTP `accept-language` 해더 값을 사용합니다. 혹은 사용자가 직접 언어를 선택하도록 하고, 쿠키 등을 사용해서 처리하면 됩니다.

메시지와 국제화 기능을 직접 구현할 수도 있겠지만, 스프링은 기본적인 메시지와 국제화 기능을 모두 제공합니다. 

그리고 타임리프도 스프링이 제공하는 메시지와 국제화 기능을 편리하게 통합해서 제공합니다.

지금부터 스프링이 제공하는 메시지와 국제화 기능을 알아봅니다.

# 3. 스프링 메시지 소스 설정

스프링은 기본적인 메시지 관리 기능을 제공합니다.

메시지 관리 기능을 사용하려면  스프링이 제공하는 `MessageSource` 을 스프링 빈으로 등록하면 되는데, `MessageSource` 는 인터페이스입니다.

따라서 구현체인 `ResourceBundleMessageSource` 을 스프링 빈으로 등록하면 됩니다.

```java
@Bean
public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasenames("messages", "errors");
		messageSource.setDefaultEncoding("utf-8");
	
		return messageSource;
}
```

- `basenames` - 설정 파일의 이름을 지정.
    - `messages` 로 지정하면 `messages.properties` 파일을 읽어서 사용한다.
    - 추가로 국제화 기능을 적용하려면 `messages_en.properties`, `messages_ko.properties` 와 같이 파일명 마지막에 언어 정보를 주면 된다.
    - 만약 찾을 수 있는 국제화 파일이 없으면 `messages.properties` (언어정보가 없는 파일명)를 기본으로 사용한다.
    - 파일의 위치는 `/resources/messages.properties` 에 두면 된다.
    - 여러 파일을 한번에 지정할 수 있다. 여기서는 `messages` , `errors` 둘을 지정했다.
- `defaultEncoding` - 인코딩 정보를 지정한다. `utf-8` 을 사용하면 된다.

그런데 위처럼 일일이 할 필요 없습니다!!

스프링 부트를 사용하면 스프링 부트가 `MessageSource` 를 자동으로 스프링 빈으로 등록한다.

### 스프링 부트 메시지 소스 설정

스프링 부트를 사용하면 다음과 같이 메시지 소스를 설정할 수 있다.

`application.properties`

```java
spring.messages.basename=messages,config.i18n.messages
```

스프링 부트 메시지 소스 기본 값은  `spring.messages.basename=messages` 입니다.

`MessageSource` 를 스프링 빈으로 등록하지 않고, 스프링 부트와 관련된 별도의 설정을 하지 않으면 `messages` 라는 이름으로 기본 등록된다. 

즉, `messages_en.properties` ,`messages_ko.properties` , `messages.properties` 파일만 등록하면 자동으로 인식된다

[https://docs.spring.io/spring-boot/docs/current/reference/html/](https://docs.spring.io/spring-boot/docs/current/reference/html/)

위 링크에서 Application Properties 로 들어가면 관련 내용들을 찾을 수 있다.

### 메시지 파일 만들기

메시지 파일을 만들어보자. 국제화 테스트를 위해서 messages_en 파일도 추가하자.

- messages.properties :기본 값으로 사용(한글)
- messages_en.properties : 영어 국제화 사용

`/resources/messages.properties`

`/resources/messages_en.properties`