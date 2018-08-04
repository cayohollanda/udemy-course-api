package br.com.cayohollanda.cursomc.services;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.com.cayohollanda.cursomc.domain.Cidade;
import br.com.cayohollanda.cursomc.domain.Cliente;
import br.com.cayohollanda.cursomc.domain.Endereco;
import br.com.cayohollanda.cursomc.domain.enums.Perfil;
import br.com.cayohollanda.cursomc.domain.enums.TipoCliente;
import br.com.cayohollanda.cursomc.dto.ClienteDTO;
import br.com.cayohollanda.cursomc.dto.ClienteNewDTO;
import br.com.cayohollanda.cursomc.repositories.ClienteRepository;
import br.com.cayohollanda.cursomc.repositories.EnderecoRepository;
import br.com.cayohollanda.cursomc.security.UserSS;
import br.com.cayohollanda.cursomc.services.exceptions.AuthorizationException;
import br.com.cayohollanda.cursomc.services.exceptions.DataIntegrityException;
import br.com.cayohollanda.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private BCryptPasswordEncoder pe;
	
	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;
	
	@Autowired
	private S3Service s3Service;

	public Cliente find(Integer id) {
		
		UserSS user = UserService.authenticated();
		if(user == null || !id.equals(user.getId())) {
			if(!user.hasRole(Perfil.ADMIN)) {				
				throw new AuthorizationException("Acesso negado!");
			}
		}
		
		Optional<Cliente> obj = this.repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Objeto não encontrado! ID: " + id));
	}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = this.repo.save(obj);
		this.enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
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
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há pedidos relacionados");
		}
	}

	public List<Cliente> findAll() {
		return this.repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return this.repo.findAll(pageRequest);
	}

	public Cliente fromDto(ClienteDTO objDto) {
		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null, null);
	}

	public Cliente fromDto(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfOuCnpj(),
				TipoCliente.toEnum(objDto.getTipo()), pe.encode(objDto.getSenha()));
		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(),
				objDto.getBairro(), objDto.getCep(), cli, cid);
		
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());
		if (objDto.getTelefone2() != null) {
			cli.getTelefones().add(objDto.getTelefone2());
		}

		if (objDto.getTelefone3() != null) {
			cli.getTelefones().add(objDto.getTelefone3());
		}

		return cli;
	}

	private void updateData(Cliente newObj, Cliente obj) {
		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
	
	public URI uploadProfilePicture(MultipartFile multipartFile) {
		return this.s3Service.uploadFile(multipartFile);
	}

}
