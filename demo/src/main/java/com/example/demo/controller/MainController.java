package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.core.ResolvableType;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.LoginUser;
import com.example.demo.dto.CareerDTO;
import com.example.demo.dto.PageRequestDTO;
import com.example.demo.model.WalletResponse;
import com.example.demo.security.dto.SessionUser;
import com.example.demo.service.CareerService;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;

import lombok.RequiredArgsConstructor;


@Controller
@RequiredArgsConstructor
public class MainController {
	private final HttpSession httpSession;
    private final CareerService careerService;
    private final UserService userS;
    private final WalletService walletS;
    
	// 메인 홈
	@GetMapping("/main")
	public String main(Model model, @LoginUser SessionUser user) {
		if(user!=null) {	// session에 저장된 값이 있을 때만 model에 userName으로 등록
			model.addAttribute("user", user);
		}
		
		return "main";
	}
		
	// 지갑 페이지
	@GetMapping("/career/wallet")
	public String walletPage(Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if(user!=null) {
			model.addAttribute("user", user);
		}else {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		return "/career/wallet";
	}
	
	@PostMapping("/career/wallet")
	public String createwallet(String alias, Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		WalletResponse response = walletS.initWallet(alias);
		// 백엔드로부터 받은 pk보여주기
		userS.registerWallet(user.getId(), response.getWallet(), alias);
		
		model.addAttribute("pk", response.getPrivateKey());
		return "/career/wallet";
	}
	
	@GetMapping("/career/new")
	public String newcareerPage(Model model, RedirectAttributes rAttr) {
		SessionUser user = (SessionUser)httpSession.getAttribute("user");
		if(user!=null) {
			model.addAttribute("id", user.getId());
		}else {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		return "/career/newcareer";
	}
	
	@PostMapping("/career/new")
	public String insertnewcareer(CareerDTO dto, RedirectAttributes rAttr) {
		SessionUser user = (SessionUser)httpSession.getAttribute("user");
		if (user == null) {
			rAttr.addAttribute("result", "사용자 정보가 없습니다.");
			return "/";
		}
		if (careerService.registerCareer(dto) != null) {
			return "newcareer_confirm";
		}
		
		return "redirect:/main";
	}
	
	@GetMapping("/career/newconfirm")
	public String newcareerConfirmPage(CareerDTO dto, Model model, RedirectAttributes rAttr) {
		SessionUser user = (SessionUser)httpSession.getAttribute("user");
		if (user == null) {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		model.addAttribute("career", dto);
		
		return "newcareer_confirm";
	}
	
	
	@GetMapping("/career/pending")
	public String pendingList(Model model, RedirectAttributes rAttr) {
		SessionUser user = (SessionUser)httpSession.getAttribute("user");
		if (user == null) {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		PageRequestDTO pageRequestDTO = PageRequestDTO.builder().id(user.getId()).page(1).size(10).build();
		model.addAttribute("result", careerService.getTempCareerList(pageRequestDTO));
		model.addAttribute("email", user.getId());
		return "/career/pending";
	}
	
	@GetMapping("/career/mine")
	public String myCareer(Model model) {
		return null;
		
	}
}
