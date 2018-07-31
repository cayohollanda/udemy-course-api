package br.com.cayohollanda.cursomc;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Implementando CommandLineRunner para instanciação ao rodar aplicação.

@SpringBootApplication
public class CursomcApplication implements CommandLineRunner {
	
	public static void main(String[] args) {
		SpringApplication.run(CursomcApplication.class, args);
	}

	// Método que faz a instanciação ao rodar a aplicação
	@Override
	public void run(String... args) throws Exception { }
	
}
