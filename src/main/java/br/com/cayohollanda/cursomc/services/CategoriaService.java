package br.com.cayohollanda.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import br.com.cayohollanda.cursomc.domain.Categoria;
import br.com.cayohollanda.cursomc.repositories.CategoriaRepository;
import br.com.cayohollanda.cursomc.services.exceptions.DataIntegrityException;
import br.com.cayohollanda.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;
	
	public Categoria find(Integer id) {
		Optional<Categoria> obj = this.repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! ID: " + id
				+ ", Tipo: " + Categoria.class.getName()));
	}
	
	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return this.repo.save(obj);
	}

	public Categoria update(Categoria obj) {
		this.find(obj.getId());
		return this.repo.save(obj);
	}
	
	public void delete(Integer id) {
		this.find(id);
		try {			
			this.repo.deleteById(id);
		} catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos");
		}
	}
	
	public List<Categoria> findAll() {
		return this.repo.findAll();
	}
	
}
