package com.servico.pagamento.servico;

import com.servico.pagamento.dto.ClienteDTO;
import com.servico.pagamento.entidade.Cliente;

public interface ClienteServico {

	public Cliente buscarCliente(ClienteDTO clienteDTO);
	
	
}
