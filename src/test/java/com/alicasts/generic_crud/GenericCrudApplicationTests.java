package com.alicasts.generic_crud;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class GenericCrudApplicationTests {

	@Test
	void contextLoads() {
	}

}
