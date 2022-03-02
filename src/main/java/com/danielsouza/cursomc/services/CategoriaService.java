package com.danielsouza.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.danielsouza.cursomc.domain.Categoria;
import com.danielsouza.cursomc.dto.CategoriaDTO;
import com.danielsouza.cursomc.repositories.CategoriaRepository;
import com.danielsouza.cursomc.services.exceptions.DataIntegrityException;
import com.danielsouza.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;

	public Categoria findById(Integer id) {
		Optional<Categoria> categoria = repo.findById(id);
		return categoria
				.orElseThrow(() -> new ObjectNotFoundException("Nenhuma categoria encontrada para ID: " + id + "."));
	}

	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return repo.save(obj);
	}
	
	public Categoria update(Categoria obj) {
		Categoria novoObj = this.findById(obj.getId());
		updateData(novoObj, obj);
		return repo.save(novoObj);
	}

	private void updateData(Categoria novoObj, Categoria obj) {
		novoObj.setNome(obj.getNome());
	}
	

	public void delete(Integer id) {
		this.findById(id);
		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Não é possível excluir uma categoria que possui produtos.");
		}
	}

	public List<Categoria> findAll() {
		return repo.findAll();
	}
	
	public Page<Categoria> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		return repo.findAll(pageRequest);
	}
	
	public Categoria fromDTO(CategoriaDTO categoriaDTO) {
		return new Categoria(categoriaDTO.getId(), categoriaDTO.getNome());
	}

}
