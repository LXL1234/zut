package cn.gpa.zut.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import cn.gpa.zut.domain.DictPara;
import cn.gpa.zut.domain.DictRatio;
import cn.gpa.zut.domain.GpaDistr;
import cn.gpa.zut.domain.Paper;
import cn.gpa.zut.domain.Record;
import cn.gpa.zut.domain.User;
import cn.gpa.zut.domain.Userteam;
import cn.gpa.zut.service.IDictParaService;
import cn.gpa.zut.service.IDictRatioService;
import cn.gpa.zut.service.IGpaDistrService;
import cn.gpa.zut.service.IPaperService;
import cn.gpa.zut.service.IRecordService;
import cn.gpa.zut.service.IUserService;
import cn.gpa.zut.service.IUserteamService;
import cn.gpa.zut.utils.UUIDUtils;

@Controller
@RequestMapping("/paper")
@SessionAttributes(value = { "infoId", "Userteam","totalGpa","recordId","sort"})
// ①将ModelMap中属性名为的属性
//放到Session属性列表中，以便这个属性可以跨请求访问
public class PaperController {
	/*
	 * @RequestMapping("save") public String save(String save,ModelMap model) {
	 * UUIDUtils uuidUtils=new UUIDUtils(); String uuidString=uuidUtils.getUUID();
	 * model.addAttribute("infoid",uuidString); //②向ModelMap中添加一个属性 return "save"; }
	 */

	@Autowired
	private IPaperService paperService;
	@Autowired
	private IDictParaService dictParaService;
	@Autowired
	private IDictRatioService dictRatioService;
	@Autowired
	private IUserteamService userteamService;
	@Autowired
	private IUserService userService;
	@Autowired
	private IGpaDistrService gpaDistrService;
	@Autowired
	private IRecordService recordService;
   
	// 显示所有论文信息
	@RequestMapping("/findAll.do")
	public ModelAndView findAll() throws Exception {
		ModelAndView mv = new ModelAndView();
		List<Paper> papers = paperService.findAll();
		mv.addObject("paperList", papers);
		mv.setViewName("paper-list");
		return mv;
	}

	// 查找用户名下所有业绩记录并将信息编号存入session
	@RequestMapping("/findAllById.do")
	public ModelAndView findAllById(ModelMap model, @RequestParam(name = "id", required = true) String userId)
			throws Exception {
		ModelAndView mv = new ModelAndView();
		List<Paper> papers = paperService.findAllById(userId);
		UUIDUtils uuidUtils = new UUIDUtils();
		String uuidString = uuidUtils.getUUID();
		model.addAttribute("infoId", uuidString);
		System.out.println(uuidString);
		mv.addObject("paperList", papers);
		mv.setViewName("paper-list");
		return mv;
	}
	
	//信息详情页面
	@RequestMapping("/findByPaper.do")
	public ModelAndView findByPaper(ModelMap model, @RequestParam(name = "id", required = true) String paperId)
			throws Exception {
		ModelAndView mv = new ModelAndView();
		List<GpaDistr> gpaDistrs = gpaDistrService.findAllById(paperId);
		mv.addObject("gpaDistrsList", gpaDistrs);
		mv.setViewName("paper-details");
		return mv;
	}
	
	 //获取参数与系数信息并跳转信息添加页面
		@RequestMapping("/getSort.do")
		public ModelAndView getSort() throws Exception {
			ModelAndView mv = new ModelAndView();
			List<DictPara> dictParas = dictParaService.getSort("02");
			List<DictRatio> dictRatios = dictRatioService.getLev("02");
			List<Userteam> userteams = userteamService.findAll();
			mv.addObject("dictRatios", dictRatios);
			mv.addObject("dictParas", dictParas);
			mv.addObject("userteams", userteams);
			mv.setViewName("paper-add");
			return mv;
		}

	// 论文信息添加并将数据存入数据库
	@RequestMapping("/save.do")
	public String save(ModelMap model, @ModelAttribute("form") Paper paper,
			@RequestParam("userteam_name") String userteam_name, @RequestParam("userteam_num") Integer userteam_num)
			throws Exception {
		Double sumGpa = sumGPA(paper);
		paper.setPaperinfo_getGpa(sumGpa);
		paperService.save(paper);
		
		Userteam userteam = isExist(userteam_name, userteam_num);
		model.addAttribute("Userteam", userteam);
		model.addAttribute("totalGpa", sumGpa);
		String sort="paper";
		model.addAttribute("sort", sort);
		return "redirect:gpadistribute.do";
	}

