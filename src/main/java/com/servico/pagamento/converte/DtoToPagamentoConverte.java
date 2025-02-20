package com.servico.pagamento.converte;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.servico.pagamento.dto.PagamentoDTO;
import com.servico.pagamento.entidade.FormaPagamento;
import com.servico.pagamento.entidade.Pagamento;
import com.servico.pagamento.exception.CartaoNuloException;

@Component
public class DtoToPagamentoConverte implements Converter<PagamentoDTO, Pagamento> {

	@Autowired
	private DtoToCartaoConverte cartaoConverte;

	@Override
	public Pagamento convert(PagamentoDTO pagamentoDTO) {
		// Utilizando Java moderno, como record e simplificação do código.
		var pagamento = new Pagamento();

		if (FormaPagamento.CARTAO_CREDITO.equals(pagamentoDTO.forma())) {
			var cartao = pagamentoDTO.cartao();
			if (cartao == null) {
				throw new CartaoNuloException();
			}
			pagamento.setCartao(cartaoConverte.convert(cartao));
		}

		pagamento.setForma(pagamentoDTO.forma());
		pagamento.setValor(pagamentoDTO.valor());
		return pagamento;
	}
}