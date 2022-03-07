package com.danielsouza.cursomc.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.danielsouza.cursomc.domain.ItemPedido;
import com.danielsouza.cursomc.domain.PagamentoComBoleto;
import com.danielsouza.cursomc.domain.Pedido;
import com.danielsouza.cursomc.domain.Produto;
import com.danielsouza.cursomc.domain.enums.EstadoPagamento;
import com.danielsouza.cursomc.repositories.ClienteRepository;
import com.danielsouza.cursomc.repositories.ItemPedidoRepository;
import com.danielsouza.cursomc.repositories.PagamentoRepository;
import com.danielsouza.cursomc.repositories.PedidoRepository;
import com.danielsouza.cursomc.repositories.ProdutoRepository;
import com.danielsouza.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class PedidoService {

	@Autowired
	private PedidoRepository repo;
	
	@Autowired
	private BoletoService boletoService;
	
	@Autowired
	private PagamentoRepository pagamentoRepository;

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ItemPedidoRepository itemPedidoRepository;
	
	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private EmailService emailService;
	
	public Pedido findById(Integer id) {
		Optional<Pedido> pedido = repo.findById(id);
		return pedido.orElseThrow(() -> new ObjectNotFoundException("Nenhum Pedido encontrado para ID: " + id + "."));
	}

	public Pedido insert(Pedido obj) {
		obj.setId(null);
		obj.setInstante(new Date());
		obj.setCliente(clienteRepository.findById(obj.getCliente().getId()).get());
		obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
		obj.getPagamento().setPedido(obj);
		if (obj.getPagamento() instanceof PagamentoComBoleto) {
			PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
			boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
		}
		obj = repo.save(obj);
		pagamentoRepository.save(obj.getPagamento());
		for (ItemPedido ip: obj.getItens()) {
			ip.setDesconto(0.0);
			Optional<Produto> produto = produtoRepository.findById(ip.getProduto().getId());
			ip.setProduto(produto.get());
			ip.setPreco(produto.get().getPreco());
			ip.setPedido(obj);
		}
		itemPedidoRepository.saveAll(obj.getItens());
		emailService.sendOrderConfirmationHtmlEmail(obj);
		return obj;
	}

}
