package com.servico.pagamento.converte;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.servico.pagamento.dto.CartaoDTO;
import com.servico.pagamento.entidade.CartaoCredito;

@Component
public class DtoToCartaoConverte implements Converter<CartaoDTO, CartaoCredito> {

	@Override
	public CartaoCredito convert(CartaoDTO cartaoDTO) {
		// Usando o Java moderno para mapear os campos de forma eficiente
		return new CartaoCredito(
				cartaoDTO.nome(),
				cartaoDTO.numero(),
				cartaoDTO.cvv(),
				cartaoDTO.dataValidade()
		);
	}
}
