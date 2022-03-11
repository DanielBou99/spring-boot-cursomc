package com.danielsouza.cursomc.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.danielsouza.cursomc.domain.Cidade;
import com.danielsouza.cursomc.domain.Cliente;
import com.danielsouza.cursomc.domain.ClienteNewDTO;
import com.danielsouza.cursomc.domain.Endereco;
import com.danielsouza.cursomc.domain.enums.Perfil;
import com.danielsouza.cursomc.domain.enums.TipoCliente;
import com.danielsouza.cursomc.dto.ClienteDTO;
import com.danielsouza.cursomc.repositories.ClienteRepository;
import com.danielsouza.cursomc.repositories.EnderecoRepository;
import com.danielsouza.cursomc.security.JWTUtil;
import com.danielsouza.cursomc.security.UserSS;
import com.danielsouza.cursomc.services.exceptions.AuthorizationException;
import com.danielsouza.cursomc.services.exceptions.DataIntegrityException;
import com.danielsouza.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;

	@Autowired
	private EnderecoRepository enderecoRepo;

	@Autowired
	private BCryptPasswordEncoder pe;
	
	private static final Logger LOG = LoggerFactory.getLogger(JWTUtil.class);

	public Cliente findById(Integer id) {
		
		UserSS user = UserService.authenticated();
		if (user == null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId())) {
			LOG.info("Acesso negado");
			LOG.info("user: " + user.getUsername());
			LOG.info("ID: " + id);
			LOG.info("userId: " + user.getId());
			throw new AuthorizationException("Acesso negado");
		}
		LOG.info("Buscar cliente.");
		Optional<Cliente> cliente = repo.findById(id);
		LOG.info("Cliente encontrado: " + cliente.toString());
		return cliente.orElseThrow(() -> new ObjectNotFoundException("Nenhum Cliente encontrado para ID: " + id + "."));
	}

	public Cliente update(Cliente obj) {
		Cliente novoObj = this.findById(obj.getId());
		updateData(novoObj, obj);
		return repo.save(novoObj);
	}

	private void updateData(Cliente novoObj, Cliente obj) {
		novoObj.setNome(obj.getNome());
		novoObj.setEmail(obj.getEmail());
	}

	public void delete(Integer id) {
		this.findById(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir porque há entidades relacionadas.");
		}
	}

	public List<Cliente> findAll() {
		return repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDTO) {
		return new Cliente(objDTO.getId(), objDTO.getNome(), objDTO.getEmail(), null, null, null);
	}

	public Cliente fromDTO(ClienteNewDTO objDTO) {
		Cliente cli = new Cliente(null, objDTO.getNome(), objDTO.getEmail(), objDTO.getCpfOuCnpj(),
				TipoCliente.toEnum(objDTO.getTipo()), pe.encode(objDTO.getSenha()));
		Cidade cid = new Cidade(objDTO.getCidadeId(), null, null);
		Endereco end = new Endereco(null, objDTO.getLogadouro(), objDTO.getNumero(), objDTO.getComplemento(),
				objDTO.getBairro(), objDTO.getCep(), cli, cid);
		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDTO.getTelefone1());
		if (objDTO.getTelefone2() != null) {
			cli.getTelefones().add(objDTO.getTelefone2());
		}
		if (objDTO.getTelefone3() != null) {
			cli.getTelefones().add(objDTO.getTelefone3());
		}
		return cli;
	}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj = repo.save(obj);
		enderecoRepo.saveAll(obj.getEnderecos());
		return obj;
	}

}
