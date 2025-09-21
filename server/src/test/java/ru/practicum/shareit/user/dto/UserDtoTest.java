package ru.practicum.shareit.user.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDtoTest {

    private final JacksonTester<UserDto> json;

    @Test
    void testUserDto() throws Exception {

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john@yandex.ru")
                .build();

        JsonContent<UserDto> jsonContent = json.write(userDto);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("John");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("john@yandex.ru");
    }
}