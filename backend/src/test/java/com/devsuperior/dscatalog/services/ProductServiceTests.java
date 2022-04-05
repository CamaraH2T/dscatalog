package com.devsuperior.dscatalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import com.devsuperior.dscatalog.repositories.ProductRepository;
import com.devsuperior.dscatalog.resources.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {
	
	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;		

	private long existsId;
	private long nonExistsId;
	private long dependentId;
	
	@BeforeEach
	void setUp() throws Exception {
		existsId = 1L;
		nonExistsId = 1000L;
		dependentId = 4L;
		
		Mockito.doNothing().when(repository).deleteById(existsId);
		Mockito.doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistsId);
		Mockito.doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
				
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(dependentId);
	}
	
	@Test
	public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {
				
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(nonExistsId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(nonExistsId);
	}
	
	@Test
	public void deleteShouldDoNothingWhenIdExists() {
				
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existsId);
		});
		
		Mockito.verify(repository, Mockito.times(1)).deleteById(existsId);
	}
}
