package br.com.cayohollanda.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	public Pedido find(Integer id) {
		Optional<Pedido> obj = this.repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado!"));
	}
	
	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
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
			ip.setPreco(this.produtoService.find(ip.getProduto().getId()).getPreco());
			ip.setPedido(obj);
		}
		
		this.itemPedidoRepository.saveAll(obj.getItens());
		
		return obj;
	}
	
}
