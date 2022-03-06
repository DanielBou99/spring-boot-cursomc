package com.danielsouza.cursomc.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.danielsouza.cursomc.domain.Categoria;
import com.danielsouza.cursomc.domain.Produto;
import com.danielsouza.cursomc.repositories.CategoriaRepository;
import com.danielsouza.cursomc.repositories.ProdutoRepository;
import com.danielsouza.cursomc.services.exceptions.ObjectNotFoundException;

@Service
public class ProdutoService {

	@Autowired
	private ProdutoRepository repo;
	
	@Autowired
	private CategoriaRepository categoriaRepo;

	public Produto findById(Integer id) {
		Optional<Produto> pedido = repo.findById(id);
		return pedido.orElseThrow(() -> new ObjectNotFoundException("Nenhum Produto encontrado para ID: " + id + "."));
	}

	public Page<Produto> search(String nome, List<Integer> ids, Integer page, Integer linesPerPage, String orderBy,
			String direction) {
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		List<Categoria> categorias = categoriaRepo.findAllById(ids);
		return repo.findDistinctByNomeContainingAndCategoriasIn(nome, categorias, pageRequest);
	}

}
