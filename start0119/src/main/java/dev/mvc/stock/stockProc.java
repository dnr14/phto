package dev.mvc.stock;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("stockProc")
public class stockProc implements stockProcInter{
	
	@Autowired
	stockDAOInter stockDAO;// stockDAOInter => stockDAOImpl

	@Override
	public int create(stockCreateRequest request) {
		return stockDAO.create(request);
	}

	@Override
	public List<stockVO> selectStock() {
		return stockDAO.selectStock();
	}

	@Override
	public int delete(int stockNo) {
		return stockDAO.delete(stockNo);
	}

	@Override
	public stockVO update(int stockNo) {
		return stockDAO.update(stockNo);
	}

	@Override
	public int updateProc(stockVO stockVO) {
		return stockDAO.updateProc(stockVO);
	}
	
	
	

}
