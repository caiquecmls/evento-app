package com.eventoapp.eventoapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.eventoapp.eventoapp.models.Convidado;
import com.eventoapp.eventoapp.models.Evento;
import com.eventoapp.eventoapp.repository.ConvidadoRepository;
import com.eventoapp.eventoapp.repository.EventoRepository;

@Controller
public class EventoController {

	@Autowired
	private EventoRepository er;

	@Autowired
	private ConvidadoRepository cr;

	@GetMapping(value = "/cadastrarEvento")
	public String form() {
		return "evento/formEvento.html";
	}

	@PostMapping(value = "/cadastrarEvento")
	public String form(Evento evento, BindingResult bindingResult, RedirectAttributes redirAttr) {
		if (evento.getNome() == "" || evento.getNome().matches("[0-9]+") || evento.getLocal() == ""
				|| evento.getData() == "" || evento.getHorario() == "") {
			redirAttr.addFlashAttribute("msgSucesso", "preencha os campos corretamente");
			return "redirect:/cadastrarEvento";
		}
		er.save(evento);
		redirAttr.addFlashAttribute("msgSucesso", "salvo com sucesso");
		return "redirect:/cadastrarEvento";
	}

	@GetMapping("/eventos")
	public ModelAndView listaEventos(Evento evento) {
		ModelAndView mv = new ModelAndView("/index");
		Iterable<Evento> eventos = er.findAll();
		mv.addObject("eventos", eventos);
		return mv;
	}

	@GetMapping(value = "/{codigo}")
	public ModelAndView detalhesEvento(@PathVariable("codigo") long codigo) {
		Evento evento = er.findByCodigo(codigo);
		ModelAndView mv = new ModelAndView("evento/detalhesEvento");
		mv.addObject("evento", evento);

		Iterable<Convidado> convidados = cr.findByEvento(evento);
		mv.addObject("convidados", convidados);

		return mv;
	}

//	@GetMapping("/delete/{id}")
//	public String deletarEvento(Long codigo) {
//		Evento evento = er.findByCodigo(codigo);
//		er.delete(evento);
//		return "redirect:/eventos";
//	}
	
	@GetMapping("/delete/{id}")
	public String deletar(@PathVariable Long id) {
		Evento evento = er.findByCodigo(id);
		er.delete(evento);
		return "redirect:/eventos";
	}

	@PostMapping(value = "/{codigo}")
	public String detalhesEventoPost(@PathVariable("codigo") long codigo, Convidado convidado,
			BindingResult bindingResult, RedirectAttributes redirAttr) {
		Evento evento = er.findByCodigo(codigo);
		if (convidado.getNomeConvidado() == "" || convidado.getRg() == null
				|| convidado.getNomeConvidado().matches("[0-9]+")) {
			redirAttr.addFlashAttribute("msgSucesso", "preencha os campos corretamente");
			return "redirect:/{codigo}";
		}
		convidado.setEvento(evento);
		cr.save(convidado);
		redirAttr.addFlashAttribute("msgSucesso", "salvo com sucesso");
		return "redirect:/{codigo}";
	}
	
	@GetMapping("/deletarConvidado/{id}")
	public String deletarConvidado(@PathVariable Long id) {
		Convidado convidado = cr.findByRg(id);
		cr.delete(convidado);	
		Evento evento = convidado.getEvento();
		
		
		return "redirect:/"+evento.getCodigo();
	}
}
