package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.config.LoginUser;
import com.example.demo.dto.CareerDTO;
import com.example.demo.dto.PageRequestDTO;
import com.example.demo.security.dto.SessionUser;
import com.example.demo.service.CareerService;
import com.example.demo.service.UserService;
import com.example.demo.service.WalletService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CareerController {
    private final CareerService careerService;
    private final UserService userS;
    private final WalletService walletS;
    
	@GetMapping("/career/other") // 타인의 이메일로 지갑 주소 조회
	public String Other(Model model, @LoginUser SessionUser user) {
		return "/career/wallet2";
	}
	
	@GetMapping("/career/findothercareer") // 타인의 지갑 주소로 경력 조회
	public String Others(Model model,@LoginUser SessionUser user) {
		return "/career/findothercareer";
	}
	@PostMapping("/career/others")
	public String OthersCareer(RedirectAttributes rAttr , @RequestParam("address")String address) {
		rAttr.addAttribute("address",address);
		return "redirect:/career/requested";
	}
	
	@GetMapping("/admin/pending")
	public String adminpage(PageRequestDTO pageRequestDTO, Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if (user == null) {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		model.addAttribute("result", careerService.getTempCareerList(pageRequestDTO));
		
		return "/admin/pending";
	}
	
	@GetMapping("/admin/pendingcareer")
	public String adminpage(@RequestParam("num")long num, Model model, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if (user == null) {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "redirect:/main";
		}
		CareerDTO dto = careerService.getTempCareer(num);
		model.addAttribute("dto", dto);
		return "/admin/pendingcareer";
	}
	@PostMapping("/admin/pendingcareer")
	public String Approvenewcareer(CareerDTO dto, RedirectAttributes rAttr, @LoginUser SessionUser user) {
		if (user == null) {
			rAttr.addAttribute("error", "로그인 해주세요!");
			return "/";
		}
		String wallet = userS.getWallet(dto.getApplier());
		String response = walletS.registerCareer(dto, wallet);
		if (response!= null) {
			if(careerService.deleteCareer(dto.getNum())) {
				rAttr.addAttribute("msg", response + " 승인 성공!");
				return "redirect:/admin/pending";
			}
		}
			rAttr.addAttribute("num", dto.getNum());
			rAttr.addAttribute("msg", "승인 실패!");
		return "redirect:/admin/pendingcareer";
	}
	
}
