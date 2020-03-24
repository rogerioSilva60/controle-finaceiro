package br.com.controlefinaceiro.financeiroapi.usuario.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDto {

    private Long id;
    @NotEmpty
    private String nome;
    @NotEmpty
    private String email;
    @NotEmpty
    private String telefone;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Fortaleza")
    private Date dataNascimento;

}
