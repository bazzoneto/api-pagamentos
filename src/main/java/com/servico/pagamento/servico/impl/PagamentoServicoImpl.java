package com.servico.pagamento.servico.impl;

import java.time.LocalDate;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.servico.pagamento.dto.RequisicaoPagamentoDTO;
import com.servico.pagamento.dto.RespostaPagamentoDTO;
import com.servico.pagamento.entidade.Boleto;
import com.servico.pagamento.entidade.CartaoCredito;
import com.servico.pagamento.entidade.Cliente;
import com.servico.pagamento.entidade.Comprador;
import com.servico.pagamento.entidade.FormaPagamento;
import com.servico.pagamento.entidade.Pagamento;
import com.servico.pagamento.entidade.Status;
import com.servico.pagamento.repositorio.PagamentoRepositorio;
import com.servico.pagamento.servico.BoletoServico;
import com.servico.pagamento.servico.CartaoServico;
import com.servico.pagamento.servico.CompradorServico;
import com.servico.pagamento.servico.PagamentoServico;

@Service
public class PagamentoServicoImpl implements PagamentoServico {

	@Autowired
	private PagamentoRepositorio pagamentoRepositorio;

	@Autowired
	private CartaoServico cartaoServico;

	@Autowired
	private CompradorServico compradorServico;

	@Autowired
	private BoletoServico boletoServico;

	@Autowired
	private ConversionService conversionService;

	@Override
	public RespostaPagamentoDTO realizarPagamento(RequisicaoPagamentoDTO requisicaoPagamentoDTO) {

		var pagamento = conversionService.convert(requisicaoPagamentoDTO.getPagamento(), Pagamento.class);
		var cliente = conversionService.convert(requisicaoPagamentoDTO.getCliente(), Cliente.class);
		pagamento.setCliente(cliente);

		// Verifying the buyer
		var comprador = compradorServico.buscarCompradorCPF(requisicaoPagamentoDTO.getComprador().getCpf());
		if (Objects.nonNull(comprador)) {
			pagamento.setComprador(comprador);
		} else {
			var compradorNovo = conversionService.convert(requisicaoPagamentoDTO.getComprador(), Comprador.class);
			pagamento.setComprador(compradorServico.salvarComprador(compradorNovo));
		}

		// Handling payment method
		if (FormaPagamento.CARTAO_CREDITO.equals(pagamento.getForma())) {
			var cartaoCredito = (CartaoCredito) pagamento.getCartao();
			cartaoServico.validarCartao(cartaoCredito);
			cartaoCredito.setTipoBandeira(cartaoServico.identificarBandeira(cartaoCredito.getNumero()));
			pagamento.setCartao(cartaoServico.salvarCartao(cartaoCredito));
			pagamento.setStatus(Status.ENVIADO);
		} else if (FormaPagamento.BOLETO.equals(pagamento.getForma())) {
			var boleto = boletoServico.gerarBoleto();
			pagamento.setBoleto(boletoServico.salvarBoleto(boleto));
			pagamento.setStatus(Status.PROCESSANDO);
		}

		pagamento.setDataCadastro(LocalDate.now());
		var pagamentoSalvo = pagamentoRepositorio.save(pagamento);

		// Preparing the response
		var respostaPagamentoDTO = new RespostaPagamentoDTO();
		respostaPagamentoDTO.setIdPagamento(pagamentoSalvo.getIdPagamento());
		respostaPagamentoDTO.setValor(pagamentoSalvo.getValor());
		respostaPagamentoDTO.setForma(pagamentoSalvo.getForma());
		respostaPagamentoDTO.setStatus(pagamentoSalvo.getStatus());
		if (FormaPagamento.BOLETO.equals(pagamentoSalvo.getForma())) {
			respostaPagamentoDTO.setNumeroBoleto(pagamentoSalvo.getBoleto().getNumeroBoleto());
		}
		return respostaPagamentoDTO;
	}

	@Override
	public Pagamento buscarPagamento(Long idPagamento) {
		var pagamento = pagamentoRepositorio.findById(idPagamento).orElse(null);
		if (Objects.isNull(pagamento)) {
			return null;
		}
		// Obscuring CPF
		var cpfNovo = esconderCPF(pagamento.getComprador().getCpf());
		pagamento.getComprador().setCpf(cpfNovo);
		return pagamento;
	}

	@Override
	public String esconderCPF(String cpf) {
		if (Objects.isNull(cpf)) {
			return null;
		}
		var cpfNovo = "*".repeat(cpf.length() - 2) + cpf.substring(cpf.length() - 2);
		return cpfNovo;
	}

	@Override
	public boolean removerPagamento(Long idPagamento) {
		var pagamento = pagamentoRepositorio.findById(idPagamento).orElse(null);
		if (Objects.isNull(pagamento)) {
			return false;
		}
		pagamentoRepositorio.delete(pagamento);
		return true;
	}
}