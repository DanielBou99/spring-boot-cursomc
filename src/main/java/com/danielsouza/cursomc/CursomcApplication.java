package com.danielsouza.cursomc;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.danielsouza.cursomc.domain.Categoria;
import com.danielsouza.cursomc.domain.Produto;
import com.danielsouza.cursomc.repositories.CategoriaRepository;
import com.danielsouza.cursomc.repositories.ProdutoRepository;

@SpringBootApplication
public class CursomcApplication implements CommandLineRunner {

	@Autowired
	private CategoriaRepository repoCat;
	
	@Autowired
	private ProdutoRepository repoProd;
	
	public static void main(String[] args) {
		SpringApplication.run(CursomcApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		
		 Categoria cat1 = new Categoria(null, "Informática");
	     Categoria cat2 = new Categoria(null, "Escritório");
	 
	     Produto p1 = new Produto(null, "Computador", 2000.00);
	     Produto p2 = new Produto(null, "Impressora", 800.00);
	     Produto p3 = new Produto(null, "Mouse", 80.00);
	 
	     repoCat.saveAll(Arrays.asList(cat1, cat2));
	     repoProd.saveAll(Arrays.asList(p1,p2,p3));
	     repoCat.flush();
	     repoProd.flush();
	 
	     cat1.getProdutos().addAll(Arrays.asList(p1,p2,p3));
	     cat2.getProdutos().add(p2);
	 
	     p1.getCategorias().add(cat1);
	     p2.getCategorias().addAll(Arrays.asList(cat1,cat2));
	     p3.getCategorias().add(cat1);
	     repoCat.saveAll(Arrays.asList(cat1, cat2));
	     repoProd.saveAll(Arrays.asList(p1,p2,p3));
	}

}
