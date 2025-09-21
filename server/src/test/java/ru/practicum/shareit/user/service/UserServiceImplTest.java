package ru.practicum.shareit.user.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceImplTest {

    private final EntityManager entityManager;
    private final UserService userService;
    private final UserRepository userRepository;
    private UserDto userDto;
    private final UserMapper userMapper;
    private List<UserDto> userDtoList;
    private UserDto updateUserDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "john@yandex.ru", "John");
        updateUserDto = new UserDto(null, "Smith@yandex.ru", "Smith");
        userDtoList = List.of(userDto, updateUserDto);
    }

    @Test
    void getAllUsers() {
        List<User> users = userDtoList.stream()
                .map(userMapper::toUser)
                .toList();
        List<User> users1 = userRepository.saveAll(users);
        List<UserDto> userDtos = userService.getAllUsers();

        assertThat("Проверка, что список не пустой", userDtos, not(empty()));
        assertThat("Проверка размера списка", userDtos.size(), equalTo(2));
        assertThat("Проверка равенства id", userDtos.getFirst().getId(), equalTo(users1.getFirst().getId()));
        assertThat("Проверка равенства id", userDtos.getLast().getId(), equalTo(users1.getLast().getId()));
        assertThat("Проверка равенства name", userDtos.getFirst().getName(), equalTo(userDto.getName()));
        assertThat("Проверка равенства name", userDtos.getLast().getName(), equalTo(updateUserDto.getName()));
        assertThat("Проверка равенства email", userDtos.getFirst().getEmail(), equalTo(userDto.getEmail()));
        assertThat("Проверка равенства email", userDtos.getLast().getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void saveUser() {
        UserDto save = userService.saveUser(userDto);
        TypedQuery<User> typedQuery = entityManager.createQuery("Select u FROM User u WHERE u.email = :email",
                User.class);
        User user = typedQuery.setParameter("email", userDto.getEmail()).getSingleResult();

        assertThat("Проверка, что id не равен null", user.getId(), notNullValue());
        assertThat("Проверка на равенство id пользователя", user.getId(), equalTo(save.getId()));
        assertThat("Проверка на равенство email пользователя", user.getEmail(), equalTo(userDto.getEmail()));
        assertThat("Проверка на равенство имени пользователя", user.getName(), equalTo(userDto.getName()));
        assertThrows(ConflictException.class, () -> userService.saveUser(userDto),
                "Должно выброситься исключение при добавлении пользователя в методе saveUser с таким же email");
    }

    @Test
    void updateUser() {
        User userSave = userRepository.save(userMapper.toUser(userDto));
        User userUpdate = userRepository.save(userMapper.toUser(updateUserDto));
        long id = userSave.getId();
        String name = userSave.getName();
        String email = userSave.getEmail();
        UserDto updateUserDto = userService.updateUser(id, new UserDto(null, "alex@yandex.ru", null));
        assertThat("Проверка, что id не равен null", updateUserDto.getId(), notNullValue());
        assertThat("Проверка на совпадения id пользователя", updateUserDto.getId(), equalTo(id));
        assertThat("Проверка на обновление email пользователя", updateUserDto.getEmail(), not(equalTo(email)));
        assertThat("Проверка на совпадения имени пользователя", updateUserDto.getName(), equalTo(name));

        String emailUpdate = updateUserDto.getEmail();
        String nameUpdate = updateUserDto.getName();
        UserDto updateUserDto2 = userService.updateUser(id, new UserDto(null, null, "alex"));
        assertThat("Проверка, что id не равен null", updateUserDto2.getId(),
                notNullValue());
        assertThat("Проверка на совпадения id пользователя", updateUserDto2.getId(),
                equalTo(id));
        assertThat("Проверка на обновление email пользователя", updateUserDto2.getEmail(),
                equalTo(emailUpdate));
        assertThat("Проверка на совпадения имени пользователя", updateUserDto2.getName(),
                not(equalTo(nameUpdate)));

        String emailUpdate1 = updateUserDto2.getEmail();
        String nameUpdate1 = updateUserDto2.getName();
        UserDto updateUserDto3 = userService.updateUser(id,
                new UserDto(null, "Mock@yandex.ru", "Mock"));
        assertThat("Проверка, что id не равен null", updateUserDto3.getId(), notNullValue());
        assertThat("Проверка на совпадения id пользователя", updateUserDto3.getId(), equalTo(id));
        assertThat("Проверка на обновление email пользователя", updateUserDto3.getEmail(),
                not(equalTo(emailUpdate1)));
        assertThat("Проверка на совпадения имени пользователя", updateUserDto3.getName(),
                not(equalTo(nameUpdate1)));

        assertThrows(ConflictException.class, () -> userService.updateUser(userUpdate.getId(),
                        new UserDto(null, "Mock@yandex.ru", null)),
                "Должно выброситься исключение при обновлении email пользователя. " +
                        "Такой email имеется в базе данных");

        String emailUpdate2 = updateUserDto3.getEmail();
        String nameUpdate2 = updateUserDto3.getName();
        UserDto updateUserDto4 = userService.updateUser(id,
                new UserDto(null, "alex@yandex.ru", ""));
        assertThat("Проверка, что id не равен null", updateUserDto4.getId(), notNullValue());
        assertThat("Проверка на совпадения id пользователя", updateUserDto4.getId(), equalTo(id));
        assertThat("Проверка на обновление email пользователя", updateUserDto4.getEmail(),
                not(equalTo(emailUpdate2)));
        assertThat("Проверка на совпадения имени пользователя", updateUserDto4.getName(),
                equalTo(nameUpdate2));

        String emailUpdate3 = updateUserDto4.getEmail();
        String nameUpdate3 = updateUserDto4.getName();
        UserDto updateUserDto5 = userService.updateUser(id,
                new UserDto(null, "", "pasha"));
        assertThat("Проверка, что id не равен null", updateUserDto5.getId(), notNullValue());
        assertThat("Проверка на совпадения id пользователя", updateUserDto5.getId(), equalTo(id));
        assertThat("Проверка на обновление email пользователя", updateUserDto5.getEmail(),
                equalTo(emailUpdate3));
        assertThat("Проверка на совпадения имени пользователя", updateUserDto5.getName(),
                not(equalTo(nameUpdate3)));

        String emailUpdate4 = updateUserDto4.getEmail();
        String nameUpdate4 = updateUserDto4.getName();
        UserDto updateUserDto6 = userService.updateUser(id,
                new UserDto(null, emailUpdate4, "pasha"));
        assertThat("Проверка, что id не равен null", updateUserDto6.getId(), notNullValue());
        assertThat("Проверка на совпадения id пользователя", updateUserDto6.getId(), equalTo(id));
        assertThat("Проверка на обновление email пользователя", updateUserDto6.getEmail(),
                equalTo(emailUpdate4));
        assertThat("Проверка на совпадения имени пользователя", updateUserDto6.getName(),
                not(equalTo(nameUpdate4)));
    }

    @Test
    void deleteUser() {
        User user1 = userRepository.save(userMapper.toUser(userDto));
        User user2 = userRepository.save(userMapper.toUser(updateUserDto));
        userService.deleteUser(user2.getId());

        TypedQuery<User> typedQuery = entityManager.createQuery("Select u FROM User u",
                User.class);
        List<User> users = typedQuery.getResultList();

        assertThat("Проверка, что список не пустой", users, not(empty()));
        assertThat("Проверка размера list<UserDto>", users.size(), equalTo(1));
        assertThat("Проверка равенства id", users.getFirst().getId(), equalTo(user1.getId()));
        assertThat("Проверка равенства name", users.getFirst().getName(), equalTo(user1.getName()));
        assertThat("Проверка равенства email", users.getFirst().getEmail(), equalTo(user1.getEmail()));
    }

    @Test
    void findUserById() {
        User user1 = userRepository.save(userMapper.toUser(userDto));
        UserDto userDto1 = userService.findUserById(user1.getId());

        assertThat("Проверка, что id не равен null", userDto1.getId(), notNullValue());
        assertThat("Проверка на совпадения id пользователя", userDto1.getId(), equalTo(user1.getId()));
        assertThat("Проверка на обновление email пользователя", userDto1.getEmail(), equalTo(user1.getEmail()));
        assertThat("Проверка на совпадения имени пользователя", userDto1.getName(), equalTo(user1.getName()));
        assertThrows(NotFoundException.class, () -> userService.findUserById(2L),
                "Должно выброситься исключение при поиске несуществующего пользователя по id");
    }

    @Test
    void returnUserFindById() {
        User userSave = userRepository.save(userMapper.toUser(userDto));
        User userReturn = userService.returnUserFindById(userSave.getId());

        assertThat("Проверка, что id не равен null", userReturn.getId(), notNullValue());
        assertThat("Проверка на совпадения id пользователя", userReturn.getId(), equalTo(userSave.getId()));
        assertThat("Проверка на обновление email пользователя", userReturn.getEmail(),
                equalTo(userSave.getEmail()));
        assertThat("Проверка на совпадения имени пользователя", userReturn.getName(),
                equalTo(userSave.getName()));
        assertThrows(NotFoundException.class, () -> userService.findUserById(2L),
                "Должно выброситься исключение при поиске несуществующего пользователя по id");
    }
}