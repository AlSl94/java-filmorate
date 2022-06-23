package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controllers.FilmController;
import ru.yandex.practicum.filmorate.controllers.UserController;

@SpringBootTest
class FilmorateApplicationTests {
	FilmController filmController = new FilmController();
	UserController userController = new UserController();


	@Test
	void filmBlankNameTest() {
	}

}
