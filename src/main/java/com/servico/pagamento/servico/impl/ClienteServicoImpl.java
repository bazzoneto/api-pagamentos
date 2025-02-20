package com.servico.pagamento.servico.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.servico.pagamento.dto.ClienteDTO;
import com.servico.pagamento.entidade.Cliente;
import com.servico.pagamento.repositorio.ClienteRepositorio;
import com.servico.pagamento.servico.ClienteServico;

@Service
public class ClienteServicoImpl implements ClienteServico {

	@Autowired
	private ClienteRepositorio clienteRepositorio;

	@Override
	public Cliente buscarCliente(ClienteDTO clienteDTO) {
		return clienteRepositorio.findById(clienteDTO.getIdCliente()).orElse(null);
	}

}