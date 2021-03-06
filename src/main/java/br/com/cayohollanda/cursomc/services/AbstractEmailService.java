package br.com.cayohollanda.cursomc.services;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import br.com.cayohollanda.cursomc.domain.Cliente;
import br.com.cayohollanda.cursomc.domain.Pedido;

public abstract class AbstractEmailService implements EmailService {

	@Value("${default.sender}")
	private String sender;
	
//	@Autowired
//	private TemplateEngine templateEngine;
//	
//	@Autowired
//	private JavaMailSender javaMailSender;
	
	@Override
	public void sendOrderConfirmationEmail(Pedido obj) {
		SimpleMailMessage sm = this.prepareSimpleMailMessageFromPedido(obj);
		this.sendEmail(sm);
	}
	
	protected SimpleMailMessage prepareSimpleMailMessageFromPedido(Pedido obj) {
		SimpleMailMessage sm = new SimpleMailMessage();
		
		sm.setTo(obj.getCliente().getEmail());
		sm.setFrom(this.sender);
		sm.setSubject("Pedido confirmado! Código: " + obj.getId());
		sm.setSentDate(new Date(System.currentTimeMillis()));
		sm.setText(obj.toString());
		
		return sm;
	}
	
	@Override
	public void sendNewPassword(Cliente cliente, String newPass) {
		SimpleMailMessage sm = this.prepareNewPasswordEmail(cliente, newPass);
		this.sendEmail(sm);
	}

	protected SimpleMailMessage prepareNewPasswordEmail(Cliente cliente, String newPass) {
		SimpleMailMessage sm = new SimpleMailMessage();
		
		sm.setTo(cliente.getEmail());
		sm.setFrom(this.sender);
		sm.setSubject("Solicitação de nova senha");
		sm.setSentDate(new Date(System.currentTimeMillis()));
		sm.setText("Nova senha: " + newPass);
		
		return sm;
	}
	
//	protected String htmlFromTemplatePedido(Pedido obj) {
//		Context context = new Context();
//		context.setVariable("pedido", obj);
//		
//		return this.templateEngine.process("email/confirmacaoPedido", context);
//	}
//	
//	@Override
//	public void sendOrderConfirmationHtmlEmail(Pedido obj) {
//		try {			
//			MimeMessage mm = this.prepareMimeMessageFromPedido(obj);
//			this.sendHtmlEmail(mm);
//		} catch(MessagingException e) {
//			this.sendOrderConfirmationEmail(obj);
//		}
//	}
//
//	protected MimeMessage prepareMimeMessageFromPedido(Pedido obj) throws MessagingException {
//		MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
//		MimeMessageHelper mmh = new MimeMessageHelper(mimeMessage, true);
//		
//		mmh.setTo(obj.getCliente().getEmail());
//		mmh.setFrom(this.sender);
//		mmh.setSubject("Pedido #" + obj.getId() + " Confirmado");
//		mmh.setSentDate(new Date(System.currentTimeMillis()));
//		mmh.setText(this.htmlFromTemplatePedido(obj), true);
//		
//		return mimeMessage;
//	}
	
}
