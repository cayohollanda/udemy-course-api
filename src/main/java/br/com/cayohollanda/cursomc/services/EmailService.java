package br.com.cayohollanda.cursomc.services;

import org.springframework.mail.SimpleMailMessage;

import br.com.cayohollanda.cursomc.domain.Pedido;

public interface EmailService {

	void sendOrderConfirmationEmail(Pedido pedido);
	
	void sendEmail(SimpleMailMessage msg);
	
}
