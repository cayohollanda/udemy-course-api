package br.com.cayohollanda.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import br.com.cayohollanda.cursomc.domain.Cliente;
import br.com.cayohollanda.cursomc.dto.ClienteDTO;
import br.com.cayohollanda.cursomc.repositories.ClienteRepository;
import br.com.cayohollanda.cursomc.services.exceptions.DataIntegrityException;
import br.com.cayohollanda.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;
	
	public Cliente find(Integer id) {
		Optional<Cliente> obj = this.repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! ID: " + id));
	}
	
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		return this.repo.save(obj);
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = this.find(obj.getId());
		this.updateData(newObj, obj);
		return this.repo.save(newObj);
	}
	
	public void delete(Integer id) {
		this.find(id);
		try {			
			this.repo.deleteById(id);
		} catch(DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas");
		}
	}
	
	public List<Cliente> findAll() {
		return this.repo.findAll();
	}
	
	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest	= PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return this.repo.findAll(pageRequest);
	}
	
	public Cliente fromDto(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null);
	}
	
	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
	
}
