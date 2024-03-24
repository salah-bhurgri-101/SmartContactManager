package com.spring.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spring.dao.ContactRepository;
import com.spring.dao.UserRepository;
import com.spring.entities.Contact;
import com.spring.entities.User;
import com.spring.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;



@org.springframework.stereotype.Controller
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder; 
	
	@ModelAttribute
	public void addCommomData(Model model,Principal principal) {
		String userName = principal.getName();
		User user = userRepository.findByEmail(userName);
		model.addAttribute("user",user);
	}
	
	@RequestMapping("/index")
	public String dashboard(Model model) {
		model.addAttribute("title", "Admin_Dashbord - Smart Contact Manger");
		return "admin/admin_dashboard";
	}

	
	@GetMapping("/profile")
	public String profile(Model model) {
		model.addAttribute("title","Admin - Profile");
		return "admin/profile";
	}
	
	@GetMapping("/view_user/{page}")
	public String viewUser(Model model , @PathVariable("page") Integer page , Principal principal) {
		model.addAttribute("title","Admin -Users");
		Pageable pageable = (Pageable) PageRequest.of(page, 5);
		
//		Page<User> allUser = userRepository.findAll(pageable);	
		String role = "Role_User";
		Page<User> allUser = userRepository.findByUser(role, pageable);
		
		model.addAttribute("users", allUser);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages",allUser.getTotalPages());
		return "admin/view_user";
	}
	
	@GetMapping("/settings")
	public String settings(Model model) {
		model.addAttribute("title","Admin - Settings");
		return "admin/settings";
	}
	
//	@PostMapping("/change-password")
//	public String changePassword(@RequestParam("oldPassword") String oldPassword , @RequestParam("newPassword") String newPassword , Principal principal , HttpSession session) {
//		
//		User user = userRepository.findByEmail(principal.getName());	
//		
//		if(passwordEncoder.matches(oldPassword, user.getPassword())) {
//			
//			user.setPassword(passwordEncoder.encode(newPassword));
//			userRepository.save(user);
//			session.setAttribute("message", new com.spring.helper.Message("Your password is successfully change...", "success"));
//		}
//		else {
//			session.setAttribute("message", new com.spring.helper.Message("Please enter corect old password !!", "danger"));
//			return "redirect:/admin/settings";
//		}
//		
//		return "redirect:/admin/index";
//	}
	
	@GetMapping("/add-user")
	public String addUser(Model model) {
		model.addAttribute("title","Admin -Add User");

		return "admin/signup";
	}
	
	
	@GetMapping("/delete-user/{id}")
	public String deleteUser(Model model , @PathVariable("id") Integer id) {
		
		System.out.println("delete account");
		User user = userRepository.getById(id);
		userRepository.delete(user);
		return "redirect:/admin/view_user/0";
	}
	
	
	@PostMapping("/proccess-user-update")
	public String userUpdate(@ModelAttribute User user , @RequestParam("idp") Integer idp , @RequestParam("profileImage") MultipartFile file , HttpSession session) {	

		try {

			User oldUser = userRepository.findById(idp).get();
			System.out.println("bnbn"+oldUser);
			if (!file.isEmpty()) {

				// delete photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldUser.getImageUrl());
				System.err.println(file1.delete());

				// Update new Photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());

				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				user.setImageUrl(file.getOriginalFilename());

			} else {
				user.setImageUrl(oldUser.getImageUrl());
			}

			System.out.println(user.toString());
			
			userRepository.save(user);
			session.setAttribute("message", new com.spring.helper.Message("User Updated Succesfully","success"));
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "admin/admin_dashboard";
		
	}
	
	@RequestMapping("/{id}/view_user")
	public String showUserDetail(@PathVariable("id") Integer cId, Model model, Principal principal) {

		User user = userRepository.findById(cId).get();
		
		model.addAttribute("user", user);
		model.addAttribute("title", user.getName());
		
		return "admin/user_detail";
	}
	@PostMapping("/update-user/{id}")
	public String updateForm(@PathVariable("id") Integer cId, Model model) {
		model.addAttribute("title", "Update Contact");

		User user = userRepository.findById(cId).get();
		model.addAttribute("user", user);
		System.out.println("User Form Fire"+user);
		return "admin/update_form";
	}
	
	
	@PostMapping("/proccess-update")
	public String processUpdate(@ModelAttribute User user, @RequestParam("profileImage") MultipartFile file, @RequestParam("id") Integer id ,
			Model model, HttpSession session) {

		try {

			User oldUser = userRepository.findById(id).get();
			System.out.println("bnbn"+oldUser);
			if (!file.isEmpty()) {

				// delete photo
				File deleteFile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deleteFile, oldUser.getImageUrl());
				System.err.println(file1.delete());

				// Update new Photo
				File saveFile = new ClassPathResource("static/img").getFile();
				Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				user.setImageUrl(file.getOriginalFilename());

			} else {
				user.setImageUrl(oldUser.getImageUrl());
			}

			System.out.println("user To String"+user.toString());
			System.out.println("User"+user);
			session.setAttribute("message", new com.spring.helper.Message("User Updated Succesfully","success"));
			userRepository.save(user);
			

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return "admin/admin_dashboard";
//		return "redirect:/admin/" + user.getId() + "/view_user";
	}
	
	
	
	@GetMapping("/view_contact/{page}/{id}")
	public String viewContact(@PathVariable("page") Integer page,@PathVariable("id") Integer id, Model model, Principal principal) {

		
		User user = userRepository.findById(id).get();

		Pageable pageable = PageRequest.of(page, 5);

		Page<Contact> contact = contactRepository.findByUserId(user.getId(), pageable);
		
		model.addAttribute("users", contact);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", contact.getTotalPages());

		return "admin/view_contact";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword ,@RequestParam("newPassword") String newPassword , HttpSession session , Principal principal , Model model) {
		User exitingUser = userRepository.findByEmail(principal.getName());
		
		if(oldPassword =="" || oldPassword.isEmpty()) {
			model.addAttribute("oldReq","New Password Required !");
		}
		if(newPassword == "" || newPassword.isEmpty()) {
			model.addAttribute("newReq","New Password required !");
		}
		if(passwordEncoder.matches(oldPassword, exitingUser.getPassword())){
			if(newPassword.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$")) {
				System.out.println("New Password "+newPassword);
				exitingUser.setPassword(passwordEncoder.encode(newPassword));
				System.out.println("New Password "+newPassword);
				userRepository.save(exitingUser);
				session.setAttribute("message", new com.spring.helper.Message("Your password is successfully changed.","success"));
			}
			else {
				model.addAttribute("newPassword", newPassword);
				model.addAttribute("oldPassword", oldPassword);
				model.addAttribute("newPassError", "Please use at least one uppercase letter, one lowercase letter, one digit, and one special character.");
				return "admin/settings";
			}
		}
		else {
	       session.setAttribute("message", new com.spring.helper.Message("Please enter the correct old password!","danger"));
	    	model.addAttribute("oldPass",oldPassword);
	        model.addAttribute("newPass", newPassword);
	        return "admin/settings";
	    }
	
		return "redirect:/admin/index";
		
	}
	
	@RequestMapping("/{cId}/view_contact")
	public String showContactDetail(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		System.out.println("CID " + cId);

		Optional<Contact> contactOptional = contactRepository.findById(cId);
		Contact contact = contactOptional.get();

		String userName = principal.getName();
		User user = userRepository.findByEmail(userName);

//		if (user.getId() == contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("title", contact.getName());
//		}

		return "admin/contact_detail";
	}
	
}
