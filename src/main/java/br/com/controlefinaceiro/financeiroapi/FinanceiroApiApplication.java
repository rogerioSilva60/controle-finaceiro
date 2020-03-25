package br.com.controlefinaceiro.financeiroapi;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
//@EnableScheduling
public class FinanceiroApiApplication {

	@Bean
	public ModelMapper modelMapper(){
		return new ModelMapper();
	}

//	@Scheduled(cron = "0 0/5 * 1/1 * ?")
//	public void teste(){
//		System.out.println("Executado ás " + DataHora.dataFormatada(new Date(),"dd-MM-yyyy HH:mm:ss"));
//	}

	public static void main(String[] args) {
		SpringApplication.run(FinanceiroApiApplication.class, args);
	}

}
