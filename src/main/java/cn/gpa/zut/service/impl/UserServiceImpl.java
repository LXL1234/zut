package cn.gpa.zut.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.gpa.zut.dao.IUserDao;
import cn.gpa.zut.domain.User;
import cn.gpa.zut.service.IUserService;
@Service
@Transactional
public class UserServiceImpl implements IUserService {
	@Autowired
	private IUserDao userDao;

	@Override
	public List<User> findAll() {
		
		return userDao.findAll();
	}
	@Override
	 public User checkLogin(String username, String password) {
	        
	        User user = userDao.findByUsername(username);
	        if(user != null && user.getUser_password().equals(password)){
	        
	            return user;
	        }
	        return null;
	    }

}
