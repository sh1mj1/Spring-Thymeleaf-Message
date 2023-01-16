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

```java
hello=안녕
hello.name=안녕 {0}
```

`/resources/messages_en.properties`

```java
hello=hello
hello.name=hello {0}
```

# 4. 스프링 메시지 소스 사용

`MessageSource` 인터페이스

```java
public interface MessageSource {
		String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale);
		String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException;
```

`test/java/hello/itemservice/message.MessageSourceTest.java`

```java
package hello.itemservice.message;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource ms;

    @Test
    void helloMessage(){
        String result = ms.getMessage("hello", null, null);
        assertThat(result).isEqualTo("안녕");
    }
}
```

`ms.getMessage("hello", null, null)`

- **code** : hello
- **agrs** : null
- **locale** : null

가장 단순한 테스트로 메시지 코드로 `hello` 를 입력하고 나머지 값은 `null` 을 입력했다. 

`locale` 정보가 없으면 `basename` 에서 설정한 기본 이름 메시지 파일을 조회한다.

`basename` 으로 `messages` 를 지정 했으므로 `messages.properties` 파일에서 데이터 조회한다

일반적으로는 다들 테스트가 성공할 것입니다! 

### 테스트 실패시

제 기본 OS 는 en_Kor 이어서 테스트를 계속 실패하네요…  이 경우에 아래의 절차를 따르면 됩니다.

테스트 전에 `Locale.setDefault(Locale.KOREA)`; 를 추가하면 기본 Locale 을 한시적으로 ko_KR 로 변경할 수 있습니다

그렇게 변경을 하고 테스트를 진행하는데도 **excepted가 ??** 로 뜨면서 테스트를 실패할 수도 있습니다.

그 경우 아래 사진처럼 File Encodings 을 UTF-8 로 설정하고 IntelliJ 을 다시 시작합니다.

![Untitled](https://s3-us-west-2.amazonaws.com/secure.notion-static.com/d3ec3337-24c3-417b-87ad-be83710233df/Untitled.png)

그렇게 했는데도 실패할 수도 있습니다. 그렇다면  아래의 순서로 처리합니다.

Help → "Edit Custom VM options" 를 선택합니다. 그후 아래의 인코딩 옵션을 추가해줍니다.

```java
-Dfile.encoding=UTF-8
-Dconsole.encoding=UTF-8
```

설정 후 File → Invalidate Caches 을 선택하여 캐시를 삭제하고 IntelliJ 을 다시시작합니다.

위 절차에 따르면 모두 해결될 것입니다.

`MessageSourceTest` 추가 - 메시지가 없는 경우, 기본 메시지

```java
@Test
void notFoundMessageCode() {
    Locale.setDefault(Locale.KOREA);
    assertThatThrownBy(() -> ms.getMessage("no_code", null, Locale.KOREA))
            .isInstanceOf(NoSuchMessageException.class);
}

@Test
void notFoundMessageCodeDefaultMessage() {
    Locale.setDefault(Locale.KOREA);
    String result = ms.getMessage("no_code", null, "기본 메시지", null);
    assertThat(result).isEqualTo("기본 메시지");
}
```

- 메시지가 없는 경우에는 `NoSuchMessageException` 이 발생한다.
- 메시지가 없어도 기본 메시지( `defaultMessage` )를 사용하면 기본 메시지가 반환된다.

`MessageSourceTest` 추가 - 매개변수 사용

```java
@Test
void argumentMessage() {
    Locale.setDefault(Locale.KOREA);
    String result = ms.getMessage("hello.name", new Object[]{"Spring"}, null);
    assertThat(result).isEqualTo("안녕 Spring");
}
```

다음 메시지의 {0} 부분은 매개변수를 전달해서 치환할 수 있다.

`hello.name=안녕 {0}` Spring 단어를 매개변수로 전달 `안녕 Spring`

### 국제화 파일 선택

locale 정보를 기반으로 국제화 파일을 선택한다.

- Locale이 `en_US` 의 경우 `messages_en_US` → `messages_en` → `messages` 순서로 찾는다.
- `Locale` 에 맞추어 구체적인 것이 있으면 구체적인 것을 찾고, 없으면 디폴트를 찾는다고 이해하면 된다.

`MessageSourceTest` 추가 - 국제화 파일 선택1

```java
@Test
void defaultLang(){
    Locale.setDefault(Locale.KOREA);
    assertThat(ms.getMessage("hello", null, null)).isEqualTo("안녕");
    assertThat(ms.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
}
```

- `ms.getMessage("hello", null, null)` : locale 정보가 없으므로 `Locale.getDefault()` 을 호출해서 시스템의 기본 로케일을 사용합니다.
    - 예) locale = null 인 경우 시스템 기본 locale 이 ko_KR 이므로 `messages_ko.properties` 조회 시도 조회 실패 `messages.properties` 조회

- `ms.getMessage("hello", null, Locale.KOREA)` : locale 정보가 있지만, `message_ko` 가 없으므로 messages 을 사용

`MessageSourceTest` 추가 - 국제화 파일 선택2

```java
@Test
void enLang(){
    Locale.setDefault(Locale.KOREA);
    assertThat(ms.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
}
```

- `ms.getMessage("hello", null, Locale.ENGLISH)` : locale 정보가 `Locale.ENGLISH` 이므로 `messages_en` 을 찾아서 사용

