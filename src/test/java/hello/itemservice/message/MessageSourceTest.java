package hello.itemservice.message;

import org.apache.tomcat.jni.Local;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;

import java.util.Locale;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class MessageSourceTest {

    @Autowired
    MessageSource ms;

    @Test
    void helloMessage(){

        Locale.setDefault(Locale.KOREA);

        String result = ms.getMessage("hello", null, null);

        System.out.println(Locale.getDefault());
        assertThat(result).isEqualTo("안녕");
    }

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

    @Test
    void argumentMessage() {
        Locale.setDefault(Locale.KOREA);
        String result = ms.getMessage("hello.name", new Object[]{"Spring"}, null);
        assertThat(result).isEqualTo("안녕 Spring");
    }

    @Test
    void defaultLang(){
        Locale.setDefault(Locale.KOREA);
        assertThat(ms.getMessage("hello", null, null)).isEqualTo("안녕");
        assertThat(ms.getMessage("hello", null, Locale.KOREA)).isEqualTo("안녕");
    }

    @Test
    void enLang(){
        Locale.setDefault(Locale.KOREA);
        assertThat(ms.getMessage("hello", null, Locale.ENGLISH)).isEqualTo("hello");
    }

}
