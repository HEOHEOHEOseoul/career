package com.example.demo.controller;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.LoginUser;
import com.example.demo.dto.CareerDTO;
import com.example.demo.dto.NewWalletDTO;
import com.example.demo.dto.PageRequestDTO;
import com.example.demo.dto.TxDTO;
import com.example.demo.dto.TxsDTO;
import com.example.demo.model.User;
import com.example.demo.security.dto.SessionUser;
import com.example.demo.service.CareerService;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class ClientController {
    private final CareerService careerService;
    private final UserService userS;
    private final WalletService walletS;
    
	// 메인 홈
	@GetMapping({"/main", "/"})
	public String main(Model model, @LoginUser SessionUser user) {
		model.addAttribute("user", user);
		return "/main";
	}
		
	// 지갑 페이지
	@GetMapping("/career/wallet")
	public String walletPage(Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if(user!=null) {
			model.addAttribute("user", user);
			 User userInfo = userS.getUserInfo(user.getId());
			 model.addAttribute("userInfo", userInfo);
		}else {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		return "/career/wallet";
	}
	
	@PostMapping("/career/wallet")
	public String createwallet(@RequestParam("alias") String alias, Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		model.addAttribute("user", user);
		NewWalletDTO response = walletS.initWallet(alias);
		// 백엔드로부터 받은 pk보여주기
		if(response.getAlias().equals(alias)) {
			userS.registerWallet(user.getId(), response.getAddress(), alias);
		}
			
		rAttr.addAttribute("msg", response.getPrivateKey());
		
		return "redirect:/career/wallet";
	}
	
	@GetMapping("/career/new")
	public String newcareerPage(Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if(user!=null) {
			model.addAttribute("id", user.getId());
			model.addAttribute("user", user);
		}else{
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		return "/career/newcareer";
	}
	
	@PostMapping("/career/new")
	public String insertnewcareer(CareerDTO dto, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if (user == null) {
			rAttr.addAttribute("result", "사용자 정보가 없습니다.");
			return "/";
		}
		long num = careerService.registerCareer(dto);
		if (num > -1) {
			rAttr.addAttribute("email", dto.getApplier());
			rAttr.addAttribute("num", num);
			return "redirect:/pay/kakaoPay";
		}
		return "redirect:/main";
	}
		
	@GetMapping("/career/pending")
	public String pendingList(Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if (user == null) {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		model.addAttribute("result", careerService.getTempCareerList(user.getId()));
		model.addAttribute("email", user.getId());
		model.addAttribute("user", user);
		return "/career/pending";
	}
	
	@GetMapping("/career/pendingcareer")
	public String detail(@RequestParam("num")Long num, Model model, @LoginUser SessionUser user) {
		model.addAttribute("user", user);
		CareerDTO dto = careerService.getTempCareer(num);
		model.addAttribute("dto", dto);
		return "/career/pendingcareer";
	}
	
	@GetMapping("/career/mine")
	public String myCareer(Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if (user == null) {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		model.addAttribute("user", user);
		String wallet = userS.getWallet(user.getId());
		List<TxDTO> tdto = walletS.getTxs(wallet);
		model.addAttribute("list",tdto);
		return null;
	}
	@GetMapping("/career/requested")
	public String myCareer(Model model, @Param("address")String address, @LoginUser SessionUser user) {
		List<TxDTO> tdto = walletS.getTxs(address);
		if(tdto != null) {			
			model.addAttribute("list",tdto);
		}
		model.addAttribute("user", user);
		return null;
	}
	@GetMapping("/career/detail")
	public String detailTx(Model model, @RequestParam("txid")String txid, RedirectAttributes rAttr , @LoginUser SessionUser user) {
		model.addAttribute("user", user);
		TxDTO dto = walletS.getTxDetail(txid);
		String str = "상세정보를 불러오지 못했습니다. 다시 시도해주세요";
		if (dto != null) {
			model.addAttribute("dto",dto);
			model.addAttribute("txid", txid);
		}else {
			model.addAttribute("msg",str);
		}
			return null;
	}
	
	
	
}
