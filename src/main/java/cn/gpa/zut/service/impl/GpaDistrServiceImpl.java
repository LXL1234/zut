package cn.gpa.zut.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.gpa.zut.dao.IGpaDistrDao;
import cn.gpa.zut.domain.GpaDistr;
import cn.gpa.zut.service.IGpaDistrService;

@Service
@Transactional
public class GpaDistrServiceImpl implements IGpaDistrService {
	@Autowired
	private IGpaDistrDao gpaDistrDao;

	@Override
	public void save(GpaDistr gpaDistr) {
		// TODO Auto-generated method stub
      gpaDistrDao.save(gpaDistr);
	}

	@Override
	public List<GpaDistr> findAllById(String Id) throws Exception {
		// TODO Auto-generated method stub
		return gpaDistrDao.findAllById(Id);
	}

}
