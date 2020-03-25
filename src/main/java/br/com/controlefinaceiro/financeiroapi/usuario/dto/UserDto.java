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
public class UserDto {

    private Long id;
    @NotEmpty(message = "Nome obrigatório")
    private String name;
    @NotEmpty(message = "Email obrigatório")
    private String email;
    @NotEmpty(message = "Telefone obrigatório")
    private String phone;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "America/Fortaleza")
    private Date birthDate;

}
