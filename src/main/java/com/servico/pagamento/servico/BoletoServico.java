package com.servico.pagamento.servico;

import com.servico.pagamento.entidade.Boleto;

public interface BoletoServico {

	public Boleto salvarBoleto(Boleto boleto);

	public Boleto gerarBoleto();
}
