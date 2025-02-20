package com.servico.pagamento.servico;

import com.servico.pagamento.entidade.Comprador;

public interface CompradorServico {

	public Comprador buscarComprador(Comprador comprador);

	public Comprador buscarCompradorCPF(String cpf);
	
	public Comprador salvarComprador(Comprador comprador);
}
