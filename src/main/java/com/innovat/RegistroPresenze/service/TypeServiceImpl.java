//package com.innovat.RegistroPresenze.service;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
//import org.springframework.stereotype.Service;
//
//import com.innovat.RegistroPresenze.dto.JwtUser;
//import com.innovat.RegistroPresenze.model.Type;
//import com.innovat.RegistroPresenze.repository.TypeRepository;
//
//import lombok.extern.java.Log;
//
//@Service
//@Log
//public class TypeServiceImpl implements TypeService{
//
//	@Autowired
//	private TypeRepository repo;
//	
//	@Override
//	@CacheEvict(cacheNames = "type", allEntries = true)
//	public void save(List<Type> requestBody, JwtUser userlogged) {		
//		repo.saveAll(requestBody);
//	}
//
//	@Override
//	@CacheEvict(cacheNames = "type", allEntries = true)
//	public void delete(Long typeId) {
//		repo.deleteById(typeId);
//	}
//
//	@Override
//    @Cacheable(value="type", key = "#id", sync = true)
//	public boolean isExist(Long typeId) {
//		// TODO Auto-generated method stub
//		log.info("isExist richiesta non cachata");
//		return repo.existsById(typeId);
//	}
//
//	@Override
//	@Cacheable(value="type")
//	public List<Type> getAll() {
//		// TODO Auto-generated method stub
//		log.info("getAll richiesta non cachata");
//		return repo.findAll();
//	}
//
//}
