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
        long id = userRepository.save(userMapper.toUser(userDto)).getId();
        userRepository.save(userMapper.toUser(updateUserDto));
        userDto.setEmail("smity@yandex.ru");
        UserDto updateUserDto = userService.updateUser(id, userDto);

        assertThat("Проверка, что id не равен null", updateUserDto.getId(), notNullValue());
        assertThat("Проверка на совпадения id пользователя", updateUserDto.getId(), equalTo(id));
        assertThat("Проверка на обновление email пользователя", id, not(equalTo(updateUserDto.getEmail())));
        assertThat("Проверка на совпадения имени пользователя", updateUserDto.getName(), equalTo(userDto.getName()));

        userRepository.save(userMapper.toUser(updateUserDto));
        updateUserDto.setEmail("Smith@yandex.ru");

        assertThrows(ConflictException.class, () -> userService.updateUser(id, updateUserDto),
                "Должно выброситься исключение при обновлении email пользователя. " +
                        "Такой email имеется в базе данных");
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