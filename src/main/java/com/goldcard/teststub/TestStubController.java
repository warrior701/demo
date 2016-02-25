package com.goldcard.teststub;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 用xml保存测试用例的测试桩
 * 
 * @author 1895
 *
 */
@Controller
public class TestStubController {

	private Logger log = LoggerFactory.getLogger(TestStubController.class);

	@RequestMapping
	public void testStub( HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setCharacterEncoding("utf-8");
		
		log.info(request.getRequestURI() + "?" + request.getParameterMap());

		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(this.getClass().getClassLoader().getResourceAsStream("stub.xml"));
		} catch (DocumentException e) {
			log.error(e.getMessage());
		}

		if(document == null) {
			response.getWriter().println("stub file error");
			return;
		}
		Node testcase = document.selectSingleNode("//case[@id='" +request.getRequestURI()+ "']");
		if(testcase == null){
			response.getWriter().println("test case not found");
			return;
		}
		
		
		@SuppressWarnings("unchecked")
		List<Node> params = testcase.selectNodes("./params/param");
		for(Node param : params) {
			String key = ((Element)param).attribute("id").getStringValue();
			String name = ((Element)param).attribute("name").getStringValue();
			String need = ((Element)param).attribute("need").getStringValue();
			
			if("1".equals(need) && request.getParameterMap().get(key) == null) {
				response.getWriter().println("param [ " + key + " ( " + name +" ) " + " ] not found");
				return;
			}
		}
		
		String casename = ((Element)testcase).attribute("name").getStringValue(); 
		
		log.info("成功调用接口：" + casename);
		
		Node result = testcase.selectSingleNode("./result");
		if(result == null) {
			response.getWriter().println("test case result not found");
			return;
		}
		log.info("接口返回：\n" + result.getText());
		
		response.getWriter().println(result.getText().replaceAll("\\s", ""));
		return;
	}

}
