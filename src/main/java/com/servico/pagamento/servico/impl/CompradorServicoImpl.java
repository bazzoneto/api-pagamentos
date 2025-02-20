package com.servico.pagamento.servico.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.servico.pagamento.entidade.Comprador;
import com.servico.pagamento.repositorio.CompradorRepositorio;
import com.servico.pagamento.servico.CompradorServico;

@Service
public class CompradorServicoImpl implements CompradorServico {

	@Autowired
	private CompradorRepositorio compradorRepositorio;

	@Override
	public Comprador buscarComprador(Comprador comprador) {
		return compradorRepositorio.findById(comprador.getIdComprador()).get();
	}

	@Override
	public Comprador buscarCompradorCPF(String cpf) {
		return compradorRepositorio.findByCpf(cpf);
	}

	@Override
	public Comprador salvarComprador(Comprador comprador) {
		return compradorRepositorio.save(comprador);
	}

}
