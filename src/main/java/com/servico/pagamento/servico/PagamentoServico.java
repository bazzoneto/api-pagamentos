package com.servico.pagamento.servico;

import com.servico.pagamento.dto.RequisicaoPagamentoDTO;
import com.servico.pagamento.dto.RespostaPagamentoDTO;
import com.servico.pagamento.entidade.Pagamento;

public interface PagamentoServico {

	public RespostaPagamentoDTO realizarPagamento(RequisicaoPagamentoDTO requisicaoPagamentoDTO);

	public Pagamento buscarPagamento(Long idPagamento);
	
	public String esconderCPF(String cpf);
	
	public boolean removerPagamento(Long idPagamento);
}
