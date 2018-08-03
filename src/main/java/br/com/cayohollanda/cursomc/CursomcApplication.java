package br.com.cayohollanda.cursomc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.cayohollanda.cursomc.services.S3Service;

// Implementando CommandLineRunner para instanciação ao rodar aplicação.

@SpringBootApplication
public class CursomcApplication implements CommandLineRunner {
	
	@Autowired
	private S3Service s3Service;
	
	public static void main(String[] args) {
		SpringApplication.run(CursomcApplication.class, args);
	}

	// Método que faz a instanciação ao rodar a aplicação
	@Override
	public void run(String... args) throws Exception {
		s3Service.uploadFile("C:\\temp\\Koala");
	}
	
}
