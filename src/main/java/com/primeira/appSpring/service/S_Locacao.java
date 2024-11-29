package com.primeira.appSpring.service;

import com.primeira.appSpring.model.M_Locacao;
import com.primeira.appSpring.model.M_Quarto;
import com.primeira.appSpring.repository.R_Locacao;
import com.primeira.appSpring.repository.R_Quarto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class S_Locacao {
    private static R_Quarto r_quarto;
    private static R_Locacao r_locacao;

    public S_Locacao(R_Quarto r_quarto, R_Locacao r_locacao) {
        this.r_quarto = r_quarto;
        this.r_locacao = r_locacao;
    }

    public static M_Quarto acharQuarto(int numero) {
        return r_quarto.findByNumero(numero);
    }

    public static M_Locacao realizarLocacao(int numero_quarto, LocalDate data_checkIn,
                                            LocalDate data_checkOut, long id_usuario, long id_quarto) {
        boolean podeLocar = true;

        if (numero_quarto <= 0) {
            podeLocar = false;
        }
        if (data_checkIn == null || data_checkIn.isBefore(LocalDate.now())) {
            podeLocar = false;
        }
        if (data_checkOut == null || data_checkOut.isBefore(data_checkIn) || data_checkOut.isEqual(data_checkIn)) {
            podeLocar = false;
        }
        if (id_usuario <= 0) {
            podeLocar = false;
        }

        LocalDateTime dataTimeCheckIn = data_checkIn.atTime(12, 0);
        LocalDateTime dataTimeCheckOut = data_checkOut.atTime(10, 0);

        if (podeLocar) {
            M_Locacao m_locacao = new M_Locacao();
            m_locacao.setId_usuario(id_usuario);
            m_locacao.setNum_quarto(numero_quarto);
            m_locacao.setCheck_in(dataTimeCheckIn);
            m_locacao.setCheck_out(dataTimeCheckOut);

            Random random = new Random(System.nanoTime());
            BigDecimal senha = BigDecimal.valueOf(random.nextInt(1000, 10000));
            while (isSenhaRepetida(senha)) {
                senha = BigDecimal.valueOf(random.nextInt(1000, 10000));
            }
            m_locacao.setSenha(senha);
            m_locacao.setId_quarto(id_quarto);
            m_locacao.setPreco(r_quarto.findByNumero(numero_quarto).getPreco());

            try {
                return r_locacao.save(m_locacao);
            } catch (Exception e) {
            }
        }

        return null;
    }

    private static boolean isSenhaRepetida(BigDecimal senha) {
        return r_locacao.findBySenha(senha).isPresent(); // verifica se o objeto retornado possui campos preenchidos (a senha ja
        // existe em algum lugar) ou se o objeto esta null (a senha n existe)
    }

    public static List<M_Quarto> getQuartosDisponiveis() {
        return r_quarto.findQuartosDisponiveis();
    }

    public static M_Locacao getLocacaoComCheckInCheckOut(LocalDate checkIn, LocalDate checkOut){
        return r_locacao.findLocacaoInThisTime(checkIn, checkOut);
    }
}
