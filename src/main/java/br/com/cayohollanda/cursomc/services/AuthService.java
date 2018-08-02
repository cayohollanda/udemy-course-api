package br.com.cayohollanda.cursomc.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.cayohollanda.cursomc.domain.Cliente;
import br.com.cayohollanda.cursomc.repositories.ClienteRepository;
import br.com.cayohollanda.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class AuthService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private EmailService emailService;
	
	private Random rand = new Random();
	
	public void sendNewPassword(String email) {
		Cliente cli = this.clienteRepository.findByEmail(email);
		
		if(cli == null) {
			throw new ObjectNotFoundException("E-mail não encontrado");
		}
		
		String newPass = newPassword();
		cli.setSenha(pe.encode(newPass));
		
		clienteRepository.save(cli);
		emailService.sendNewPassword(cli, newPass);
	}

	private String newPassword() {

		char[] vet = new char[10];
		for(int i = 0; i < 10; i++) {
			vet[i] = randomChar();
		}
		
		return new String(vet);
	}

	private char randomChar() {
		int opt = rand.nextInt(3);
		if(opt == 0) { // gera um digito
			return (char) (rand.nextInt(10) + 48);
		} else if(opt == 1) { // gera letra maiúscula
			return (char) (rand.nextInt(26) + 65);
		} else { // gera letra minúscula
			return (char) (rand.nextInt(26) + 97);
		}
	}
	
}
