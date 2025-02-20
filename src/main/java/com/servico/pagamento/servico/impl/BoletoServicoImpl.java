package com.servico.pagamento.servico.impl;

import java.time.LocalDate;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.servico.pagamento.entidade.Boleto;
import com.servico.pagamento.repositorio.BoletoRepositorio;
import com.servico.pagamento.servico.BoletoServico;

@Service
public class BoletoServicoImpl implements BoletoServico {

	@Autowired
	private BoletoRepositorio boletoRepositorio;

	@Override
	public Boleto salvarBoleto(Boleto boleto) {
		return boletoRepositorio.save(boleto);
	}

	@Override
	public Boleto gerarBoleto() {
		Boleto boleto = new Boleto();
		Random gerador = new Random();
		boleto.setNumeroBoleto(gerador.nextInt(101) * 100);
		boleto.setDataVencimento(LocalDate.now().plusDays(5));
		return boleto;
	}

}