	// 分配业绩点
	@RequestMapping("/gpadistribute.do")
	public ModelAndView gpaDistribute(Userteam userteam) throws Exception {
		ModelAndView mv = new ModelAndView();
		// mv.addObject("userteam", userteam);
		/*
		 * request.setAttribute("userteam_name", "zfy");
		 * request.setAttribute("userteam_num", "10");
		 */
		List<User> users = userService.findAll();
		mv.addObject("users", users);
		System.out.println("分配页面出现了");
		mv.setViewName("gpadistribute");
		return mv;
	}
	// 保存业绩点分配记录
		@RequestMapping("/gpasave.do")
		public String saveUsers(ModelMap model,@ModelAttribute("Userteam") Userteam uerUserteam, 
				SessionStatus sessionStatus,@ModelAttribute("form") Paper paper) {
			UUIDUtils uuidUtils = new UUIDUtils();
			String recordString = uuidUtils.getUUID();
			List<GpaDistr> gpaDistrs=paper.getGpaDistrs();
			for (int i = 0; i < gpaDistrs.size(); i++) {
				String uuidString = uuidUtils.getUUID();
				GpaDistr gpaDistr = paper.getGpaDistrs().get(i);
				gpaDistr.setGpadistr_id(uuidString);
				gpaDistr.setRecord_id(recordString);
				gpaDistr.setUserteam_id(uerUserteam.getUserteam_id());
				System.out.println(gpaDistr.getUser_Id());
				gpaDistrService.save(gpaDistr);
			}
			model.addAttribute("recordId", recordString);
			System.out.println("业绩点保存了");
			
			return "record";

		}
	
	/*
	 * // 保存业绩点分配记录
	 * 
	 * @RequestMapping("/gpasave.do")
	 * 
	 * @ResponseBody public String saveUsers(ModelMap model,@RequestBody Paper
	 * paper) { UUIDUtils uuidUtils = new UUIDUtils(); String recordString =
	 * uuidUtils.getUUID(); for (int i = 0; i < paper.getGpaDistrs().size(); i++) {
	 * String uuidString = uuidUtils.getUUID(); GpaDistr gpaDistr =
	 * paper.getGpaDistrs().get(i); gpaDistr.setGpadistr_id(uuidString);
	 * gpaDistr.setRecord_id(recordString); gpaDistr.setUserteam_id("003");
	 * System.out.println(gpaDistr.getUser_Id()); gpaDistrService.save(gpaDistr); }
	 * model.addAttribute("recordId", recordString); System.out.println("业绩点保存了");
	 * 
	 * return "nihao";
	 * 
	 * }
	 */
	//
	/*
	 * @RequestMapping("/record.do") public ModelAndView record() throws Exception {
	 * ModelAndView mv = new ModelAndView(); mv.setViewName("record"); return mv; }
	 * //凭证提交
	 * 
	 * @RequestMapping("/recordsave.do") public String saveRecord( Record record,
	 * HttpServletRequest request) { HttpSession session = request.getSession(true);
	 * recordService.save(record); System.out.println("nihao");
	 * session.removeAttribute("Userteam"); session.removeAttribute("infoId");
	 * session.removeAttribute("totalGpa"); session.removeAttribute("recordId");
	 * return "redirect:findAll.do";
	 * 
	 * }
	 */
		private  String fileUrl=null;
		private  String rfilename=null;

		@RequestMapping("/record.do")
		public ModelAndView record() throws Exception {
			ModelAndView mv = new ModelAndView();
			mv.setViewName("record");
			return mv;
		}

