package com.example.demo.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.demo.dto.AddressDTO;
import com.example.demo.dto.AliasDTO;
import com.example.demo.dto.CareerDTO;
import com.example.demo.dto.NewWalletDTO;
import com.example.demo.dto.TxDTO;
import com.example.demo.dto.TxsDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@Service
@RequiredArgsConstructor
public class WalletService {
	private final WebClient client;
	
    public NewWalletDTO initWallet(String alias) {
    	AliasDTO a = new AliasDTO(alias);
        String walletmono = client.post().uri("/MakeWallet")
        		//.bodyValue("{\n\"Alias\" : " + alias + "\n}")
        		 .accept(MediaType.APPLICATION_JSON)
        		 .bodyValue(a)
        		.retrieve().bodyToMono(String.class).block();

        	ObjectMapper objectMapper = new ObjectMapper();

            // JSON -> Java Object
            try {
            	@SuppressWarnings("unchecked")
				Map<String, String> map = objectMapper.readValue(walletmono, Map.class);
            	
                NewWalletDTO response = new NewWalletDTO();
                response.setAddress(map.get("Address"));
                response.setAlias(map.get("Alias"));
                response.setPrivateKey(map.get("PrivateKey"));
                response.setPublickey(map.get("PublicKey"));
                return response;
                
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
       return null;
    }
    
    public String registerCareer(CareerDTO dto, String wallet) {
    	Map<String, String> body = new HashMap<>();
    	body.put("Address", wallet);
    	body.put("Applier", dto.getApplier());
    	body.put("CareerStart", dto.getCareer_start());
    	body.put("CareerEnd", dto.getCareer_end());
    	body.put("Company", dto.getCompany());
    	body.put("Job", dto.getJob());
    	body.put("Payment", dto.getPayment());
    	body.put("Proof", dto.getProof_name());

    	String mono = client.post().uri("/RegisterCareer")
       		 .accept(MediaType.APPLICATION_JSON)
       		 .body(Mono.just(body), Map.class).retrieve().bodyToMono(String.class).block();
    	return mono; // 결과가 하나만 온다는 가정하에 mono<string>
    }
    
    public List<TxDTO> getTxs(String wallet) {
    	AddressDTO a = new AddressDTO(wallet);
       	String mono = client.post().uri("/CheckAddress").bodyValue(a)
    			.retrieve().bodyToMono(String.class).block();
       	
       	ObjectMapper mapper = new ObjectMapper();

        try {
        	@SuppressWarnings("unchecked")
			Map<String, ArrayList<String>> map = mapper.readValue(mono, Map.class);
        	
        	List<TxDTO> list = new ArrayList<TxDTO>();
        	// 하고자 했던 것 : 각 항목에 대해 배열로 넘어 오므로 각각 순서에 맞춰 TxDTO에 넣어주고자 함
        	if (map.get("StringTxid")!= null) {
        	for(int i = 0; i < map.get("StringTxid").size(); i++) {
        		TxsDTO temp = TxsDTO.builder().txid(map.get("StringTxid")).job(map.get("Job")).payment(map.get("Payment")).proof(map.get("Proof")).company(map.get("Company")).CareerStart(map.get("CareerStart")).CareerEnd(map.get("CareerEnd")).build();
        		list.add(TxDTO.builder().Txid(temp.getTxid().get(i)).Job(temp.getJob().get(i)).Company(temp.getCompany().get(i)).Proof(temp.getProof().get(i)) .build());	
        	}
            return list;
        	}else {
        		return null;
        	}
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
		return null;
    }
    public TxDTO getTxDetail(String txid) {
    	TxDTO temp = new TxDTO();
    	temp.setTxid(txid);
    	String mono = client.post().uri("detailTx").bodyValue(temp).retrieve().bodyToMono(String.class).block();
    	ObjectMapper mapper = new ObjectMapper();
    	try {
    		@SuppressWarnings("unchecked")
			Map<String,String> map = mapper.readValue(mono, Map.class);
    		TxDTO dto = TxDTO.builder().Txid(map.get("StringTxid")).Job(map.get("Job")).Company(map.get("Company")).CareerStart(map.get("CareerStart")).CareerEnd(map.get("CareerEnd")).Proof(map.get("Proof")).Sign(map.get("Sign")).Hash(map.get("Hash")).build(); 
    		return dto ;
    	}catch(Exception e) {
    			e.printStackTrace();
    	}
    	return null;
    	
    }
      
}
