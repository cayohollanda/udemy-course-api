package br.com.cayohollanda.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.cayohollanda.cursomc.domain.ItemPedido;
import br.com.cayohollanda.cursomc.domain.PagamentoComBoleto;
import br.com.cayohollanda.cursomc.domain.Pedido;
import br.com.cayohollanda.cursomc.domain.enums.EstadoPagamento;
import br.com.cayohollanda.cursomc.repositories.ItemPedidoRepository;
import br.com.cayohollanda.cursomc.repositories.PagamentoRepository;
import br.com.cayohollanda.cursomc.repositories.PedidoRepository;
import br.com.cayohollanda.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;
	
	@Autowired
	private ProdutoService produtoService;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteService clienteService;
	
	@Autowired
	private EmailService emailService;
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = this.repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado!"));
	}
	
	@Transactional
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(this.clienteService.find(obj.getCliente().getId()));
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		
		if(obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			this.boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());	
		}
		
		obj = this.repo.save(obj);
		this.pagamentoRepository.save(obj.getPagamento());
		for(ItemPedido ip: obj.getItens()) {
			ip.setDesconto(0.0);
			ip.setProduto(this.produtoService.find(ip.getProduto().getId()));
			ip.setPreco(ip.getProduto().getPreco());
			ip.setPedido(obj);
		}
		
		this.itemPedidoRepository.saveAll(obj.getItens());
		this.emailService.sendOrderConfirmationHtmlEmail(obj);
		return obj;
	}
	
}
