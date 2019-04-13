package cn.gpa.zut.service.impl;

import cn.gpa.zut.dao.IProductDao;
import cn.gpa.zut.domain.Product;
import cn.gpa.zut.service.IProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@Transactional
public class ProductServiceImpl implements IProductService{

    @Autowired
    private IProductDao productDao;

    @Override
    public void save(Product product) {
        productDao.save(product);
    }

    @Override
    public List<Product> findAll() throws Exception{
        return productDao.findAll();
    }
}
