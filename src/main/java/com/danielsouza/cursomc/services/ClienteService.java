package com.danielsouza.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.danielsouza.cursomc.domain.Cliente;
import com.danielsouza.cursomc.dto.ClienteDTO;
import com.danielsouza.cursomc.repositories.ClienteRepository;
import com.danielsouza.cursomc.services.exceptions.DataIntegrityException;
import com.danielsouza.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository repo;

	public Cliente findById(Integer id) {
		Optional<Cliente> cliente = repo.findById(id);
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
	
	public Cliente fromDTO(ClienteDTO clienteDTO) {
		return new Cliente(clienteDTO.getId(), clienteDTO.getNome(), clienteDTO.getEmail(), null, null);
	}

}
