package com.alicasts.generic_crud;

import org.springframework.boot.SpringApplication;

public class TestGenericCrudApplication {

	public static void main(String[] args) {
		SpringApplication.from(GenericCrudApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