		// 凭证提交
		@RequestMapping("/recordsave.do")
		//@ResponseBody
		public String saveRecord(MultipartFile file, Record record, HttpServletRequest request) throws IllegalStateException, IOException {
			HttpSession session = request.getSession(true);
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSS");
			String res = sdf.format(new Date());
			// uploads文件夹位置
			String rootPath = request.getSession().getServletContext().getRealPath("resource/uploads/");
			// 原始名称
			String originalFileName = file.getOriginalFilename();
			// 新文件名
			String newFileName = "sliver" + res + originalFileName.substring(originalFileName.lastIndexOf("."));
			// 创建年月文件夹
			Calendar date = Calendar.getInstance();
			File dateDirs = new File(date.get(Calendar.YEAR) + File.separator + (date.get(Calendar.MONTH) + 1));
			// 新文件
			File newFile = new File(rootPath + File.separator + dateDirs + File.separator + newFileName);
			// 判断目标文件所在目录是否存在
			if (!newFile.getParentFile().exists()) {
				// 如果目标文件所在的目录不存在，则创建父目录
				newFile.getParentFile().mkdirs();
			}
			System.out.println(newFile);
			// 将内存中的数据写入磁盘
			file.transferTo(newFile);
			//文件名
			rfilename=newFileName;
			// 完整的url
			
			fileUrl = date.get(Calendar.YEAR) + "/" + (date.get(Calendar.MONTH) + 1) + "/" + newFileName;
			System.out.println(fileUrl);
			session.removeAttribute("Userteam");
			session.removeAttribute("infoId");
			session.removeAttribute("totalGpa");
			session.removeAttribute("recordId");
			System.out.println(fileUrl);
			record.setRecord_proof(fileUrl);
			recordService.save(record);
			//return "redirect:findAll.do";
			return "redirect:downshow.do";

		}
		
		@RequestMapping("/downshow.do")
		public ModelAndView down() throws Exception {
			ModelAndView mv = new ModelAndView();
			mv.setViewName("fail");
			return mv;
		}
		
		 @RequestMapping("/down.do")  
	     public void down(HttpServletRequest request,HttpServletResponse response) throws Exception{  
	        
	         String fileName = request.getSession().getServletContext().getRealPath("resource/uploads/")+fileUrl;  
	         
	         InputStream bis = new BufferedInputStream(new FileInputStream(new File(fileName)));  
	           
	         String filename = rfilename ;  
	         
	         filename = URLEncoder.encode(rfilename,"UTF-8");  
	         
	         response.addHeader("Content-Disposition", "attachment;filename=" + filename);    
	             
	         response.setContentType("multipart/form-data");   
	         
	         BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());  
	         int len = 0;  
	         while((len = bis.read()) != -1){  
	             out.write(len);  
	             out.flush();  
	         }  
	         out.close();  
	     }  
	
	// 计算业绩点
	public Double sumGPA(Paper paper) throws Exception {

		int gpa = 0;
		Double ratio = 0.0;
		Double sumgpa = 0.0;
		List<DictPara> dictParas = dictParaService.getSort("02");
		List<DictRatio> dictRatios = dictRatioService.getLev("02");
		paper.getPaperinfo_Lev();
		paper.getPaperinfo_orglev();
		for (Iterator iterators = dictParas.iterator(); iterators.hasNext();) {
			DictPara dictPara = (DictPara) iterators.next();// 获取当前遍历的元素，指定为Example对象
			String name = dictPara.getDictpara_id();
			if (paper.getPaperinfo_Lev().equals(name)) {
				gpa = dictPara.getDictpara_gpa();
			}
		}
		for (Iterator iterators = dictRatios.iterator(); iterators.hasNext();) {
			DictRatio dictRatio = (DictRatio) iterators.next();// 获取当前遍历的元素，指定为Example对象
			String name = dictRatio.getDictratio_id();
			if (paper.getPaperinfo_orglev().equals(name)) {
				ratio = dictRatio.getDictratio_ratio();
			}
		}
		sumgpa = gpa * ratio;
		return gpa * ratio;
	}

	// 判断回传的用户组数据是否存在
	public Userteam isExist(String userteam_name, Integer userteam_num) throws Exception {
		List<Userteam> userteams = userteamService.findAll();
		for (Iterator iterators = userteams.iterator(); iterators.hasNext();) {
			Userteam userteam = (Userteam) iterators.next();// 获取当前遍历的元素，指定为Example对象
			if (userteam.getUserteam_name().equals(userteam_name)) {
				return userteam;
			}
		}
		Userteam userteam = new Userteam();
		UUIDUtils uuidUtils = new UUIDUtils();
		String uuidString = uuidUtils.getUUID();
		userteam.setUserteam_id(uuidString);
		userteam.setUserteam_name(userteam_name);
		userteam.setUserteam_num(userteam_num);
		userteamService.save(userteam);
		return userteam;
	}
}
