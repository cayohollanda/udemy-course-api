package br.com.cayohollanda.cursomc.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.cayohollanda.cursomc.domain.Categoria;
import br.com.cayohollanda.cursomc.repositories.CategoriaRepository;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;
	
	public Categoria buscar(Integer id) {
		Optional<Categoria> obj = this.repo.findById(id);
		return obj.orElse(null);
	}
	
}
