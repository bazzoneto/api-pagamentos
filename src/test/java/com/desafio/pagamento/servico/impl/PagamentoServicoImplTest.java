package com.servico.pagamento.servico.impl;

import java.time.LocalDate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.servico.pagamento.dto.CartaoDTO;
import com.servico.pagamento.dto.ClienteDTO;
import com.servico.pagamento.dto.CompradorDTO;
import com.servico.pagamento.dto.PagamentoDTO;
import com.servico.pagamento.dto.RequisicaoPagamentoDTO;
import com.servico.pagamento.dto.RespostaPagamentoDTO;
import com.servico.pagamento.entidade.Cliente;
import com.servico.pagamento.entidade.Comprador;
import com.servico.pagamento.entidade.FormaPagamento;
import com.servico.pagamento.entidade.Pagamento;
import com.servico.pagamento.repositorio.ClienteRepositorio;
import com.servico.pagamento.servico.PagamentoServico;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class PagamentoServicoImplTest {

	@Autowired
	private PagamentoServico pagamentoservico;

	@Autowired
	private ClienteRepositorio clienteRepositorio;

	Cliente cliente;

	@Before
	public void init() {
		Cliente clienteNew = new Cliente();
		clienteNew.setCnpj("37667221000128");
		clienteNew.setRazaoSocial("Teste");
		cliente = clienteRepositorio.save(clienteNew);
	}

	@After
	public void deleteAll() {
		clienteRepositorio.deleteAll();
	}

	@Test
	public void validarRealizarPagamentoSucessoBoleto() {
		RequisicaoPagamentoDTO requisicaoPagamentoDTO = new RequisicaoPagamentoDTO();
		ClienteDTO clienteDto = new ClienteDTO(cliente.getIdCliente());
		CompradorDTO compradorDto = new CompradorDTO("Amanda", "adsilva@gmail.com", "16379094890");
		double valor = 234.53;
		PagamentoDTO pagamentoDto = new PagamentoDTO(valor, FormaPagamento.BOLETO, null);

		requisicaoPagamentoDTO.setCliente(clienteDto);
		requisicaoPagamentoDTO.setComprador(compradorDto);
		requisicaoPagamentoDTO.setPagamento(pagamentoDto);
		RespostaPagamentoDTO realizarPagamento = pagamentoservico.realizarPagamento(requisicaoPagamentoDTO);

		Assert.assertNotNull(realizarPagamento.getIdPagamento());
		Assert.assertNotNull(realizarPagamento.getNumeroBoleto());
		Assert.assertEquals(realizarPagamento.getForma(), FormaPagamento.BOLETO);
		Assert.assertEquals(realizarPagamento.getValor(), valor, 0);
	}

	@Test
	public void validarRealizarPagamentoSucessoCartaoCredito() {
		RequisicaoPagamentoDTO requisicaoPagamentoDTO = new RequisicaoPagamentoDTO();
		ClienteDTO clienteDto = new ClienteDTO(cliente.getIdCliente());
		CompradorDTO compradorDto = new CompradorDTO("Amanda", "adsilva@gmail.com", "16379094890");
		double valor = 21431.15;
		CartaoDTO cartaoDto = new CartaoDTO("Teste Fulano", "378600277204815", LocalDate.now().plusYears(3), "235");
		PagamentoDTO pagamentoDto = new PagamentoDTO(valor, FormaPagamento.CARTAO_CREDITO, cartaoDto);

		requisicaoPagamentoDTO.setCliente(clienteDto);
		requisicaoPagamentoDTO.setComprador(compradorDto);
		requisicaoPagamentoDTO.setPagamento(pagamentoDto);
		RespostaPagamentoDTO realizarPagamento = pagamentoservico.realizarPagamento(requisicaoPagamentoDTO);

		Assert.assertNotNull(realizarPagamento.getIdPagamento());
		Assert.assertNull(realizarPagamento.getNumeroBoleto());
		Assert.assertEquals(realizarPagamento.getForma(), FormaPagamento.CARTAO_CREDITO);
		Assert.assertEquals(realizarPagamento.getValor(), valor, 0);
	}

	@Test
	public void validarRealizarPagamentoSucessoMesmoCPF() {
		CompradorDTO compradorDto = new CompradorDTO("Amanda", "adsilva@gmail.com", "16379094890");

		// realizar pagamento 2
		RequisicaoPagamentoDTO requisicaoPagamentoDTO = new RequisicaoPagamentoDTO();
		requisicaoPagamentoDTO.setCliente(new ClienteDTO(cliente.getIdCliente()));
		requisicaoPagamentoDTO.setComprador(compradorDto);
		requisicaoPagamentoDTO.setPagamento(new PagamentoDTO(482.53, FormaPagamento.BOLETO, null));
		RespostaPagamentoDTO realizarPagamento1 = pagamentoservico.realizarPagamento(requisicaoPagamentoDTO);

		requisicaoPagamentoDTO = new RequisicaoPagamentoDTO();
		requisicaoPagamentoDTO.setCliente(new ClienteDTO(cliente.getIdCliente()));
		requisicaoPagamentoDTO.setComprador(compradorDto);
		CartaoDTO cartaoDto = new CartaoDTO("Teste Fulano", "378600277204815", LocalDate.now().plusYears(3), "235");
		requisicaoPagamentoDTO.setPagamento(new PagamentoDTO(224.63, FormaPagamento.CARTAO_CREDITO, cartaoDto));
		RespostaPagamentoDTO realizarPagamento2 = pagamentoservico.realizarPagamento(requisicaoPagamentoDTO);

		// verificar se tem somente um comprador com mesmo CPF
		Pagamento pagamento1 = pagamentoservico.buscarPagamento(realizarPagamento1.getIdPagamento());
		Comprador comprador1 = pagamento1.getComprador();

		Pagamento pagamento2 = pagamentoservico.buscarPagamento(realizarPagamento2.getIdPagamento());
		Comprador comprador2 = pagamento2.getComprador();

		Assert.assertEquals(comprador1.getIdComprador(), comprador2.getIdComprador());
		Assert.assertEquals(comprador1.getCpf(), comprador2.getCpf());
	}

	// @Test(expected = ClienteNaoEncontradoException.class)
	@Test(expected = ConversionFailedException.class)
	public void validarRealizarPagamentoFalhaClienteInvalido() {
		RequisicaoPagamentoDTO requisicaoPagamentoDTO = new RequisicaoPagamentoDTO();
		ClienteDTO clienteDto = new ClienteDTO(123456789l);
		CompradorDTO compradorDto = new CompradorDTO("Amanda", "adsilva@gmail.com", "16379094890");
		double valor = 234.53;
		PagamentoDTO pagamentoDto = new PagamentoDTO(valor, FormaPagamento.BOLETO, null);

		requisicaoPagamentoDTO.setCliente(clienteDto);
		requisicaoPagamentoDTO.setComprador(compradorDto);
		requisicaoPagamentoDTO.setPagamento(pagamentoDto);
		pagamentoservico.realizarPagamento(requisicaoPagamentoDTO);
	}

	// @Test(expected = CartaoNuloException.class)
	@Test(expected = ConversionFailedException.class)
	public void validarRealizarPagamentoFalhaCartaoNuloException() {
		RequisicaoPagamentoDTO requisicaoPagamentoDTO = new RequisicaoPagamentoDTO();
		ClienteDTO clienteDto = new ClienteDTO(123456789l);
		CompradorDTO compradorDto = new CompradorDTO("Amanda", "adsilva@gmail.com", "16379094890");
		PagamentoDTO pagamentoDto = new PagamentoDTO(234.23, FormaPagamento.CARTAO_CREDITO, null);

		requisicaoPagamentoDTO.setCliente(clienteDto);
		requisicaoPagamentoDTO.setComprador(compradorDto);
		requisicaoPagamentoDTO.setPagamento(pagamentoDto);
		pagamentoservico.realizarPagamento(requisicaoPagamentoDTO);
	}

	@Test
	public void esconderCPFComSucesso() {
		String cpfAntigo = "76217645033";
		String cpfNovo = pagamentoservico.esconderCPF(cpfAntigo);
		Assert.assertEquals("*********33", cpfNovo);
	}

	@Test
	public void esconderCPFNuloComFalha() {
		Assert.assertNull(pagamentoservico.esconderCPF(null));
	}
}
